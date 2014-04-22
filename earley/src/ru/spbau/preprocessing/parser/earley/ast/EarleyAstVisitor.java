package ru.spbau.preprocessing.parser.earley.ast;

public abstract class EarleyAstVisitor {
  public void visitAlternativesNode(EarleyAlternativesNode alternativesNode) {
  }

  public void visitCompositeNode(EarleyCompositeNode compositeNode) {
  }

  public void visitLeafNode(EarleyLeafNode<?> leafNode) {
  }

  public void visitConditionalBranchNode(EarleyConditionalBranchNode conditionalBranchNode) {
  }
}
