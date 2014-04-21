package ru.spbau.preprocessing.parser.earley.ast;

import ru.spbau.preprocessing.parser.earley.grammar.EarleySymbol;

import java.util.Collections;
import java.util.List;

public abstract class EarleyAstNode {
  public abstract EarleySymbol getSymbol();

  public List<? extends EarleyAstNode> getChildren() {
    return Collections.emptyList();
  }
}
