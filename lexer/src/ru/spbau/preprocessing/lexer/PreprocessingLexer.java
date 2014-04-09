package ru.spbau.preprocessing.lexer;

import ru.spbau.preprocessing.api.LanguageLexer;
import ru.spbau.preprocessing.api.LanguageProvider;
import ru.spbau.preprocessing.api.conditions.MacroDefinitionState;
import ru.spbau.preprocessing.api.conditions.PresenceCondition;
import ru.spbau.preprocessing.api.conditions.PresenceConditionFactory;
import ru.spbau.preprocessing.api.macros.MacroCall;
import ru.spbau.preprocessing.api.macros.MacroCallParser;
import ru.spbau.preprocessing.api.preprocessor.*;
import ru.spbau.preprocessing.lexer.lexemegraph.Lexeme;
import ru.spbau.preprocessing.lexer.lexemegraph.LexemeGraphBuilder;
import ru.spbau.preprocessing.lexer.lexemegraph.LexemeGraphNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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
        LexemeBuffer<TokenTypeBase> buffer = LexemeBuffer.create(text, langLexer);
        processBuffer(buffer);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    private void processBuffer(LexemeBuffer<TokenTypeBase> buffer) throws IOException {
      Collection<MacroCallParser<TokenTypeBase>> macroCallParsers = myLanguageProvider.createMacroCallParsers();
      MacroDefinitionsTableImpl macroTable = myContext.getMacroTable();

      while (buffer.hasNext()) {
        List<MacroCall<TokenTypeBase>> parsedMacroCalls = new ArrayList<MacroCall<TokenTypeBase>>(macroCallParsers.size());
        for (MacroCallParser<TokenTypeBase> parser : macroCallParsers) {
          if (parser.parse(buffer.spawnLookahead())) {
            parsedMacroCalls.add(parser.getParsedCall());
          }
        }

        if (parsedMacroCalls.isEmpty()) {
          myLexemeGraphBuilder.addLexeme(buffer.next());
          continue;
        }

        List<MacroCall<TokenTypeBase>> resolvedCalls = new ArrayList<MacroCall<TokenTypeBase>>(parsedMacroCalls.size());
        for (MacroCall<TokenTypeBase> call : parsedMacroCalls) {
          if (macroTable.getMacroDefinitionState(call.getMacroName(), call.getArity(), myContext) != MacroDefinitionState.UNDEFINED) {
            // since the macro is not undefined, there could be some non-free reaching definitions
            Collection<MacroDefinitionsTableImpl.Entry> entries = macroTable.getEntries(call.getMacroName(), call.getArity(), myContext);
            Collection<MacroDefinitionsTableImpl.DefinedEntry> definedEntries = MacroDefinitionsTableImpl.filterDefinedEntries(entries);
            if (!definedEntries.isEmpty()) {
              resolvedCalls.add(call);
            }
          }
        }

        if (resolvedCalls.isEmpty()) {
          myLexemeGraphBuilder.addLexeme(buffer.next());
          continue;
        }

        //TODO provide a strategy for choosing action when multiple calls are parsed and resolved starting at the same offset
        //TODO under different presence conditions different macro calls should be used

        //for now we'll select the one with biggest arity
        MacroCall<TokenTypeBase> call = null;
        for (MacroCall<TokenTypeBase> resolvedCall : resolvedCalls) {
          if (call == null || call.getArity() < resolvedCall.getArity()) {
            call = resolvedCall;
          }
        }
        assert call != null;

        //TODO make sure to have an alternative where a maro call is not resolved
        Collection<MacroDefinitionsTableImpl.Entry> entries = macroTable.getEntries(call.getMacroName(), call.getArity(), myContext);
        List<MacroDefinitionsTableImpl.DefinedEntry> definedEntries =
                new ArrayList<MacroDefinitionsTableImpl.DefinedEntry>(MacroDefinitionsTableImpl.filterDefinedEntries(entries));
        List<LexemeGraphBuilder<TokenTypeBase>> forkBuilders = myLexemeGraphBuilder.fork(definedEntries.size());
        for (int i = 0; i < forkBuilders.size(); i++) {
          LexemeGraphBuilder<TokenTypeBase> substitutionForkBuilder = forkBuilders.get(i);
          MacroDefinitionsTableImpl.DefinedEntry definedEntry = definedEntries.get(i);
          PreprocessorLanguageMacroDefinitionNode definition = definedEntry.getDefinition();
          PresenceCondition definitionPresenceCondition = definedEntry.getPresenceCondition();

          //TODO process macro call arguments and macro calls in the substitution
          String unprocessedSubstitutionText = definition.getSubstitutionText();
          ConditionalContextImpl substitutionContext = myContext
                  .copy().andCondition(definitionPresenceCondition);

          new LexingPreprocessorLanguageNodeVisitor(substitutionContext, substitutionForkBuilder)
                  .processText(unprocessedSubstitutionText, 0, unprocessedSubstitutionText.length());
          substitutionForkBuilder.build();
        }

        //skip macro call lexemes
        for (int i = 0; i < call.getLexemesCount(); i++) {
          buffer.next();
        }
      }

    }
  }


  private static class LexemeBuffer<TokenTypeBase> implements Iterator<Lexeme<TokenTypeBase>> {
    private final List<Lexeme<TokenTypeBase>> myBuffer;
    private int myIndex;

    private LexemeBuffer(List<Lexeme<TokenTypeBase>> buffer) throws IOException {
      this(buffer, 0);
    }

    private LexemeBuffer(List<Lexeme<TokenTypeBase>> buffer, int index) {
      myBuffer = buffer;
      myIndex = index;
    }

    public LexemeBuffer<TokenTypeBase> spawnLookahead() {
      return new LexemeBuffer<TokenTypeBase>(myBuffer, myIndex);
    }

    @Override
    public boolean hasNext() {
      return myIndex < myBuffer.size();
    }

    @Override
    public Lexeme<TokenTypeBase> next() {
      return hasNext() ? myBuffer.get(myIndex++) : null;
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }

    public static <TokenTypeBase> LexemeBuffer<TokenTypeBase> create(CharSequence text, LanguageLexer<TokenTypeBase> lexer) throws IOException {
      List<Lexeme<TokenTypeBase>> buffer = new ArrayList<Lexeme<TokenTypeBase>>();
      for (lexer.advance(); lexer.tokenType() != null; lexer.advance()) {
        Lexeme<TokenTypeBase> lexeme = new Lexeme<TokenTypeBase>(lexer.tokenType(),
                text.subSequence(lexer.tokenStartOffset(), lexer.tokenEndOffset()).toString());
        buffer.add(lexeme);
      }
      return new LexemeBuffer<TokenTypeBase>(buffer);
    }
  }
}
