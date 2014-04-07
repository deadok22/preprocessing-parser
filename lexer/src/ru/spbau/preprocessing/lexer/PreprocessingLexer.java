package ru.spbau.preprocessing.lexer;

import com.google.common.collect.Lists;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;

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
              MacroCall<TokenTypeBase> call = macroCalls.iterator().next();
              MacroDefinitionsTableImpl macroTable = myContext.getMacroTable();
              switch (macroTable.getMacroDefinitionState(call.getMacroName(), call.getArity(), myContext)) {
                case DEFINED: {
                  expandMacro(call);
                  break;
                }
                case UNDEFINED:
                  myLexemeGraphBuilder.addLexeme(myLookAheadBuffer.poll());
                  myMacroCallMultiParser.reset();
                  //TODO reset the multiparser and feed it tokens from the buffer
                  break;
                case FREE:
                  //TODO examine what's there - be sure to add a branch in case the macro is neither defined nor undefined.
                  break;
                default: throw new RuntimeException("unexpected enum member");
              }
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

    private void expandMacro(MacroCall<TokenTypeBase> call) {
      Collection<MacroDefinitionsTableImpl.Entry> entries = myContext.getMacroTable().getEntries(call.getMacroName(), call.getArity(), myContext);
      List<MacroDefinitionsTableImpl.DefinedEntry> definitions = Lists.newArrayList(MacroDefinitionsTableImpl.filterDefinedEntries(entries));
      List<LexemeGraphBuilder<TokenTypeBase>> substitutionForks = myLexemeGraphBuilder.fork(definitions.size());
      for (int i = 0; i < definitions.size(); i++) {
        //TODO use a lexer which substitutes macro call argument references
        LexemeGraphBuilder<TokenTypeBase> forkBuilder = substitutionForks.get(i);
        MacroDefinitionsTableImpl.DefinedEntry definedEntry = definitions.get(i);
        //TODO make sure this branch condition is ok
        ConditionalContextImpl forkContext = myContext.copy().andCondition(definedEntry.getPresenceCondition());
        PreprocessorLanguageMacroDefinitionNode definition = definedEntry.getDefinition();
        new LexingPreprocessorLanguageNodeVisitor(forkContext, forkBuilder)
                .processText(definition.getSubstitutionText(), 0, definition.getSubstitutionText().length());
        forkBuilder.build();
      }
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
