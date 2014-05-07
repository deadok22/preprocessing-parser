package ru.spbau.preprocessing.api.preprocessor;

/**
 * Represents a node such as C #include directive.
 */
public interface PreprocessorLanguageFileInclusionNode extends PreprocessorLanguageNode {
  String getIncludePath();
}
