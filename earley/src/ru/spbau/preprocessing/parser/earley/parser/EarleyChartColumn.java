package ru.spbau.preprocessing.parser.earley.parser;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
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
   * @return added item or null if the column was not modified.
   */
  public EarleyItem addItem(EarleyProduction production) {
    EarleyItem earleyItem = new EarleyItem(production, this);
    return myItems.put(earleyItem, new EarleyItemDescriptor(null)) ? earleyItem : null;
  }

  /**
   * Add an item produced from a predecessor via token consuming.
   *
   * @param predecessor the predecessor of this item.
   * @param terminal a terminal consumed.
   * @return added item or null if the column was not modified.
   */
  public EarleyItem addItem(EarleyItem predecessor, EarleyTerminal<?> terminal) {
    EarleyItem newItem = predecessor.advanceWith(terminal);
    return myItems.put(newItem, new EarleyItemDescriptor(predecessor)) ? newItem : null;
  }

  /**
   * Add an item produced from a predecessor via reduction.
   *
   * @param predecessor the predecessor of this item.
   * @param reduction a reduction item.
   * @return added item or null if the column was not modified.
   */
  public EarleyItem addItem(EarleyItem predecessor, EarleyItem reduction) {
    EarleyItem newItem = predecessor.advanceWith(reduction.getSymbol());
    EarleyItemDescriptor newItemDescriptor = new EarleyItemDescriptor(predecessor);
    if (myItems.containsEntry(newItem, newItemDescriptor)) {
      Set<EarleyItemDescriptor> descriptors = myItems.get(newItem);
      for (EarleyItemDescriptor descriptor : descriptors) {
        if (newItemDescriptor.equals(descriptor)) {
          return descriptor.addReductionItem(reduction) ? newItem : null;
        }
      }
    }
    else {
      newItemDescriptor.addReductionItem(reduction);
      return myItems.put(newItem, newItemDescriptor) ? newItem : null;
    }
    return null;
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
}
