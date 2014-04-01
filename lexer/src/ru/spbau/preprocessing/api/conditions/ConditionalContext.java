package ru.spbau.preprocessing.api.conditions;

public interface ConditionalContext {
  MacroDefinitionsTable getMacroTable();
  PresenceCondition getCurrentPresenceCondition();
}
