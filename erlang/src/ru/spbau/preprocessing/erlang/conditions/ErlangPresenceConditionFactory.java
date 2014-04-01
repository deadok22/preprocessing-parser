package ru.spbau.preprocessing.erlang.conditions;

import ru.spbau.preprocessing.api.conditions.*;
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
  public PresenceCondition create(PreprocessorLanguageConditionalNode.PreprocessorLanguageCondition langCondition, ConditionalContext context) {
    assert langCondition instanceof ErlangMacroDefinedConditionAttributeNode;
    ErlangMacroDefinedConditionAttributeNode macroDefinedCondition = (ErlangMacroDefinedConditionAttributeNode) langCondition;
    String macroName = macroDefinedCondition.getMacroName();
    MacroDefinitionState mds = context.getMacroTable().getMacroDefinitionState(macroName, context);
    PresenceCondition.Value macroIsDefined =
            mds == MacroDefinitionState.DEFINED ? PresenceCondition.Value.TRUE :
                    mds == MacroDefinitionState.UNDEFINED ? PresenceCondition.Value.FALSE :
                            PresenceCondition.Value.VARIANCE;
    ErlangPresenceCondition.ErlangMacroDefinedPresenceCondition cond =
            new ErlangPresenceCondition.ErlangMacroDefinedPresenceCondition(macroIsDefined, macroName);
    return macroDefinedCondition.isPositiveCondition() ? cond : cond.not();
  }
}
