package ru.spbau.preprocessing.api.preprocessor;

/**
 * A base AST node for all of preprocessor language's nodes
 */
public interface PreprocessorLanguageNode {
  int getStartOffset();
  int getLength();
  CharSequence getText();
  void accept(PreprocessorLanguageNodeVisitor visitor);
}
