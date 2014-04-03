package ru.spbau.preprocessing.lexer;

import com.google.common.base.Predicate;
import com.google.common.collect.*;
import ru.spbau.preprocessing.api.conditions.ConditionalContext;
import ru.spbau.preprocessing.api.conditions.MacroDefinitionState;
import ru.spbau.preprocessing.api.conditions.MacroDefinitionsTable;
import ru.spbau.preprocessing.api.conditions.PresenceCondition;
import ru.spbau.preprocessing.api.preprocessor.PreprocessorLanguageMacroDefinitionNode;
import ru.spbau.preprocessing.api.preprocessor.PreprocessorLanguageMacroUndefinitionNode;

import java.util.EnumSet;
import java.util.List;
import java.util.ListIterator;

public class MacroDefinitionsTableImpl implements MacroDefinitionsTable {
  private final ListMultimap<String, Entry> myTable = LinkedListMultimap.create();

  @Override
  public MacroDefinitionState getMacroDefinitionState(String macroName, ConditionalContext context) {
    return getMacroDefinitionState(macroName, context.getCurrentPresenceCondition());
  }

  public void undefine(PreprocessorLanguageMacroUndefinitionNode undefinition, PresenceCondition presenceCondition) {
    updateEntriesBeforeUndefinition(undefinition.getName(), presenceCondition);
    addFreeEntryForDefinition(undefinition.getName(), presenceCondition);
    putEntry(newEntry(undefinition, presenceCondition));
  }

  public void define(PreprocessorLanguageMacroDefinitionNode definition, PresenceCondition presenceCondition) {
    updateEntriesBeforeDefinition(definition.getName(), definition.getArity(), presenceCondition);
    addFreeEntryForDefinition(definition.getName(), presenceCondition);
    putEntry(newEntry(definition, presenceCondition));
  }

  private void addFreeEntryForDefinition(String macroName, PresenceCondition newDefinitionPresenceCondition) {
    if (newDefinitionPresenceCondition.value() == PresenceCondition.Value.TRUE) return;
    if (getMacroDefinitionState(macroName, newDefinitionPresenceCondition) == MacroDefinitionState.FREE &&
            getReachingEntries(macroName, newDefinitionPresenceCondition).isEmpty()) {
      putEntry(newEntry(macroName, newDefinitionPresenceCondition.not()));
    }
  }

  private MacroDefinitionState getMacroDefinitionState(String macroName, PresenceCondition currentPresenceCondition) {
    //TODO handle all cases
    List<Entry> reachingEntries = getReachingEntries(macroName, currentPresenceCondition);
    EnumSet<MacroDefinitionState> states = EnumSet.noneOf(MacroDefinitionState.class);
    for (Entry reachingEntry : reachingEntries) {
      states.add(reachingEntry.type());
    }
    if (states.size() == 1) {
      return states.iterator().next();
    }
    //TODO deduce condition for macro to be defined or undefined
    System.err.println("Macro definition state is not clear. We'll treat it as a free macro for now.");
    return MacroDefinitionState.FREE;
  }

  private void updateEntriesBeforeDefinition(String macroName, int arity, PresenceCondition definitionPresenceCondition) {
    updateEntries(macroName, arity, true, definitionPresenceCondition);
  }

  private void updateEntriesBeforeUndefinition(String macroName, PresenceCondition undefinitionPresenceCondition) {
    updateEntries(macroName, 0, false, undefinitionPresenceCondition);
  }

  private void updateEntries(String macroName, int macroArity, boolean arityIsSignificant, PresenceCondition definitionPresenceCondition) {
    PresenceCondition negationOfDefinitionPresenceCondition = definitionPresenceCondition.not();
    List<Entry> allEntries = myTable.get(macroName);
    for (ListIterator<Entry> i = allEntries.listIterator(); i.hasNext(); ) {
      Entry entry = i.next();
      if (arityIsSignificant &&
              entry instanceof DefinedEntry &&
              ((DefinedEntry) entry).arity() != macroArity) continue;
      entry.setPresenceCondition(entry.getPresenceCondition().and(negationOfDefinitionPresenceCondition));
      if (entry.getPresenceCondition().value() == PresenceCondition.Value.FALSE) {
        i.remove();
      }
    }
  }

  /**
   * Returns all table entries which are reachable at current presence condition.
   */
  private List<Entry> getReachingEntries(String macroName, final PresenceCondition presenceCondition) {
    return Lists.newArrayList(Collections2.filter(myTable.get(macroName), new Predicate<Entry>() {
      @Override
      public boolean apply(Entry entry) {
        return entry.getPresenceCondition().and(presenceCondition).value() != PresenceCondition.Value.FALSE;
      }
    }));
  }

  private void putEntry(Entry e) {
    myTable.put(e.macroName(), e);
  }

  private static Entry newEntry(PreprocessorLanguageMacroDefinitionNode definition, PresenceCondition presenceCondition) {
    return new DefinedEntry(definition, presenceCondition);
  }

  private static Entry newEntry(PreprocessorLanguageMacroUndefinitionNode undefinition, PresenceCondition presenceCondition) {
    return new UndefinedEntry(undefinition, presenceCondition);
  }

  private static Entry newEntry(String macroName, PresenceCondition presenceCondition) {
    return new FreeEntry(macroName, presenceCondition);
  }

  public static abstract class Entry {
    private PresenceCondition myPresenceCondition;

    protected Entry(PresenceCondition presenceCondition) {
      myPresenceCondition = presenceCondition;
    }

    public abstract MacroDefinitionState type();
    public abstract String macroName();

    public final PresenceCondition getPresenceCondition() {
      return myPresenceCondition;
    }

    public void setPresenceCondition(PresenceCondition presenceCondition) {
      myPresenceCondition = presenceCondition;
    }
  }

  public static final class DefinedEntry extends Entry {
    private final PreprocessorLanguageMacroDefinitionNode myDefinition;

    public DefinedEntry(PreprocessorLanguageMacroDefinitionNode definition, PresenceCondition presenceCondition) {
      super(presenceCondition);
      myDefinition = definition;
    }

    @Override
    public MacroDefinitionState type() {
      return MacroDefinitionState.DEFINED;
    }

    @Override
    public String macroName() {
      return myDefinition.getName();
    }

    public int arity() {
      return myDefinition.getArity();
    }
  }

  public static final class UndefinedEntry extends Entry {
    private final PreprocessorLanguageMacroUndefinitionNode myUndefinition;

    protected UndefinedEntry(PreprocessorLanguageMacroUndefinitionNode undefinition, PresenceCondition presenceCondition) {
      super(presenceCondition);
      myUndefinition = undefinition;
    }

    @Override
    public MacroDefinitionState type() {
      return MacroDefinitionState.UNDEFINED;
    }

    @Override
    public String macroName() {
      return myUndefinition.getName();
    }
  }

  public static final class FreeEntry extends Entry {
    private final String myMacroName;

    protected FreeEntry(String macroName, PresenceCondition presenceCondition) {
      super(presenceCondition);
      myMacroName = macroName;
    }

    @Override
    public MacroDefinitionState type() {
      return MacroDefinitionState.FREE;
    }

    @Override
    public String macroName() {
      return myMacroName;
    }
  }
}
