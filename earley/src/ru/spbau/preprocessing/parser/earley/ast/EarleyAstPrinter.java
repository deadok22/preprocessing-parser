package ru.spbau.preprocessing.parser.earley.ast;

import ru.spbau.preprocessing.api.conditions.PresenceCondition;

import java.io.PrintWriter;

public class EarleyAstPrinter extends EarleyAstVisitor {
  private final PrintWriter myPrintWriter;
  private int myDepth = 0;

  public EarleyAstPrinter(PrintWriter printWriter) {
    myPrintWriter = printWriter;
  }

  @Override
  public void visitAlternativesNode(EarleyAlternativesNode alternativesNode) {
    printAndVisitChildren("ALTERNATIVES", alternativesNode);
  }

  @Override
  public void visitCompositeNode(EarleyCompositeNode compositeNode) {
    printAndVisitChildren("COMPOSITE: " + compositeNode.getSymbol().getName(), compositeNode);
  }

  @Override
  public void visitLeafNode(EarleyLeafNode<?> leafNode) {
    printAndVisitChildren("LEAF: " + leafNode.getSymbol(), leafNode);
  }

  @Override
  public void visitConditionalBranchNode(EarleyConditionalBranchNode conditionalBranchNode) {
    printAndVisitChildren("CONDITIONAL", conditionalBranchNode);
  }

  private void printAndVisitChildren(String line, EarleyAstNode node) {
    printLine(line + ": " + getConditionalString(node));
    visitNodes(node.getChildren());
  }

  private String getConditionalString(EarleyAstNode node) {
    PresenceCondition presenceCondition = node.getPresenceCondition();
    return presenceCondition != null ? presenceCondition + " <" + presenceCondition.value() + ">" : "null";
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
