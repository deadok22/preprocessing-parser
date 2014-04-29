package ru.spbau.preprocessing.parser.earley.parser;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import ru.spbau.preprocessing.api.conditions.PresenceCondition;
import ru.spbau.preprocessing.api.conditions.PresenceConditionFactory;
import ru.spbau.preprocessing.lexer.lexemegraph.*;
import ru.spbau.preprocessing.parser.earley.grammar.EarleyGrammar;
import ru.spbau.preprocessing.parser.earley.grammar.EarleyProduction;
import ru.spbau.preprocessing.parser.earley.grammar.EarleySymbol;
import ru.spbau.preprocessing.parser.earley.grammar.EarleyTerminal;

import java.util.List;
import java.util.Set;

class EarleyRecognizer implements LexemeGraphVisitor {
  private final PresenceConditionFactory myPresenceConditionFactory;
  private final EarleyGrammar myGrammar;
  private final EarleyChart myChart;

  EarleyRecognizer(PresenceConditionFactory presenceConditionFactory, EarleyGrammar grammar) {
    this(presenceConditionFactory, grammar, new EarleyChart());
    createFirstChartColumn();
  }

  private EarleyRecognizer(PresenceConditionFactory presenceConditionFactory, EarleyGrammar grammar, EarleyChart chart) {
    myPresenceConditionFactory = presenceConditionFactory;
    myGrammar = grammar;
    myChart = chart;
  }

  private void createFirstChartColumn() {
    EarleySymbol startSymbol = myGrammar.getStartSymbol();
    EarleyChartColumn firstColumn = myChart.newColumn();
    Set<EarleyProduction> productions = myGrammar.getProductions(startSymbol.getName());
    for (EarleyProduction production : productions) {
      firstColumn.addItem(production, myPresenceConditionFactory.getTrue());
    }
  }

  @Override
  public void visitForkNode(LexemeGraphForkNode forkNode) {
    EarleyChartColumn columnBeforeFork = myChart.lastColumn();
    //TODO create a subchart for each alternative branch
    //TODO add a new chart column to this myChart filling it with join of items from all branches.
    //TODO BUT be sure not to complete items from one branch with items from another!
    List<EarleyRecognizer> forkRecognizers = Lists.newArrayListWithExpectedSize(forkNode.getChildren().size());
    for (LexemeGraphNode lexemeGraphNode : forkNode.getChildren()) {
      EarleyRecognizer recognizer = new EarleyRecognizer(myPresenceConditionFactory, myGrammar, myChart.createSubChart());
      forkRecognizers.add(recognizer);
      lexemeGraphNode.accept(recognizer);
    }
    EarleyChartColumn columnAfterFork = myChart.newColumn();
    for (EarleyRecognizer forkRecognizer : forkRecognizers) {
      EarleyChartColumn lastForkColumn = forkRecognizer.myChart.lastColumn();
      if (lastForkColumn != columnBeforeFork) {
        columnAfterFork.addAllFrom(lastForkColumn);
      }
    }
  }

  @Override
  public void visitLangNode(LexemeGraphLangNode langNode) {
    @SuppressWarnings("unchecked") List<Lexeme<?>> lexemes = (List<Lexeme<?>>) langNode.getLexemes();
    for (Lexeme<?> lexeme : lexemes) {
      doEarleyStep(lexeme, langNode.getPresenceCondition());
    }
  }

  public EarleyChart completeChart() {
    predict(myChart.lastColumn());
    complete(myChart.lastColumn());
    return myChart;
  }

  private void doEarleyStep(Lexeme<?> lexeme, PresenceCondition presenceCondition) {
    EarleyTerminal<Object> terminal = new EarleyTerminal<Object>(lexeme.getType());
    if (myGrammar.isIgnoredSymbol(terminal)) return;

    EarleyChartColumn currentColumn = myChart.lastColumn();
    EarleyChartColumn nextColumn = myChart.newColumn();

    predict(currentColumn);
    scan(terminal, currentColumn, nextColumn, presenceCondition);
    complete(nextColumn);
  }

  private void predict(EarleyChartColumn currentColumn) {
    for (EarleyItem item : currentColumn) {
      predictForItem(item, currentColumn);
    }
  }

  private void predictForItem(EarleyItem item, EarleyChartColumn currentColumn) {
    EarleySymbol nextExpectedSymbol = item.getNextExpectedSymbol();
    if (nextExpectedSymbol == null || nextExpectedSymbol.isTerminal()) return;
    Set<EarleyProduction> productions = myGrammar.getProductions(nextExpectedSymbol.getName());
    PresenceCondition presenceCondition = getOrOfPresenceConditions(item, currentColumn);
    for (EarleyProduction production : productions) {
      EarleyItem newItem = currentColumn.addItem(production, presenceCondition);
      if (newItem != null) {
        predictForItem(newItem, currentColumn);
      }
    }
  }

  private void scan(EarleyTerminal<?> terminal, EarleyChartColumn currentColumn, EarleyChartColumn nextColumn, PresenceCondition presenceCondition) {
    for (EarleyItem item : currentColumn) {
      if (Objects.equal(terminal, item.getNextExpectedSymbol())) {
        PresenceCondition itemPresenceCondition = getOrOfPresenceConditions(item, currentColumn);
        PresenceCondition newItemPresenceCondition = presenceCondition.and(itemPresenceCondition);
        nextColumn.addItem(item, terminal, currentColumn, newItemPresenceCondition);
      }
    }
  }

  private void complete(EarleyChartColumn nextColumn) {
    for (EarleyItem item : nextColumn) {
      completeForItem(item, nextColumn);
    }
  }

  private void completeForItem(EarleyItem item, EarleyChartColumn nextColumn) {
    PresenceCondition itemPresenceCondition = getOrOfPresenceConditions(item, nextColumn);
    EarleyChartColumn startColumn = item.getStartColumn();
    if (startColumn == null || !item.isComplete()) return;
    for (EarleyItem startColumnItem : startColumn) {
      if (Objects.equal(item.getSymbol(), startColumnItem.getNextExpectedSymbol())) {
        PresenceCondition startColumnItemPresenceCondition = getOrOfPresenceConditions(startColumnItem, startColumn);
        PresenceCondition newItemPresenceCondition = itemPresenceCondition.and(startColumnItemPresenceCondition);
        EarleyItem newItem = nextColumn.addItem(startColumnItem, item, newItemPresenceCondition);
        if (newItem != null) {
          completeForItem(newItem, nextColumn);
        }
      }
    }
  }

  private PresenceCondition getOrOfPresenceConditions(EarleyItem item, EarleyChartColumn column) {
    PresenceCondition presenceCondition = myPresenceConditionFactory.getFalse();
    Set<EarleyItemDescriptor> descriptors = column.getDescriptors(item);
    for (EarleyItemDescriptor descriptor : descriptors) {
      presenceCondition = presenceCondition.or(descriptor.getPresenceCondition());
    }
    return presenceCondition;
  }
}
