package ru.spbau.preprocessing.lexer.lexemegraph;

import ru.spbau.preprocessing.api.conditions.PresenceCondition;

public class LexemeGraphNode {
  private final PresenceCondition myPresenceCondition;
  private LexemeGraphNode myNextNode;

  public LexemeGraphNode(PresenceCondition presenceCondition) {
    myPresenceCondition = presenceCondition;
  }

  public LexemeGraphNode next() {
    return myNextNode;
  }

  void setNext(LexemeGraphNode node) {
    myNextNode = node;
  }
}
