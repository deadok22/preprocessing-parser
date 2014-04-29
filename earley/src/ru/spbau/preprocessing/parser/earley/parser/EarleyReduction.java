package ru.spbau.preprocessing.parser.earley.parser;

import com.google.common.base.Objects;
import ru.spbau.preprocessing.parser.earley.grammar.EarleySymbol;
import ru.spbau.preprocessing.parser.earley.grammar.EarleyTerminal;

/**
 * A reduction for an Earley item.
 * It is either a completed Earley item or a consumed terminal.
 */
class EarleyReduction {
  private final EarleyItem myCompletedItem;
  private final EarleyTerminal<?> myTerminal;
  private final EarleyChartColumn myStartColumn;

  EarleyReduction(EarleyItem reductionItem) {
    this(reductionItem, null, reductionItem.getStartColumn());
  }

  EarleyReduction(EarleyTerminal<?> terminal, EarleyChartColumn chartColumn) {
    this(null, terminal, chartColumn);
  }

  private EarleyReduction(EarleyItem completedItem, EarleyTerminal<?> terminal, EarleyChartColumn startColumn) {
    myCompletedItem = completedItem;
    myTerminal = terminal;
    myStartColumn = startColumn;
  }


  public EarleyItem getCompletedItem() {
    return myCompletedItem;
  }

  public EarleyTerminal<?> getTerminal() {
    return myTerminal;
  }

  public EarleyChartColumn getStartColumn() {
    return myStartColumn;
  }

  /**
   * Each reduction corresponds either to a completed nonterminal or to a terminal.
   * @return EarleySymbol which is represented by this reduction.
   */
  public EarleySymbol getSymbol() {
    return myTerminal != null ? myTerminal : myCompletedItem.getSymbol();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    EarleyReduction that = (EarleyReduction) o;
    return Objects.equal(myCompletedItem, that.myCompletedItem) &&
            Objects.equal(myStartColumn, that.myStartColumn) &&
            Objects.equal(myTerminal, that.myTerminal);
  }

  @Override
  public int hashCode() {
    int result = myCompletedItem != null ? myCompletedItem.hashCode() : 0;
    result = 31 * result + (myTerminal != null ? myTerminal.hashCode() : 0);
    result = 31 * result + myStartColumn.hashCode();
    return result;
  }
}
