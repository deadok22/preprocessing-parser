package ru.spbau.preprocessing.lexer.lexemegraph;

import ru.spbau.preprocessing.api.conditions.PresenceCondition;

public abstract class LexemeGraphNode {
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

  public PresenceCondition getPresenceCondition() {
    return myPresenceCondition;
  }

  public final void accept(LexemeGraphVisitor visitor) {
    acceptSingleNode(visitor);
    if (myNextNode != null) {
      myNextNode.accept(visitor);
    }
  }

  protected abstract void acceptSingleNode(LexemeGraphVisitor visitor);
}
