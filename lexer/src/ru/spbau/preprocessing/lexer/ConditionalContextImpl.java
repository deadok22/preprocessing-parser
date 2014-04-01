package ru.spbau.preprocessing.lexer;

import ru.spbau.preprocessing.api.conditions.ConditionalContext;
import ru.spbau.preprocessing.api.conditions.MacroDefinitionsTable;
import ru.spbau.preprocessing.api.conditions.PresenceCondition;

public class ConditionalContextImpl implements ConditionalContext {
  private final MacroDefinitionsTable myMacroDefinitionsTable;
  private PresenceCondition myCurrentPresenceCondition;

  public ConditionalContextImpl(MacroDefinitionsTable macroDefinitionsTable, PresenceCondition currentPresenceCondition) {
    myMacroDefinitionsTable = macroDefinitionsTable;
    myCurrentPresenceCondition = currentPresenceCondition;
  }

  @Override
  public MacroDefinitionsTable getMacroTable() {
    return myMacroDefinitionsTable;
  }

  @Override
  public PresenceCondition getCurrentPresenceCondition() {
    return myCurrentPresenceCondition;
  }
}
