package ru.spbau.preprocessing.lexer.lexemegraph;

import ru.spbau.preprocessing.api.files.SourceFile;

public final class LexemeLocation {
  private final SourceFile mySourceFile;
  private final int myStartOffset;

  public LexemeLocation(SourceFile sourceFile, int startOffset) {
    mySourceFile = sourceFile;
    myStartOffset = startOffset;
  }

  public SourceFile getSourceFile() {
    return mySourceFile;
  }

  public int getStartOffset() {
    return myStartOffset;
  }
}
