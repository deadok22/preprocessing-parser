package ru.spbau.preprocessing.parser.earley;

import com.google.common.collect.Sets;
import ru.spbau.preprocessing.api.conditions.PresenceCondition;
import ru.spbau.preprocessing.parser.earley.grammar.EarleyProduction;
import ru.spbau.preprocessing.parser.earley.grammar.EarleySymbol;

import java.util.*;

public class EarleyChart {
  private final List<State> myStates = new ArrayList<State>();

  public State lastState() {
    return myStates.isEmpty() ? newState() : myStates.get(myStates.size() - 1);
  }

  public State newState() {
    State state = new State();
    myStates.add(state);
    return state;
  }

  public static class State implements Iterable<Item> {
    private LinkedHashSet<Item> myItems;

    private State() {
      myItems = Sets.newLinkedHashSet();
    }

    public boolean addItems(Collection<Item> items) {
      return myItems.addAll(items);
    }

    @Override
    public Iterator<Item> iterator() {
      return myItems.iterator();
    }

    public Item createItem(EarleyProduction production, PresenceCondition presenceCondition) {
      return new Item(production, 0, presenceCondition, this);
    }

    public boolean containsAll(Collection<Item> items) {
      return myItems.containsAll(items);
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
  }
}
