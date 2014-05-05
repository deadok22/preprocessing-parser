package ru.spbau.preprocessing.parser.earley.ast;

import ru.spbau.preprocessing.api.conditions.PresenceCondition;
import ru.spbau.preprocessing.parser.earley.grammar.EarleySymbol;

import java.util.Collections;
import java.util.List;

public abstract class EarleyAstNode {
  private final PresenceCondition myPresenceCondition;

  protected EarleyAstNode(PresenceCondition presenceCondition) {
    myPresenceCondition = presenceCondition;
  }

  public abstract EarleySymbol getSymbol();

  public List<? extends EarleyAstNode> getChildren() {
    return Collections.emptyList();
  }

  public abstract void accept(EarleyAstVisitor visitor);

  public PresenceCondition getPresenceCondition() {
    return myPresenceCondition;
  };
}
