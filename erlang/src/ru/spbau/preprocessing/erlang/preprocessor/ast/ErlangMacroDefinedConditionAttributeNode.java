package ru.spbau.preprocessing.erlang.preprocessor.ast;

import ru.spbau.preprocessing.api.preprocessor.PreprocessorLanguageConditionalNode;

public class ErlangMacroDefinedConditionAttributeNode extends ErlangConditionalAttributeNodeBase
  implements PreprocessorLanguageConditionalNode.PreprocessorLanguageCondition {
  private final String myMacroName;
  private final boolean myIsPositive;

  public ErlangMacroDefinedConditionAttributeNode(CharSequence buffer, int startOffset, int endOffset,
                                                  String macroName, boolean isPositive) {
    super(buffer, startOffset, endOffset);
    myMacroName = macroName;
    myIsPositive = isPositive;
  }

  public String getMacroName() {
    return myMacroName;
  }

  public boolean isPositiveCondition() {
    return myIsPositive;
  }
}
