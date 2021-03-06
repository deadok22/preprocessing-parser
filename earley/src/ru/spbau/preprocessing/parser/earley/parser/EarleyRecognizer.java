package ru.spbau.preprocessing.parser.earley.parser;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
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
    this(presenceConditionFactory, grammar, new EarleyChart(presenceConditionFactory.getTrue()));
    EarleySymbol startSymbol = myGrammar.getStartSymbol();
    EarleyChartColumn firstColumn = myChart.newColumn();
    Set<EarleyProduction> productions = myGrammar.getProductions(startSymbol.getName());
    for (EarleyProduction production : productions) {
      firstColumn.addItem(production, myPresenceConditionFactory.getTrue());
    }
  }

  private EarleyRecognizer(PresenceConditionFactory presenceConditionFactory, EarleyGrammar grammar, EarleyChart chart) {
    myPresenceConditionFactory = presenceConditionFactory;
    myGrammar = grammar;
    myChart = chart;
  }

  @Override
  public void visitForkNode(LexemeGraphForkNode forkNode) {
    EarleyChartColumn columnBeforeFork = myChart.lastColumn();
    predict(columnBeforeFork);
    //TODO be sure not to complete items from one branch with items from another!
    List<EarleyRecognizer> forkRecognizers = Lists.newArrayListWithExpectedSize(forkNode.getChildren().size());
    for (LexemeGraphNode lexemeGraphNode : forkNode.getChildren()) {
      PresenceCondition branchBasePresenceCondition = columnBeforeFork.getPresenceCondition().and(lexemeGraphNode.getPresenceCondition());
      EarleyChart subChart = myChart.createSubChart(branchBasePresenceCondition);
      EarleyRecognizer recognizer = new EarleyRecognizer(myPresenceConditionFactory, myGrammar, subChart);
      forkRecognizers.add(recognizer);
      lexemeGraphNode.accept(recognizer);
    }
    Set<EarleyChartColumn> forkLastColumns =
            Sets.newLinkedHashSet(Lists.transform(forkRecognizers, new Function<EarleyRecognizer, EarleyChartColumn>() {
              @Override
              public EarleyChartColumn apply(EarleyRecognizer forkRecognizer) {
                return forkRecognizer.myChart.lastColumn();
              }
            }));

    // this means we have a single fork branch and it is degenerate - no new columns were created
    if (forkLastColumns.size() == 1 && forkLastColumns.iterator().next() == columnBeforeFork) return;

    // create a merge column for all of fork's branches
    EarleyChartColumn columnAfterFork = myChart.newColumn();
    for (EarleyChartColumn forkLastColumn : forkLastColumns) {
      columnAfterFork.addAllFrom(forkLastColumn);
    }

    // determine whether fork branches coverage is exhaustive (i.e. at least one branch is always selected)
    PresenceCondition orOfForkBranches = myPresenceConditionFactory.getFalse();
    for (EarleyRecognizer forkRecognizer : forkRecognizers) {
      PresenceCondition branchBasePresenceCondition = forkRecognizer.myChart.getBasePresenceCondition();
      orOfForkBranches = orOfForkBranches.or(branchBasePresenceCondition);
    }
    if (orOfForkBranches.value() == PresenceCondition.Value.TRUE || myChart.getBasePresenceCondition().equals(orOfForkBranches)) return;

    // fork branches coverage is not exhaustive, it's possible that none of the branches is selected
    // add items from column before fork and-ing their presence conditions with not of orOfForkBranches to handle this case
    PresenceCondition notOrOfForkBranches = orOfForkBranches.not();
    columnAfterFork.addAllFrom(columnBeforeFork, notOrOfForkBranches);
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
    //noinspection unchecked
    EarleyTerminalMatch<Object> terminalMatch = new EarleyTerminalMatch<Object>(new EarleyTerminal<Object>(lexeme.getType()), (Lexeme<Object>) lexeme);
    if (myGrammar.isIgnoredSymbol(terminalMatch.getTerminal())) return;

    EarleyChartColumn currentColumn = myChart.lastColumn();
    EarleyChartColumn nextColumn = myChart.newColumn();

    predict(currentColumn);
    scan(terminalMatch, currentColumn, nextColumn, presenceCondition);
    complete(nextColumn);
  }

  private void predict(EarleyChartColumn currentColumn) {
    for (EarleyItem item : currentColumn) {
      predictForItem(item, currentColumn);
    }
  }

  private void predictForItem(EarleyItem item, EarleyChartColumn currentColumn) {
    if (item.isPredictionDone()) return;
    item.predictionIsDone();
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

  private void scan(EarleyTerminalMatch<Object> terminalMatch, EarleyChartColumn currentColumn, EarleyChartColumn nextColumn, PresenceCondition presenceCondition) {
    for (EarleyItem item : currentColumn) {
      if (Objects.equal(terminalMatch.getTerminal(), item.getNextExpectedSymbol())) {
        PresenceCondition itemPresenceCondition = getOrOfPresenceConditions(item, currentColumn);
        PresenceCondition newItemPresenceCondition = presenceCondition.and(itemPresenceCondition);
        nextColumn.addItem(item, terminalMatch, currentColumn, newItemPresenceCondition);
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
