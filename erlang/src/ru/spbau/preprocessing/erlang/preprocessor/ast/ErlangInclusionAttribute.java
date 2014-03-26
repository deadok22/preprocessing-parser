package ru.spbau.preprocessing.erlang.preprocessor.ast;

import ru.spbau.preprocessing.api.preprocessor.PreprocessorLanguageFileInclusionNode;

public class ErlangInclusionAttribute extends ErlangPreprocessorNode implements PreprocessorLanguageFileInclusionNode {
  private final String myIncludePath;
  private final ErlangIncludeResolutionStrategy myResolutionStrategy;

  public ErlangInclusionAttribute(CharSequence buffer, int startOffset, int endOffset,
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

  public enum ErlangIncludeResolutionStrategy implements IncludeResolutionStrategy {
    INCLUDE_LIB,
    INCLUDE
  }
}
