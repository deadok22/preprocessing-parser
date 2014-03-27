package ru.spbau.preprocessing.lexer.lexemegraph;

import java.util.Collections;
import java.util.List;

public class LexemeGraphForkNode extends LexemeGraphNode {
  private final List<LexemeGraphNode> myChildren;

  public LexemeGraphForkNode(List<LexemeGraphNode> children) {
    myChildren = children;
  }

  public List<LexemeGraphNode> getChildren() {
    return Collections.unmodifiableList(myChildren);
  }

  void setChild(int childIdx, LexemeGraphNode node) {
    myChildren.set(childIdx, node);
  }
}
