package ru.spbau.preprocessing.api.conditions;

public interface MacroDefinitionsTable {
  MacroDefinitionState getMacroDefinitionState(String macroName);
}
