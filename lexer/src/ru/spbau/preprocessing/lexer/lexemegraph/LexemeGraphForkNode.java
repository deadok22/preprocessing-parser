package ru.spbau.preprocessing.lexer.lexemegraph;

import ru.spbau.preprocessing.api.conditions.PresenceCondition;

import java.util.Collections;
import java.util.List;

public class LexemeGraphForkNode extends LexemeGraphNode {
  private final List<LexemeGraphNode> myChildren;

  public LexemeGraphForkNode(PresenceCondition presenceCondition, List<LexemeGraphNode> children) {
    super(presenceCondition);
    myChildren = children;
  }

  public List<LexemeGraphNode> getChildren() {
    return Collections.unmodifiableList(myChildren);
  }

  void setChild(int childIdx, LexemeGraphNode node) {
    myChildren.set(childIdx, node);
  }

  @Override
  public void acceptSingleNode(LexemeGraphVisitor visitor) {
    visitor.visitForkNode(this);
  }
}
