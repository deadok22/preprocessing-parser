package ru.spbau.preprocessing.erlang.conditions;

import ru.spbau.preprocessing.api.conditions.PresenceCondition;
import ru.spbau.preprocessing.api.conditions.PresenceConditionFactory;
import ru.spbau.preprocessing.api.preprocessor.PreprocessorLanguageConditionalNode;
import ru.spbau.preprocessing.erlang.preprocessor.ast.ErlangMacroDefinedConditionAttributeNode;

public class ErlangPresenceConditionFactory implements PresenceConditionFactory {
  @Override
  public PresenceCondition getTrue() {
    return ErlangPresenceCondition.TRUE;
  }

  @Override
  public PresenceCondition getFalse() {
    return ErlangPresenceCondition.FALSE;
  }

  @Override
  public PresenceCondition create(PreprocessorLanguageConditionalNode.PreprocessorLanguageCondition langCondition) {
    assert langCondition instanceof ErlangMacroDefinedConditionAttributeNode;
    ErlangMacroDefinedConditionAttributeNode macroDefinedCondition = (ErlangMacroDefinedConditionAttributeNode) langCondition;
    String macroName = macroDefinedCondition.getMacroName();
    ErlangPresenceCondition.ErlangMacroDefinedCondition cond = new ErlangPresenceCondition.ErlangMacroDefinedCondition(macroName);
    return macroDefinedCondition.isPositiveCondition() ? cond : cond.not();
  }
}
