package ru.spbau.preprocessing.erlang.preprocessor.ast;

import ru.spbau.preprocessing.api.preprocessor.PreprocessorLanguageConditionalNode;

import java.util.List;

public class ErlangConditionalNode extends ErlangPreprocessorNode
        implements PreprocessorLanguageConditionalNode {
  private final ErlangConditionalAttributeNodeBase myConditionalAttribute;
  private final List<ErlangPreprocessorNode> myCode;

  public ErlangConditionalNode(ErlangConditionalAttributeNodeBase conditionalAttribute, List<ErlangPreprocessorNode> code) {
    super(conditionalAttribute.getBuffer(),
            conditionalAttribute.getStartOffset(),
            code.isEmpty() ? conditionalAttribute.getStartOffset() + conditionalAttribute.getLength() :
                    code.get(code.size() - 1).getStartOffset() + code.get(code.size() - 1).getLength()
    );
    myConditionalAttribute = conditionalAttribute;
    myCode = code;
  }

  @Override
  public PreprocessorLanguageCondition getConditionExpression() {
    return myConditionalAttribute instanceof ErlangMacroDefinedConditionAttributeNode ?
            (ErlangMacroDefinedConditionAttributeNode) myConditionalAttribute :
            null;
  }

  @Override
  public List<ErlangPreprocessorNode> getCode() {
    return myCode;
  }
}
