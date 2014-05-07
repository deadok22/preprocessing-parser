package ru.spbau.preprocessing.parser.earley.ast;

import ru.spbau.preprocessing.api.conditions.PresenceCondition;
import ru.spbau.preprocessing.lexer.lexemegraph.Lexeme;
import ru.spbau.preprocessing.lexer.lexemegraph.LexemeLocation;

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
    Lexeme<?> lexeme = leafNode.getLexeme();
    String lexemeText =  " \"" + lexeme.getText() + '\"';
    LexemeLocation location = lexeme.getLocation();
    String locationText = " [" + location.getStartOffset() + "@" + location.getSourceFile().getPath() + "]";
    printAndVisitChildren("LEAF: " + leafNode.getSymbol() + lexemeText + locationText, leafNode);
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
