package ru.spbau.preprocessing.erlang.preprocessor.ast;

import ru.spbau.preprocessing.api.preprocessor.PreprocessorLanguageFileInclusionNode;
import ru.spbau.preprocessing.api.preprocessor.PreprocessorLanguageNodeVisitor;

public class ErlangInclusionAttributeNode extends ErlangPreprocessorNode implements PreprocessorLanguageFileInclusionNode {
  private final String myIncludePath;
  private final ErlangIncludeResolutionStrategy myResolutionStrategy;

  public ErlangInclusionAttributeNode(CharSequence buffer, int startOffset, int endOffset,
                                      String includePath, ErlangIncludeResolutionStrategy resolutionStrategy) {
    super(buffer, startOffset, endOffset);
    myIncludePath = includePath;
    myResolutionStrategy = resolutionStrategy;
  }

  @Override
  public String getIncludePath() {
    return myIncludePath;
  }

  @Override
  public ErlangIncludeResolutionStrategy getResolutionStrategy() {
    return myResolutionStrategy;
  }

  @Override
  public void accept(PreprocessorLanguageNodeVisitor visitor) {
    visitor.visitFileInclusion(this);
  }

  public enum ErlangIncludeResolutionStrategy implements IncludeResolutionStrategy {
    INCLUDE_LIB,
    INCLUDE
  }
}
