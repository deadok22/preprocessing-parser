package ru.spbau.preprocessing.lexer;

import ru.spbau.preprocessing.api.LanguageLexer;
import ru.spbau.preprocessing.api.LanguageProvider;
import ru.spbau.preprocessing.api.conditions.PresenceCondition;
import ru.spbau.preprocessing.api.conditions.PresenceConditionFactory;
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
    ConditionalContextImpl rootContext = new ConditionalContextImpl(myLanguageProvider.createPresenceConditionFactory());
    new LexingPreprocessorLanguageNodeVisitor(rootContext, lexemeGraphBuilder).visitNodes(preprocessorLanguageNodes);
    return lexemeGraphBuilder.build();
  }

  private class LexingPreprocessorLanguageNodeVisitor extends PreprocessorLanguageNodeVisitor {
    private final ConditionalContextImpl myContext;
    private final LexemeGraphBuilder<TokenTypeBase> myLexemeGraphBuilder;

    public LexingPreprocessorLanguageNodeVisitor(ConditionalContextImpl context, LexemeGraphBuilder<TokenTypeBase> lexemeGraphBuilder) {
      myContext = context;
      myLexemeGraphBuilder = lexemeGraphBuilder;
      myLexemeGraphBuilder.setNodePresenceCondition(context.getCurrentPresenceCondition());
    }

    @Override
    public void visit(PreprocessorLanguageNode node) {
      processText(myText, node.getStartOffset(), node.getStartOffset() + node.getLength());
    }

    @Override
    public void visitNodes(List<? extends PreprocessorLanguageNode> nodes) {
      for (PreprocessorLanguageNode preprocessorLanguageNode : nodes) {
        preprocessorLanguageNode.accept(this);
      }
    }

    @Override
    public void visitMacroDefinition(PreprocessorLanguageMacroDefinitionNode node) {
      myContext.defineMacro(node);
    }

    @Override
    public void visitMacroUndefinition(PreprocessorLanguageMacroUndefinitionNode node) {
      myContext.undefineMacro(node);
    }

    @Override
    public void visitAlternatives(PreprocessorLanguageAlternativesNode node) {
      List<? extends PreprocessorLanguageConditionalNode> alternatives = node.getAlternatives();
      List<LexemeGraphBuilder<TokenTypeBase>> forkBuilders = myLexemeGraphBuilder.fork(alternatives.size());
      PresenceConditionFactory presenceConditionFactory = myLanguageProvider.createPresenceConditionFactory();
      PresenceCondition negationOfPreviousBranchGuards = presenceConditionFactory.getTrue();
      for (int i = 0; i < alternatives.size(); i++) {
        LexemeGraphBuilder<TokenTypeBase> forkBuilder = forkBuilders.get(i);
        PreprocessorLanguageConditionalNode conditional = alternatives.get(i);
        PreprocessorLanguageConditionalNode.PreprocessorLanguageCondition branchGuardExpression = conditional.getConditionExpression();
        PresenceCondition branchGuard = branchGuardExpression != null ?
                presenceConditionFactory.create(branchGuardExpression, myContext) :
                presenceConditionFactory.getTrue();
        ConditionalContextImpl forkConditionalContext = myContext.copy()
                .andCondition(negationOfPreviousBranchGuards)
                .andCondition(branchGuard);
        new LexingPreprocessorLanguageNodeVisitor(forkConditionalContext, forkBuilder).visitConditional(conditional);
        forkBuilder.build();
        negationOfPreviousBranchGuards = negationOfPreviousBranchGuards.and(branchGuard.not());
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

    private void processText(CharSequence text, int startOffset, int endOffset) {
      myLexemeGraphBuilder.setNodePresenceCondition(myContext.getCurrentPresenceCondition());
      LanguageLexer<TokenTypeBase> langLexer = myLanguageProvider.createLanguageLexer();
      langLexer.start(text, startOffset, endOffset);
      try {
        for (langLexer.advance(); langLexer.tokenType() != null; langLexer.advance()) {
          Lexeme<TokenTypeBase> lexeme = new Lexeme<TokenTypeBase>(langLexer.tokenType(),
                  text.subSequence(langLexer.tokenStartOffset(), langLexer.tokenEndOffset()).toString());
          myLexemeGraphBuilder.addLexeme(lexeme);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
