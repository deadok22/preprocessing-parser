package ru.spbau.preprocessing.parser.earley.ast;

import ru.spbau.preprocessing.parser.earley.grammar.EarleySymbol;

import java.util.List;

public class EarleyAlternativesNode extends EarleyAstNode {
  private final List<EarleyConditionalBranchNode> myAlternatives;

  public EarleyAlternativesNode(List<EarleyConditionalBranchNode> alternatives) {
    myAlternatives = alternatives;
  }

  @Override
  public EarleySymbol getSymbol() {
    return null;
  }

  @Override
  public List<EarleyConditionalBranchNode> getChildren() {
    return myAlternatives;
  }

  @Override
  public void accept(EarleyAstVisitor visitor) {
    visitor.visitAlternativesNode(this);
  }
}
