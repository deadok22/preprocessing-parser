package ru.spbau.preprocessing.lexer;

import com.google.common.collect.Queues;
import ru.spbau.preprocessing.api.LanguageLexer;
import ru.spbau.preprocessing.api.LanguageProvider;
import ru.spbau.preprocessing.api.conditions.PresenceCondition;
import ru.spbau.preprocessing.api.conditions.PresenceConditionFactory;
import ru.spbau.preprocessing.api.macros.MacroCall;
import ru.spbau.preprocessing.api.macros.MacroCallParser;
import ru.spbau.preprocessing.api.macros.MacroCallParserState;
import ru.spbau.preprocessing.api.preprocessor.*;
import ru.spbau.preprocessing.lexer.lexemegraph.Lexeme;
import ru.spbau.preprocessing.lexer.lexemegraph.LexemeGraphBuilder;
import ru.spbau.preprocessing.lexer.lexemegraph.LexemeGraphNode;

import java.io.IOException;
import java.util.*;

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

    private final MacroCallMultiParser myMacroCallMultiParser;
    private final Queue<Lexeme<TokenTypeBase>> myLookAheadBuffer;


    public LexingPreprocessorLanguageNodeVisitor(ConditionalContextImpl context, LexemeGraphBuilder<TokenTypeBase> lexemeGraphBuilder) {
      myContext = context;
      myLexemeGraphBuilder = lexemeGraphBuilder;
      myLexemeGraphBuilder.setNodePresenceCondition(context.getCurrentPresenceCondition());
      myMacroCallMultiParser = new MacroCallMultiParser();
      myLookAheadBuffer = Queues.newArrayDeque();
    }

    @Override
    public void visit(PreprocessorLanguageNode node) {
      myLexemeGraphBuilder.setNodePresenceCondition(myContext.getCurrentPresenceCondition());
      LanguageLexer<TokenTypeBase> langLexer = myLanguageProvider.createLanguageLexer();
      langLexer.start(myText, node.getStartOffset(), node.getStartOffset() + node.getLength());
      try {
        for (langLexer.advance(); langLexer.tokenType() != null; langLexer.advance()) {
          Lexeme<TokenTypeBase> lexeme = new Lexeme<TokenTypeBase>(langLexer.tokenType());
          myLookAheadBuffer.add(lexeme);
          if (myMacroCallMultiParser.needMoreTokens()) {
            myMacroCallMultiParser.consumeLexeme(lexeme);
          }
          if (!myMacroCallMultiParser.needMoreTokens()) {
            Collection<MacroCall<TokenTypeBase>> macroCalls = myMacroCallMultiParser.getParsedMacroCalls();
            if (macroCalls.isEmpty()) {
              myLexemeGraphBuilder.addLexeme(myLookAheadBuffer.poll());
              myMacroCallMultiParser.reset();
              //TODO reset the multiparser and feed it tokens from the buffer
            }
            else if (macroCalls.size() == 1) {
              //TODO resolve a macro and decide whether to add a conditional or not
            }
            else {
              //TODO fork for each macro call which resolves to something
            }
          }
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

    private class MacroCallMultiParser {
      private final Collection<MacroCallParser<TokenTypeBase>> myMacroCallParsers =
              myLanguageProvider.createMacroCallParsers();

      public void reset() {
        for (MacroCallParser<TokenTypeBase> parser : myMacroCallParsers) {
          parser.reset();
        }
      }

      public boolean needMoreTokens() {
        for (MacroCallParser<TokenTypeBase> parser : myMacroCallParsers) {
          if (parser.getState() == MacroCallParserState.PARSING) return true;
        }
        return false;
      }

      public void consumeLexeme(Lexeme<TokenTypeBase> lexeme) {
        for (MacroCallParser<TokenTypeBase> parser : myMacroCallParsers) {
          if (parser.getState() != MacroCallParserState.NOT_PARSED && parser.getState() != MacroCallParserState.PARSED) {
            parser.consumeLexeme(lexeme);
          }
        }
      }

      public Collection<MacroCall<TokenTypeBase>> getParsedMacroCalls() {
        List<MacroCall<TokenTypeBase>> macroCalls = new ArrayList<MacroCall<TokenTypeBase>>(myMacroCallParsers.size());
        for (MacroCallParser<TokenTypeBase> parser : myMacroCallParsers) {
          if (parser.getState() == MacroCallParserState.PARSED) {
            macroCalls.add(parser.getParsedCall());
          }
        }
        return macroCalls;
      }
    }
  }
}
