package ru.spbau.preprocessing.parser.earley.parser;

import com.google.common.base.Objects;
import ru.spbau.preprocessing.parser.earley.grammar.EarleyNonTerminal;
import ru.spbau.preprocessing.parser.earley.grammar.EarleyProduction;
import ru.spbau.preprocessing.parser.earley.grammar.EarleySymbol;

import java.util.List;

class EarleyItem {
  private boolean myPredictionDone = false;
  private final EarleyProduction myProduction;
  private final int myIndexInProduction;
  private final EarleyChartColumn myStartColumn;

  EarleyItem(EarleyProduction production, EarleyChartColumn startColumn) {
    this(production, 0, startColumn);
  }

  private EarleyItem(EarleyProduction production, int indexInProduction, EarleyChartColumn startColumn) {
    myProduction = production;
    myIndexInProduction = indexInProduction;
    myStartColumn = startColumn;
  }

  public EarleyProduction getProduction() {
    return myProduction;
  }

  public int getIndexInProduction() {
    return myIndexInProduction;
  }

  public EarleyNonTerminal getSymbol() {
    return myProduction.getLeft();
  }

  /**
   * Get the last symbol successfully matched in this Earley item.
   * That is the symbol to the left of the dot.
   * For example, it will return 'X' for an item like "E ::= X X . A".
   *
   * @return the last symbol successfully matched in this Earley item
   *          or null if this item is in initial state.
   */
  public EarleySymbol getLastMatchedSymbol() {
    return myIndexInProduction != 0 ? myProduction.getRight().get(myIndexInProduction - 1) : null;
  }

  public EarleySymbol getNextExpectedSymbol() {
    List<EarleySymbol> productionSymbols = myProduction.getRight();
    return myIndexInProduction < productionSymbols.size() ? productionSymbols.get(myIndexInProduction) : null;
  }

  public EarleyChartColumn getStartColumn() {
    return myStartColumn;
  }

  public boolean isComplete() {
    return getNextExpectedSymbol() == null;
  }

  public void predictionIsDone() {
    myPredictionDone = true;
  }

  public boolean isPredictionDone() {
    return myPredictionDone;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    EarleyItem that = (EarleyItem) o;

    if (myIndexInProduction != that.myIndexInProduction) return false;
    if (!myProduction.equals(that.myProduction)) return false;
    if (!myStartColumn.equals(that.myStartColumn)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = myProduction.hashCode();
    result = 31 * result + myIndexInProduction;
    result = 31 * result + myStartColumn.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return myIndexInProduction + " in " + myProduction;
  }

  EarleyItem advanceWith(EarleySymbol symbol) {
    assert Objects.equal(symbol, getNextExpectedSymbol());
    return new EarleyItem(myProduction, myIndexInProduction + 1, myStartColumn);
  }
}
