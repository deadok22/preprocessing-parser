package ru.spbau.preprocessing.lexer.lexemegraph;

import java.util.Collections;
import java.util.List;

public class LexemeGraphLangNode<TokenTypeBase> extends LexemeGraphNode {
  private final List<Lexeme<TokenTypeBase>> myLexemes;

  public LexemeGraphLangNode(List<Lexeme<TokenTypeBase>> lexemes) {
    myLexemes = lexemes;
  }

  public List<Lexeme<TokenTypeBase>> getLexemes() {
    return Collections.unmodifiableList(myLexemes);
  }
}
