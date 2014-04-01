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

public class MacroDefinitionsTableImpl implements MacroDefinitionsTable {
  private final ListMultimap<String, Entry> myTable = ArrayListMultimap.create();

  @Override
  public MacroDefinitionState getMacroDefinitionState(String macroName, ConditionalContext context) {
    //TODO handle all cases
    List<Entry> reachingEntries = getReachingEntries(macroName, context.getCurrentPresenceCondition());
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

  public void undefine(PreprocessorLanguageMacroUndefinitionNode undefinition, PresenceCondition presenceCondition) {
    //TODO maintain consistency
    putEntry(newEntry(undefinition, presenceCondition));
  }

  public void define(PreprocessorLanguageMacroDefinitionNode definition, PresenceCondition presenceCondition) {
    //TODO maintain consistency
    putEntry(newEntry(definition, presenceCondition));
  }

  /**
   * Returns all table entries which are reachable at current presence condition.
   */
  private List<Entry> getReachingEntries(String macroName, final PresenceCondition presenceCondition) {
    return Lists.newArrayList(Collections2.filter(myTable.get(macroName), new Predicate<Entry>() {
      @Override
      public boolean apply(Entry entry) {
        return entry.presenceCondition().and(presenceCondition).value() != PresenceCondition.Value.FALSE;
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
    private final PresenceCondition myPresenceCondition;

    protected Entry(PresenceCondition presenceCondition) {
      myPresenceCondition = presenceCondition;
    }

    public abstract MacroDefinitionState type();
    public abstract String macroName();

    public final PresenceCondition presenceCondition() {
      return myPresenceCondition;
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
