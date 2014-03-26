package ru.spbau.preprocessing.erlang.preprocessor.ast;

import ru.spbau.preprocessing.api.preprocessor.PreprocessorLanguageAlternativesNode;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ErlangAlternativesNode extends ErlangPreprocessorNode
        implements PreprocessorLanguageAlternativesNode {
  private final ErlangConditionalNode myConditionTrueBranch;
  private final ErlangConditionalNode myAlternativeBranch;
  private final ErlangConditionalAttributeNode myEndif;

  public ErlangAlternativesNode(ErlangConditionalNode conditionTrueBranch, ErlangConditionalNode alternativeBranch, ErlangConditionalAttributeNode endif) {
    super(conditionTrueBranch.getBuffer(), conditionTrueBranch.getStartOffset(), endif.getStartOffset() + endif.getLength());
    myConditionTrueBranch = conditionTrueBranch;
    myAlternativeBranch = alternativeBranch;
    myEndif = endif;
  }

  @Override
  public List<ErlangConditionalNode> getAlternatives() {
    return myAlternativeBranch == null ?
            Collections.singletonList(myConditionTrueBranch) :
            Arrays.asList(myConditionTrueBranch, myAlternativeBranch);
  }
}
