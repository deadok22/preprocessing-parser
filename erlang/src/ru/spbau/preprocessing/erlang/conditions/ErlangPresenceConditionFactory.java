package ru.spbau.preprocessing.erlang.conditions;

import ru.spbau.preprocessing.api.conditions.ConditionalContext;
import ru.spbau.preprocessing.api.conditions.MacroDefinitionState;
import ru.spbau.preprocessing.api.conditions.PresenceConditionFactory;
import ru.spbau.preprocessing.api.preprocessor.PreprocessorLanguageConditionalNode;
import ru.spbau.preprocessing.erlang.preprocessor.ast.ErlangMacroDefinedConditionAttributeNode;

public class ErlangPresenceConditionFactory implements PresenceConditionFactory {
  @Override
  public ErlangPresenceCondition getTrue() {
    return ErlangPresenceCondition.TRUE;
  }

  @Override
  public ErlangPresenceCondition getFalse() {
    return ErlangPresenceCondition.FALSE;
  }

  @Override
  public ErlangPresenceCondition create(PreprocessorLanguageConditionalNode.PreprocessorLanguageCondition langCondition, ConditionalContext context) {
    assert langCondition instanceof ErlangMacroDefinedConditionAttributeNode;
    ErlangMacroDefinedConditionAttributeNode macroDefinedCondition = (ErlangMacroDefinedConditionAttributeNode) langCondition;
    String macroName = macroDefinedCondition.getMacroName();
    return createMacroIsDefined(macroName, macroDefinedCondition.isPositiveCondition(), context);
  }

  @Override
  public ErlangPresenceCondition createMacroIsDefined(String macroName, ConditionalContext containingContext) {
    return createMacroIsDefined(macroName, true, containingContext);
  }

  public ErlangPresenceCondition createMacroIsDefined(String macroName, boolean isPositive, ConditionalContext context) {
    MacroDefinitionState mds = context.getMacroTable().getMacroDefinitionState(macroName, context);
    switch (mds) {
      case FREE : return ErlangPresenceCondition.macroDefined(macroName, isPositive);
      case DEFINED: return isPositive ? getTrue() : getFalse();
      case UNDEFINED: return isPositive ? getFalse() : getTrue();
      default: throw new AssertionError("Unexpected macro definition state");
    }
  }
}
