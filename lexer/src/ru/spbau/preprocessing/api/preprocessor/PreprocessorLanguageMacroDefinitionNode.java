package ru.spbau.preprocessing.api.preprocessor;

import java.util.List;

/**
 * A macro definition node.
 * This node abstracts out macro definitions such as C #define ... directives or Erlang -define(...) attributes.
 */
public interface PreprocessorLanguageMacroDefinitionNode extends PreprocessorLanguageNode {
  String getName();
  int getArity();
  List<String> getParameterNames();
  String getSubstitutionText();
}
