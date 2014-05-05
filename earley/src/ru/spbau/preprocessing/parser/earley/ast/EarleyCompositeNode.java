package ru.spbau.preprocessing.parser.earley.ast;

import ru.spbau.preprocessing.api.conditions.PresenceCondition;
import ru.spbau.preprocessing.parser.earley.grammar.EarleyNonTerminal;

import java.util.List;

public class EarleyCompositeNode extends EarleyAstNode {
  private EarleyNonTerminal mySymbol;
  private List<EarleyAstNode> myChildren;

  public EarleyCompositeNode(EarleyNonTerminal symbol, List<EarleyAstNode> children, PresenceCondition presenceCondition) {
    super(presenceCondition);
    mySymbol = symbol;
    myChildren = children;
  }

  @Override
  public EarleyNonTerminal getSymbol() {
    return mySymbol;
  }

  @Override
  public List<EarleyAstNode> getChildren() {
    return myChildren;
  }

  @Override
  public void accept(EarleyAstVisitor visitor) {
    visitor.visitCompositeNode(this);
  }
}
