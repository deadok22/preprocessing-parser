package ru.spbau.preprocessing.api.preprocessor;

import java.util.List;

/**
 * Represents a sequence of conditionally-compiled blocks such as
 * #ifdef ... #elif ... #endif
 */
public interface PreprocessorLanguageAlternativesNode extends PreprocessorLanguageNode {
  List<PreprocessorLanguageConditionalNode> getAlternatives();
}
