package ru.spbau.preprocessing.parser.earley.ast;

import ru.spbau.preprocessing.api.conditions.PresenceCondition;
import ru.spbau.preprocessing.parser.earley.grammar.EarleyTerminal;

public class EarleyLeafNode<TokenTypeBase> extends EarleyAstNode {
  private final EarleyTerminal<TokenTypeBase> myTerminal;

  public EarleyLeafNode(EarleyTerminal<TokenTypeBase> terminal, PresenceCondition presenceCondition) {
    super(presenceCondition);
    myTerminal = terminal;
  }

  @Override
  public EarleyTerminal<TokenTypeBase> getSymbol() {
    return myTerminal;
  }

  @Override
  public void accept(EarleyAstVisitor visitor) {
    visitor.visitLeafNode(this);
  }
}
