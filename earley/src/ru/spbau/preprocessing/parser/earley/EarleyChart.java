package ru.spbau.preprocessing.parser.earley;

import com.google.common.base.Objects;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import ru.spbau.preprocessing.api.conditions.PresenceCondition;
import ru.spbau.preprocessing.parser.earley.grammar.EarleyNonTerminal;
import ru.spbau.preprocessing.parser.earley.grammar.EarleyProduction;
import ru.spbau.preprocessing.parser.earley.grammar.EarleySymbol;

import java.util.*;


public class EarleyChart {
  private final List<State> myStates = new ArrayList<State>();

  public State lastState() {
    return myStates.isEmpty() ? newState() : myStates.get(myStates.size() - 1);
  }

  public State newState() {
    State state = new State(myStates.isEmpty() ? null : lastState());
    myStates.add(state);
    return state;
  }

  public static class State implements Iterable<Item> {
    private final GeneratorTrackingItemSet myItems;
    private final State myPreviousState;

    private State(State previousState) {
      myItems = new GeneratorTrackingItemSet();
      myPreviousState = previousState;
    }

    public boolean addItems(GeneratorTrackingItemSet itemSet) {
      return myItems.addAll(itemSet);
    }

    @Override
    public Iterator<Item> iterator() {
      return myItems.iterator();
    }

    public Item createItem(EarleyProduction production, PresenceCondition presenceCondition) {
      return new Item(production, 0, presenceCondition, this);
    }

    public State previousState() {
      return myPreviousState;
    }

    public List<Item> getCompletionsOf(EarleyNonTerminal symbol) {
      List<Item> completionsOfSymbol = Lists.newArrayList();
      for (Item item : this) {
        if (Objects.equal(item.getProduction().getLeft(), symbol) && item.isComplete()) {
          completionsOfSymbol.add(item);
        }
      }
      return completionsOfSymbol;
    }
  }

  /**
   * A set of chart items which tracks the way items were created.
   * That is each item has a set of items which generated this item.
   */
  public static final class GeneratorTrackingItemSet implements Iterable<Item> {
    private final LinkedHashSet<Item> myItems = Sets.newLinkedHashSet();
    private final HashMultimap<Item, Item> myItemGenerators = HashMultimap.create();

    public boolean addItem(Item item, Item generator) {
      return addItem(item, Collections.singleton(generator));
    }

    public boolean addItem(Item item, Iterable<Item> generators) {
      return myItems.add(item) | myItemGenerators.putAll(item, generators);
    }

    public boolean addAll(GeneratorTrackingItemSet itemSet) {
      return myItems.addAll(itemSet.myItems) | myItemGenerators.putAll(itemSet.myItemGenerators);
    }

    public Set<Item> getGenerators(Item item) {
      return myItemGenerators.get(item);
    }

    public void clear() {
      myItems.clear();
      myItemGenerators.clear();
    }

    @Override
    public Iterator<Item> iterator() {
      return myItems.iterator();
    }

    public static GeneratorTrackingItemSet from(Iterable<Item> itemIterable) {
      GeneratorTrackingItemSet result = new GeneratorTrackingItemSet();
      for (Item item : itemIterable) {
        result.addItem(item, Collections.<Item>emptyList());
      }
      return result;
    }
  }

  public static final class Item {
    private final EarleyProduction myProduction;
    private final int myIndexInProduction;
    private final PresenceCondition myPresenceCondition;
    //TODO track location across conditional branches (not sure how to achieve it, yet)
    private final State myStartState;

    Item(EarleyProduction production, int indexInProduction, PresenceCondition presenceCondition, State startState) {
      myProduction = production;
      myIndexInProduction = indexInProduction;
      myPresenceCondition = presenceCondition;
      myStartState = startState;
    }

    public EarleyProduction getProduction() {
      return myProduction;
    }

    public int getIndexInProduction() {
      return myIndexInProduction;
    }

    public PresenceCondition getPresenceCondition() {
      return myPresenceCondition;
    }

    public State getStartState() {
      return myStartState;
    }

    public EarleySymbol getNextExpectedSymbol() {
      List<EarleySymbol> productionSymbols = myProduction.getRight();
      return myIndexInProduction < productionSymbols.size() ? productionSymbols.get(myIndexInProduction) : null;
    }

    //TODO handle presence conditions correctly
    public Item matchOneSymbol() {
      assert myIndexInProduction < myProduction.getRight().size();
      return new Item(myProduction, myIndexInProduction + 1, myPresenceCondition, myStartState);
    }

    public boolean isComplete() {
      return myIndexInProduction == myProduction.getRight().size();
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Item item = (Item) o;

      if (myIndexInProduction != item.myIndexInProduction) return false;
      if (!myPresenceCondition.equals(item.myPresenceCondition)) return false;
      if (!myProduction.equals(item.myProduction)) return false;
      if (!myStartState.equals(item.myStartState)) return false;

      return true;
    }

    @Override
    public int hashCode() {
      int result = myProduction.hashCode();
      result = 31 * result + myIndexInProduction;
      result = 31 * result + myPresenceCondition.hashCode();
      result = 31 * result + myStartState.hashCode();
      return result;
    }

    @Override
    public String toString() {
      return  myIndexInProduction + " in " + myProduction.toString();
    }
  }
}
