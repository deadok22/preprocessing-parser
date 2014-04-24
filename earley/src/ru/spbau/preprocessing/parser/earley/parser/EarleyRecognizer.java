package ru.spbau.preprocessing.parser.earley.parser;

import com.google.common.base.Objects;
import ru.spbau.preprocessing.lexer.lexemegraph.Lexeme;
import ru.spbau.preprocessing.lexer.lexemegraph.LexemeGraphForkNode;
import ru.spbau.preprocessing.lexer.lexemegraph.LexemeGraphLangNode;
import ru.spbau.preprocessing.lexer.lexemegraph.LexemeGraphVisitor;
import ru.spbau.preprocessing.parser.earley.grammar.EarleyGrammar;
import ru.spbau.preprocessing.parser.earley.grammar.EarleyProduction;
import ru.spbau.preprocessing.parser.earley.grammar.EarleySymbol;
import ru.spbau.preprocessing.parser.earley.grammar.EarleyTerminal;

import java.util.List;
import java.util.Set;

class EarleyRecognizer implements LexemeGraphVisitor {
  private final EarleyGrammar myGrammar;
  private final EarleyChart myChart;

  EarleyRecognizer(EarleyGrammar grammar) {
    myGrammar = grammar;
    myChart = new EarleyChart();
    createFirstChartColumn();
  }

  private void createFirstChartColumn() {
    EarleySymbol startSymbol = myGrammar.getStartSymbol();
    EarleyChartColumn firstColumn = myChart.newColumn();
    Set<EarleyProduction> productions = myGrammar.getProductions(startSymbol.getName());
    for (EarleyProduction production : productions) {
      firstColumn.addItem(production);
    }
  }

  @Override
  public void visitForkNode(LexemeGraphForkNode forkNode) {
    //TODO implement
    throw new UnsupportedOperationException("Not implemented.");
  }

  @Override
  public void visitLangNode(LexemeGraphLangNode langNode) {
    @SuppressWarnings("unchecked") List<Lexeme<?>> lexemes = (List<Lexeme<?>>) langNode.getLexemes();
    for (Lexeme<?> lexeme : lexemes) {
      doEarleyStep(lexeme);
    }
  }

  public EarleyChart completeChart() {
    predict(myChart.lastColumn());
    complete(myChart.lastColumn());
    return myChart;
  }

  private void doEarleyStep(Lexeme<?> lexeme) {
    EarleyTerminal<Object> terminal = new EarleyTerminal<Object>(lexeme);
    EarleyChartColumn currentColumn = myChart.lastColumn();
    EarleyChartColumn nextColumn = myChart.newColumn();

    predict(currentColumn);
    scan(terminal, currentColumn, nextColumn);
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
    for (EarleyProduction production : productions) {
      EarleyItem newItem = currentColumn.addItem(production);
      if (newItem != null) {
        predictForItem(newItem, currentColumn);
      }
    }
  }

  private void scan(EarleyTerminal<?> terminal, EarleyChartColumn currentColumn, EarleyChartColumn nextColumn) {
    for (EarleyItem item : currentColumn) {
      if (Objects.equal(terminal, item.getNextExpectedSymbol())) {
        nextColumn.addItem(item, terminal);
      }
    }
  }

  private void complete(EarleyChartColumn nextColumn) {
    for (EarleyItem item : nextColumn) {
      completeForItem(item, nextColumn);
    }
  }

  private void completeForItem(EarleyItem item, EarleyChartColumn nextColumn) {
    EarleyChartColumn startColumn = item.getStartColumn();
    if (startColumn == null || !item.isComplete()) return;
    for (EarleyItem startColumnItem : startColumn) {
      if (Objects.equal(item.getSymbol(), startColumnItem.getNextExpectedSymbol())) {
        EarleyItem newItem = nextColumn.addItem(startColumnItem, item);
        if (newItem != null) {
          completeForItem(newItem, nextColumn);
        }
      }
    }
  }
}
