package ru.spbau.preprocessing.api.preprocessor;

/**
 * Represents a node such as C #include directive.
 */
public interface PreprocessorLanguageFileInclusionNode extends PreprocessorLanguageNode {
  String getIncludePath();
  IncludeResolutionStrategy getResolutionStrategy();

  /**
   * * Resolution strategy abstracts out the way include path is searched
   * for example, C has two forms of #include directive - with double
   * quotes and with angle brackets, which affect include path resolution.
   */
  interface IncludeResolutionStrategy {}
}
