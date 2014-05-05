package ru.spbau.preprocessing.parser.earley.parser;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import ru.spbau.preprocessing.api.conditions.PresenceCondition;
import ru.spbau.preprocessing.parser.earley.grammar.EarleyProduction;
import ru.spbau.preprocessing.parser.earley.grammar.EarleyTerminal;

import java.util.Iterator;
import java.util.Set;

class EarleyChartColumn implements Iterable<EarleyItem> {
  private final EarleyChart myChart;

  private LinkedHashMultimap<EarleyItem, EarleyItemDescriptor> myItems = LinkedHashMultimap.create();

  EarleyChartColumn(EarleyChart chart) {
    myChart = chart;
  }

  public Set<EarleyItemDescriptor> getDescriptors(EarleyItem item) {
    return myItems.get(item);
  }

  /**
   * Add an item in initial state.
   *
   * @param production a production for this item.
   * @param presenceCondition
   * @return added item or null if the column was not modified.
   */
  public EarleyItem addItem(EarleyProduction production, PresenceCondition presenceCondition) {
    EarleyItem newItem = new EarleyItem(production, this);
    EarleyItemDescriptor newItemDescriptor = new EarleyItemDescriptor(null, presenceCondition);
    return addItem(newItem, newItemDescriptor);
  }

  /**
   * Add an item produced from a predecessor via token consuming.
   *
   * @param predecessor the predecessor of this item.
   * @param terminal a terminal consumed.
   * @param previousColumn
   * @param presenceCondition
   * @return added item or null if the column was not modified.
   */
  public EarleyItem addItem(EarleyItem predecessor, EarleyTerminal<?> terminal, EarleyChartColumn previousColumn, PresenceCondition presenceCondition) {
    EarleyItem newItem = predecessor.advanceWith(terminal);
    EarleyItemDescriptor newItemDescriptor = new EarleyItemDescriptor(predecessor, presenceCondition);
    newItemDescriptor.addReduction(terminal, previousColumn);
    return addItem(newItem, newItemDescriptor);
  }

  /**
   * Add an item produced from a predecessor via reduction.
   *
   * @param predecessor the predecessor of this item.
   * @param reduction a reduction item.
   * @param presenceCondition
   * @return added item or null if the column was not modified.
   */
  public EarleyItem addItem(EarleyItem predecessor, EarleyItem reduction, PresenceCondition presenceCondition) {
    EarleyItem newItem = predecessor.advanceWith(reduction.getSymbol());
    EarleyItemDescriptor newItemDescriptor = new EarleyItemDescriptor(predecessor, presenceCondition);
    newItemDescriptor.addReductionItem(reduction);
    return addItem(newItem, newItemDescriptor);
  }

  private EarleyItem addItem(EarleyItem newItem, EarleyItemDescriptor newItemDescriptor) {
    if (!myItems.put(newItem, newItemDescriptor)) {
      Set<EarleyItemDescriptor> descriptors = myItems.get(newItem);
      for (EarleyItemDescriptor descriptor : descriptors) {
        if (newItemDescriptor.equals(descriptor)) {
          boolean changesWereMade = descriptor.expandPresenceCondition(newItemDescriptor.getPresenceCondition());
          changesWereMade |= descriptor.addReductionItems(newItemDescriptor.getReductions());
          return changesWereMade ? newItem : null;
        }
      }
      return null;
    }
    return newItem;
  }

  /**
   * @return a concurrent modification safe iterator.
   */
  @Override
  public Iterator<EarleyItem> iterator() {
    return ImmutableSet.copyOf(myItems.keySet()).iterator();
  }

  public EarleyChartColumn previousColumn() {
    return myChart.getColumnBefore(this);
  }

  //TODO remove this. Relative column positions should be determined some other way.
  public boolean isBefore(EarleyChartColumn column) {
    if (column == this) return false;
    while ((column = column.previousColumn()) != null) {
      if (column == this) {
        return true;
      }
    }
    return false;
  }

  public void addAllFrom(EarleyChartColumn otherColumn) {
    for (EarleyItem item : otherColumn) {
      for (EarleyItemDescriptor descriptor : otherColumn.getDescriptors(item)) {
        addItem(item, descriptor);
      }
    }
  }

  public PresenceCondition getPresenceCondition() {
    return myChart.getBasePresenceCondition();
  }
}
