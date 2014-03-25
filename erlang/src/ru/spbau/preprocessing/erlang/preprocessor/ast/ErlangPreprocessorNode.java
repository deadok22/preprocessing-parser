package ru.spbau.preprocessing.erlang.preprocessor.ast;

import ru.spbau.preprocessing.api.preprocessor.PreprocessorLanguageNode;
import ru.spbau.preprocessing.api.preprocessor.PreprocessorLanguageNodeVisitor;

public class ErlangPreprocessorNode implements PreprocessorLanguageNode {
  private final CharSequence myBuffer;
  private final int myStartOffset;
  private final int myEndOffset;

  public ErlangPreprocessorNode(CharSequence buffer, int startOffset, int endOffset) {
    myBuffer = buffer;
    myStartOffset = startOffset;
    myEndOffset = endOffset;
  }

  @Override
  public int getStartOffset() {
    return myStartOffset;
  }

  @Override
  public int getLength() {
    return myEndOffset - myStartOffset;
  }

  @Override
  public CharSequence getText() {
    return myBuffer.subSequence(myStartOffset, myEndOffset);
  }

  @Override
  public void accept(PreprocessorLanguageNodeVisitor visitor) {
    visitor.visit(this);
  }
}
