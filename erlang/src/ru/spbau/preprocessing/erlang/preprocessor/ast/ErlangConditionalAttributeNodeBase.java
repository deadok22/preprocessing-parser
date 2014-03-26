package ru.spbau.preprocessing.erlang.preprocessor.ast;

/**
 * A marker superclass for -ifdef, -ifndef, -else and -endif attribute nodes
 */
public abstract class ErlangConditionalAttributeNodeBase extends ErlangPreprocessorNode {
  public ErlangConditionalAttributeNodeBase(CharSequence buffer, int startOffset, int endOffset) {
    super(buffer, startOffset, endOffset);
  }
}
