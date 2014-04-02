package ru.spbau.preprocessing.lexer.lexemegraph;

import ru.spbau.preprocessing.api.conditions.PresenceCondition;

import java.util.Collections;
import java.util.List;

public class LexemeGraphLangNode<TokenTypeBase> extends LexemeGraphNode {
  private final List<Lexeme<TokenTypeBase>> myLexemes;

  public LexemeGraphLangNode(PresenceCondition presenceCondition, List<Lexeme<TokenTypeBase>> lexemes) {
    super(presenceCondition);
    myLexemes = lexemes;
  }

  public List<Lexeme<TokenTypeBase>> getLexemes() {
    return Collections.unmodifiableList(myLexemes);
  }

  @Override
  public void acceptSingleNode(LexemeGraphVisitor visitor) {
    visitor.visitLangNode(this);
  }
}
