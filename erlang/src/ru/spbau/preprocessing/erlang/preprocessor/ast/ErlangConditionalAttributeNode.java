package ru.spbau.preprocessing.erlang.preprocessor.ast;

public class ErlangConditionalAttributeNode extends ErlangConditionalAttributeNodeBase {
  private final Type myType;

  public ErlangConditionalAttributeNode(CharSequence buffer, int startOffset, int endOffset, Type type) {
    super(buffer, startOffset, endOffset);
    myType = type;
  }

  public Type getType() {
    return myType;
  }

  public enum Type {ELSE, ENDIF}
}
