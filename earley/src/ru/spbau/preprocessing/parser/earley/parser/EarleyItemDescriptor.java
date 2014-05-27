package ru.spbau.preprocessing.parser.earley.parser;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import ru.spbau.preprocessing.api.conditions.PresenceCondition;

import java.util.Set;

/**
 * Describes the way an Earley item was produced.
 *
 * An Earley item can be produced via
 * a) scanning - see b)
 * b) reducing - reduction items set contains one or more items
 *               which were used to advance this Earley item's production index;
 * c) creating - reduction items set is empty, predecessor is null.
 *
 * This class's equals() and hashCode() are to ignore reduction items.
 */
 class EarleyItemDescriptor {
  private final EarleyItem myPredecessor;
  private final Set<EarleyReduction> myReductionItems;
  private PresenceCondition myPresenceCondition;

  EarleyItemDescriptor(EarleyItem predecessor, PresenceCondition presenceCondition) {
    this(predecessor, Sets.<EarleyReduction>newLinkedHashSet(), presenceCondition);
  }

  EarleyItemDescriptor(EarleyItemDescriptor descriptor, PresenceCondition andPresenceCondition) {
    this(descriptor.getPredecessor(),
            Sets.newLinkedHashSet(descriptor.getReductions()),
            andPresenceCondition == null ? descriptor.getPresenceCondition() : descriptor.getPresenceCondition().and(andPresenceCondition));
  }

  private EarleyItemDescriptor(EarleyItem predecessor, Set<EarleyReduction> reductionItems, PresenceCondition presenceCondition) {
    myPredecessor = predecessor;
    myReductionItems = reductionItems;
    myPresenceCondition = presenceCondition;
  }

  public boolean addReductionItem(EarleyItem reductionItem) {
    return addReduction(new EarleyReduction(reductionItem));
  }

  public boolean addReduction(EarleyTerminalMatch<?> terminalMatch, EarleyChartColumn column) {
    return addReduction(new EarleyReduction(terminalMatch, column));
  }

  public boolean addReduction(EarleyReduction reduction) {
    assert Objects.equal(myPredecessor.getNextExpectedSymbol(), reduction.getSymbol());
    return myReductionItems.add(reduction);
  }

  public boolean addReductionItems(Iterable<EarleyReduction> reductionItems) {
    boolean reductionSetChanged = false;
    for (EarleyReduction reductionItem : reductionItems) {
      reductionSetChanged |= addReduction(reductionItem);
    }
    return reductionSetChanged;
  }

  /**
   * Alter a presence condition of this object to indicate that this item can also be created under
   * different presence condition.
   * @param expansion a new presence condition under which this item can be created.
   * @return true if this item's presence condition has been changed.
   */
  public boolean expandPresenceCondition(PresenceCondition expansion) {
    PresenceCondition oldPresenceCondition = myPresenceCondition;
    myPresenceCondition = myPresenceCondition.or(expansion);
    return !Objects.equal(oldPresenceCondition, myPresenceCondition);
  }

  public EarleyItem getPredecessor() {
    return myPredecessor;
  }

  public Set<EarleyReduction> getReductions() {
    return myReductionItems;
  }

  public PresenceCondition getPresenceCondition() {
    return myPresenceCondition;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    EarleyItemDescriptor that = (EarleyItemDescriptor) o;
    return !(myPredecessor != null ? !myPredecessor.equals(that.myPredecessor) : that.myPredecessor != null);
  }

  @Override
  public int hashCode() {
    return myPredecessor != null ? myPredecessor.hashCode() : 0;
  }
}
