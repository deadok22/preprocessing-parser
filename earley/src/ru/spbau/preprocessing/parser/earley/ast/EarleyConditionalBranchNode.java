package ru.spbau.preprocessing.parser.earley.ast;

import ru.spbau.preprocessing.api.conditions.PresenceCondition;
import ru.spbau.preprocessing.parser.earley.grammar.EarleySymbol;

import java.util.List;

public class EarleyConditionalBranchNode extends EarleyAstNode {
  private final List<EarleyAstNode> myChildren;

  public EarleyConditionalBranchNode(PresenceCondition presenceCondition, List<EarleyAstNode> children) {
    super(presenceCondition);
    myChildren = children;
  }

  @Override
  public EarleySymbol getSymbol() {
    return null;
  }

  @Override
  public List<EarleyAstNode> getChildren() {
    return myChildren;
  }

  @Override
  public void accept(EarleyAstVisitor visitor) {
    visitor.visitConditionalBranchNode(this);
  }
}
