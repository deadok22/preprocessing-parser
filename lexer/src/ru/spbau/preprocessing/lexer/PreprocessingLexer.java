package ru.spbau.preprocessing.lexer;

import ru.spbau.preprocessing.api.LanguageLexer;
import ru.spbau.preprocessing.api.LanguageProvider;
import ru.spbau.preprocessing.api.preprocessor.*;
import ru.spbau.preprocessing.lexer.lexemegraph.Lexeme;
import ru.spbau.preprocessing.lexer.lexemegraph.LexemeGraphBuilder;
import ru.spbau.preprocessing.lexer.lexemegraph.LexemeGraphNode;

import java.io.IOException;
import java.util.List;

public class PreprocessingLexer<TokenTypeBase> {
  private final LanguageProvider<TokenTypeBase> myLanguageProvider;
  private final String myText;

  public PreprocessingLexer(LanguageProvider<TokenTypeBase> languageProvider, String text) throws IOException {
    myLanguageProvider = languageProvider;
    myText = text;
  }

  public LexemeGraphNode buildLexemeGraph() throws IOException {
    List<? extends PreprocessorLanguageNode> preprocessorLanguageNodes = myLanguageProvider.createPreprocessorLanguageParser().parse(myText);
    if (preprocessorLanguageNodes == null) return null;
    LexemeGraphBuilder<TokenTypeBase> lexemeGraphBuilder = new LexemeGraphBuilder<TokenTypeBase>();
    new LexingPreprocessorLanguageNodeVisitor(lexemeGraphBuilder).visitNodes(preprocessorLanguageNodes);
    return lexemeGraphBuilder.build();
  }

  private class LexingPreprocessorLanguageNodeVisitor extends PreprocessorLanguageNodeVisitor {
    private final LexemeGraphBuilder<TokenTypeBase> myLexemeGraphBuilder;

    public LexingPreprocessorLanguageNodeVisitor(LexemeGraphBuilder<TokenTypeBase> lexemeGraphBuilder) {
      myLexemeGraphBuilder = lexemeGraphBuilder;
    }

    @Override
    public void visit(PreprocessorLanguageNode node) {
      LanguageLexer<TokenTypeBase> langLexer = myLanguageProvider.createLanguageLexer();
      langLexer.start(myText, node.getStartOffset(), node.getStartOffset() + node.getLength());
      try {
        for (langLexer.advance(); langLexer.tokenType() != null; langLexer.advance()) {
          Lexeme<TokenTypeBase> lexeme = new Lexeme<TokenTypeBase>(langLexer.tokenType());
          myLexemeGraphBuilder.addLexeme(lexeme);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    @Override
    public void visitNodes(List<? extends PreprocessorLanguageNode> nodes) {
      for (PreprocessorLanguageNode preprocessorLanguageNode : nodes) {
        preprocessorLanguageNode.accept(this);
      }
    }

    @Override
    public void visitMacroDefinition(PreprocessorLanguageMacroDefinitionNode node) {
      //TODO
    }

    @Override
    public void visitMacroUndefinition(PreprocessorLanguageMacroUndefinitionNode node) {
      //TODO
    }

    @Override
    public void visitAlternatives(PreprocessorLanguageAlternativesNode node) {
      List<? extends PreprocessorLanguageConditionalNode> alternatives = node.getAlternatives();
      List<LexemeGraphBuilder<TokenTypeBase>> forkBuilders = myLexemeGraphBuilder.fork(alternatives.size());
      for (int i = 0; i < alternatives.size(); i++) {
        LexemeGraphBuilder<TokenTypeBase> forkBuilder = forkBuilders.get(i);
        PreprocessorLanguageConditionalNode conditional = alternatives.get(i);
        new LexingPreprocessorLanguageNodeVisitor(forkBuilder).visitConditional(conditional);
        forkBuilder.build();
      }
    }

    @Override
    public void visitFileInclusion(PreprocessorLanguageFileInclusionNode node) {
      //TODO
    }

    @Override
    public void visitConditional(PreprocessorLanguageConditionalNode node) {
      visitNodes(node.getCode());
    }
  }
}
