package ru.spbau.preprocessing.lexer.lexemegraph;

public interface LexemeGraphVisitor {
  void visitForkNode(LexemeGraphForkNode forkNode);
  void visitLangNode(LexemeGraphLangNode langNode);
}
