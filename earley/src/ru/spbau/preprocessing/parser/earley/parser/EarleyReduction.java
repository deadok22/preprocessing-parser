package ru.spbau.preprocessing.parser.earley.parser;

import com.google.common.base.Objects;
import ru.spbau.preprocessing.parser.earley.grammar.EarleySymbol;

/**
 * A reduction for an Earley item.
 * It is either a completed Earley item or a consumed terminal.
 */
class EarleyReduction {
  private final EarleyItem myCompletedItem;
  private final EarleyTerminalMatch<?> myTerminalMatch;
  private final EarleyChartColumn myStartColumn;

  EarleyReduction(EarleyItem reductionItem) {
    this(reductionItem, null, reductionItem.getStartColumn());
  }

  EarleyReduction(EarleyTerminalMatch<?> terminalMatch, EarleyChartColumn chartColumn) {
    this(null, terminalMatch, chartColumn);
  }

  private EarleyReduction(EarleyItem completedItem, EarleyTerminalMatch<?> terminalMatch, EarleyChartColumn startColumn) {
    myCompletedItem = completedItem;
    myTerminalMatch = terminalMatch;
    myStartColumn = startColumn;
  }


  public EarleyItem getCompletedItem() {
    return myCompletedItem;
  }

  public EarleyTerminalMatch<?> getTerminalMatch() {
    return myTerminalMatch;
  }

  public EarleyChartColumn getStartColumn() {
    return myStartColumn;
  }

  /**
   * Each reduction corresponds either to a completed nonterminal or to a terminal.
   * @return EarleySymbol which is represented by this reduction.
   */
  public EarleySymbol getSymbol() {
    return myTerminalMatch != null ? myTerminalMatch.getTerminal() : myCompletedItem.getSymbol();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    EarleyReduction that = (EarleyReduction) o;
    return Objects.equal(myCompletedItem, that.myCompletedItem) &&
            Objects.equal(myStartColumn, that.myStartColumn) &&
            Objects.equal(myTerminalMatch, that.myTerminalMatch);
  }

  @Override
  public int hashCode() {
    int result = myCompletedItem != null ? myCompletedItem.hashCode() : 0;
    result = 31 * result + (myTerminalMatch != null ? myTerminalMatch.hashCode() : 0);
    result = 31 * result + myStartColumn.hashCode();
    return result;
  }
}
