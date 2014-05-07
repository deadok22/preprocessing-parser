package ru.spbau.preprocessing.parser.earley.ast;

import ru.spbau.preprocessing.api.conditions.PresenceCondition;
import ru.spbau.preprocessing.lexer.lexemegraph.Lexeme;
import ru.spbau.preprocessing.parser.earley.grammar.EarleyTerminal;

public class EarleyLeafNode<TokenTypeBase> extends EarleyAstNode {
  private final EarleyTerminal<TokenTypeBase> myTerminal;
  private final Lexeme<TokenTypeBase> myLexeme;

  public EarleyLeafNode(EarleyTerminal<TokenTypeBase> terminal, Lexeme<TokenTypeBase> lexeme, PresenceCondition presenceCondition) {
    super(presenceCondition);
    myTerminal = terminal;
    myLexeme = lexeme;
  }

  @Override
  public EarleyTerminal<TokenTypeBase> getSymbol() {
    return myTerminal;
  }

  public Lexeme<TokenTypeBase> getLexeme() {
    return myLexeme;
  }

  @Override
  public void accept(EarleyAstVisitor visitor) {
    visitor.visitLeafNode(this);
  }
}
