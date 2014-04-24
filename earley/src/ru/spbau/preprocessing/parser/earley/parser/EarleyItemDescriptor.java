package ru.spbau.preprocessing.parser.earley.parser;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Describes the way an Earley item was produced.
 *
 * An Earley item can be produced via
 * a) scanning - reduction items set is empty;
 * b) reducing - reduction items set contains one or more items
 *               which were used to advance this Earley item's production index;
 * c) creating - reduction items set is empty, predecessor is null.
 *
 * This class's equals() and hashCode() are to ignore reduction items.
 */
 class EarleyItemDescriptor {
  private final EarleyItem myPredecessor;
  private final Set<EarleyItem> myReductionItems;

  EarleyItemDescriptor(EarleyItem predecessor) {
    myPredecessor = predecessor;
    myReductionItems = Sets.newHashSet();
  }

  public boolean addReductionItem(EarleyItem reductionItem) {
    assert Objects.equal(myPredecessor.getNextExpectedSymbol(), reductionItem.getSymbol());
    return myReductionItems.add(reductionItem);
  }

  public EarleyItem getPredecessor() {
    return myPredecessor;
  }

  public Set<EarleyItem> getReductionItems() {
    return myReductionItems;
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
