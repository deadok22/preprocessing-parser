package ru.spbau.preprocessing.api.preprocessor;

import java.util.List;

/**
 * Represents a condition and conditionally compiled code associated with it.
 */
public interface PreprocessorLanguageConditionalNode extends PreprocessorLanguageNode {
  PreprocessorLanguageNode getCondition();
  List<PreprocessorLanguageNode> getCode();
}
