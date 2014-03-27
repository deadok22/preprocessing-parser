package ru.spbau.preprocessing.lexer.lexemegraph;

public class LexemeGraphNode {
  private LexemeGraphNode myNextNode;

  public LexemeGraphNode() {
  }

  public LexemeGraphNode next() {
    return myNextNode;
  }

  void setNext(LexemeGraphNode node) {
    myNextNode = node;
  }
}
