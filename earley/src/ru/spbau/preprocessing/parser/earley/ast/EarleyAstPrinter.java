package ru.spbau.preprocessing.parser.earley.ast;

import java.io.PrintWriter;

public class EarleyAstPrinter extends EarleyAstVisitor {
  private final PrintWriter myPrintWriter;
  private int myDepth = 0;

  public EarleyAstPrinter(PrintWriter printWriter) {
    myPrintWriter = printWriter;
  }

  @Override
  public void visitAlternativesNode(EarleyAlternativesNode alternativesNode) {
    printLine("ALTERNATIVES");
    visitNodes(alternativesNode.getChildren());
  }

  @Override
  public void visitCompositeNode(EarleyCompositeNode compositeNode) {
    printLine("COMPOSITE: " + compositeNode.getSymbol().getName());
    visitNodes(compositeNode.getChildren());
  }

  @Override
  public void visitLeafNode(EarleyLeafNode<?> leafNode) {
    printLine("LEAF: " + leafNode.getSymbol());
  }

  @Override
  public void visitConditionalBranchNode(EarleyConditionalBranchNode conditionalBranchNode) {
    printLine("CONDITIONAL: " + conditionalBranchNode.getPresenceCondition() + " <" + conditionalBranchNode.getPresenceCondition().value() + ">");
    visitNodes(conditionalBranchNode.getChildren());
  }

  private void visitNodes(Iterable<? extends EarleyAstNode> nodes) {
    myDepth++;
    for (EarleyAstNode node : nodes) {
      node.accept(this);
    }
    myDepth--;
  }

  private void printLine(String what) {
    for (int i = 0; i < myDepth; i++) {
      myPrintWriter.print(' ');
    }
    myPrintWriter.println(what);
  }
}
