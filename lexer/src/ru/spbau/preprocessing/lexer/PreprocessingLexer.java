package ru.spbau.preprocessing.lexer;

import ru.spbau.preprocessing.api.LanguageLexer;
import ru.spbau.preprocessing.api.LanguageProvider;
import ru.spbau.preprocessing.api.conditions.MacroDefinitionState;
import ru.spbau.preprocessing.api.conditions.PresenceCondition;
import ru.spbau.preprocessing.api.conditions.PresenceConditionFactory;
import ru.spbau.preprocessing.api.files.SourceFile;
import ru.spbau.preprocessing.api.macros.MacroCall;
import ru.spbau.preprocessing.api.macros.MacroCallParser;
import ru.spbau.preprocessing.api.preprocessor.*;
import ru.spbau.preprocessing.lexer.lexemegraph.Lexeme;
import ru.spbau.preprocessing.lexer.lexemegraph.LexemeGraphBuilder;
import ru.spbau.preprocessing.lexer.lexemegraph.LexemeGraphNode;
import ru.spbau.preprocessing.lexer.lexemegraph.LexemeLocation;

import java.io.IOException;
import java.util.*;

public class PreprocessingLexer<TokenTypeBase> {
  private final LanguageProvider<TokenTypeBase> myLanguageProvider;
  private final SourceFile mySourceFile;

  public PreprocessingLexer(LanguageProvider<TokenTypeBase> languageProvider, SourceFile sourceFile) {
    myLanguageProvider = languageProvider;
    mySourceFile = sourceFile;
  }

  public LexemeGraphNode buildLexemeGraph() throws IOException {
    String text = mySourceFile.loadText();
    List<? extends PreprocessorLanguageNode> preprocessorLanguageNodes = myLanguageProvider.createPreprocessorLanguageParser().parse(text);
    if (preprocessorLanguageNodes == null) return null;
    LexemeGraphBuilder<TokenTypeBase> lexemeGraphBuilder = new LexemeGraphBuilder<TokenTypeBase>();
    ConditionalContextImpl rootContext = new ConditionalContextImpl(myLanguageProvider.createPresenceConditionFactory());
    new LexingPreprocessorLanguageNodeVisitor(mySourceFile, text, rootContext, lexemeGraphBuilder, null).visitNodes(preprocessorLanguageNodes);
    return lexemeGraphBuilder.build();
  }

  private class LexingPreprocessorLanguageNodeVisitor extends PreprocessorLanguageNodeVisitor {
    private final SourceFile mySourceFile;
    private final String myText;
    private final ConditionalContextImpl myContext;
    private final LexemeGraphBuilder<TokenTypeBase> myLexemeGraphBuilder;
    //if this is not null, all lexemes produced by this visitor will have this location
    //used as a workaround for substitution tokens
    private final LexemeLocation myLexemeLocation;

    public LexingPreprocessorLanguageNodeVisitor(SourceFile sourceFile,
                                                 String text,
                                                 ConditionalContextImpl context,
                                                 LexemeGraphBuilder<TokenTypeBase> lexemeGraphBuilder,
                                                 LexemeLocation lexemeLocation) {
      mySourceFile = sourceFile;
      myText = text;
      myContext = context;
      myLexemeGraphBuilder = lexemeGraphBuilder;
      myLexemeGraphBuilder.setNodePresenceCondition(context.getCurrentPresenceCondition());
      myLexemeLocation = lexemeLocation;
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
        new LexingPreprocessorLanguageNodeVisitor(mySourceFile, myText, forkConditionalContext, forkBuilder, myLexemeLocation).visitConditional(conditional);
        forkBuilder.build();
        negationOfPreviousBranchGuards = negationOfPreviousBranchGuards.and(branchGuard.not());
      }
    }

    @Override
    public void visitFileInclusion(PreprocessorLanguageFileInclusionNode node) {
      try {
        SourceFile includedFile = mySourceFile.resolveInclusion(node);
        String text = includedFile.loadText();
        List<? extends PreprocessorLanguageNode> preprocessorLanguageNodes = myLanguageProvider.createPreprocessorLanguageParser().parse(text);
        new LexingPreprocessorLanguageNodeVisitor(includedFile, text, myContext, myLexemeGraphBuilder, myLexemeLocation).visitNodes(preprocessorLanguageNodes);
      } catch (IOException e) {
        e.printStackTrace();
      }
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
        LexemeBuffer<TokenTypeBase> buffer = createLexemeBuffer(text, langLexer);
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

        //calculate resolved macro calls with corresponding presence conditions
        class ConditionalMacroCall {
          MacroCall<TokenTypeBase> myCall;
          PresenceCondition myPresenceCondition;
          ConditionalMacroCall(MacroCall<TokenTypeBase> call, PresenceCondition presenceCondition) {
            myCall = call;
            myPresenceCondition = presenceCondition;
          }
        }
        List<ConditionalMacroCall> resolvedCalls = new ArrayList<ConditionalMacroCall>(parsedMacroCalls.size());
        for (MacroCall<TokenTypeBase> call : parsedMacroCalls) {
          if (macroTable.getMacroDefinitionState(call.getMacroName(), call.getArity(), myContext) != MacroDefinitionState.UNDEFINED) {
            // since the macro is not undefined, there could be some non-free reaching definitions
            Collection<MacroDefinitionsTableImpl.Entry> entries = macroTable.getEntries(call.getMacroName(), call.getArity(), myContext);
            Collection<MacroDefinitionsTableImpl.DefinedEntry> definedEntries = MacroDefinitionsTableImpl.filterDefinedEntries(entries);
            if (!definedEntries.isEmpty()) {
              PresenceCondition callPresenceCondition = myLanguageProvider.createPresenceConditionFactory().getFalse();
              for (MacroDefinitionsTableImpl.DefinedEntry definedEntry : definedEntries) {
                callPresenceCondition = callPresenceCondition.or(definedEntry.getPresenceCondition());
              }
              resolvedCalls.add(new ConditionalMacroCall(call, callPresenceCondition));
            }
          }
        }

        if (resolvedCalls.isEmpty()) {
          myLexemeGraphBuilder.addLexeme(buffer.next());
          continue;
        }

        //store max macro call lexemes into a buffer so that
        //lexemes could be reused in conditional branches
        int maxMacroCallLexemesCount = 0;
        for (ConditionalMacroCall resolvedCall : resolvedCalls) {
          int lexemesCount = resolvedCall.myCall.getLexemesCount();
          if (maxMacroCallLexemesCount < lexemesCount) {
            maxMacroCallLexemesCount = lexemesCount;
          }
        }
        assert maxMacroCallLexemesCount > 0;
        ArrayList<Lexeme<TokenTypeBase>> macroCallLexemesBuffer = new ArrayList<Lexeme<TokenTypeBase>>(maxMacroCallLexemesCount);
        Lexeme<TokenTypeBase> macroCallsStartLexeme = buffer.next(); //store the very first token so that we can re-use it's location
        macroCallLexemesBuffer.add(macroCallsStartLexeme);
        for (int i = 1; i < maxMacroCallLexemesCount; i++) {
          macroCallLexemesBuffer.add(buffer.next());
        }

        //sort all resolved calls by their arity, biggest arity first
        //smaller arity calls' presence conditions are obtained via andNot with bigger arity calls
        Collections.sort(resolvedCalls, new Comparator<ConditionalMacroCall>() {
          @Override
          public int compare(ConditionalMacroCall o1, ConditionalMacroCall o2) {
            return o2.myCall.getArity() - o1.myCall.getArity();
          }
        });

        //fork for each resolved call
        List<LexemeGraphBuilder<TokenTypeBase>> callForkBuilders = myLexemeGraphBuilder.fork(resolvedCalls.size());
        PresenceCondition negationOfPreviousCallForkGuards = myLanguageProvider.createPresenceConditionFactory().getTrue();
        for (int i = 0; i < callForkBuilders.size(); i++) {
          ConditionalMacroCall call = resolvedCalls.get(i);
          LexemeGraphBuilder<TokenTypeBase> callForkBuilder = callForkBuilders.get(i);

          PresenceCondition callForkPresenceCondition = negationOfPreviousCallForkGuards.and(call.myPresenceCondition);
          LexingPreprocessorLanguageNodeVisitor callForkPreprocessor =
                  new LexingPreprocessorLanguageNodeVisitor(mySourceFile, myText, myContext.copy().andCondition(callForkPresenceCondition),
                          callForkBuilder, myLexemeLocation != null ? myLexemeLocation : macroCallsStartLexeme.getLocation());
          callForkPreprocessor.expandMacroCall(call.myCall);

          //TODO process tokens after shorter macro calls - they can contain macro calls (i.e. ?M(?M) in case only ?M/-1 is defined)
          // for now we just add them as text
          for (int j = call.myCall.getLexemesCount(); j < maxMacroCallLexemesCount; j++) {
            callForkBuilder.addLexeme(macroCallLexemesBuffer.get(j));
          }
          callForkBuilder.build();

          negationOfPreviousCallForkGuards = negationOfPreviousCallForkGuards.and(call.myPresenceCondition.not());
        }
        //TODO make sure to have an alternative where a macro call is not resolved
      }

    }

    private void expandMacroCall(MacroCall<TokenTypeBase> call) {
      Collection<MacroDefinitionsTableImpl.Entry> entries = myContext.getMacroTable()
              .getEntries(call.getMacroName(), call.getArity(), myContext);
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

        new LexingPreprocessorLanguageNodeVisitor(mySourceFile, myText, substitutionContext, substitutionForkBuilder, myLexemeLocation)
                .processText(unprocessedSubstitutionText, 0, unprocessedSubstitutionText.length());
        substitutionForkBuilder.build();
      }
    }

    private LexemeBuffer<TokenTypeBase> createLexemeBuffer(CharSequence text, LanguageLexer<TokenTypeBase> lexer) throws IOException {
      List<Lexeme<TokenTypeBase>> buffer = new ArrayList<Lexeme<TokenTypeBase>>();
      for (lexer.advance(); lexer.tokenType() != null; lexer.advance()) {
        LexemeLocation lexemeLocation = myLexemeLocation != null ? myLexemeLocation : new LexemeLocation(mySourceFile, lexer.tokenStartOffset());
        Lexeme<TokenTypeBase> lexeme = new Lexeme<TokenTypeBase>(lexer.tokenType(),
                text.subSequence(lexer.tokenStartOffset(), lexer.tokenEndOffset()).toString(), lexemeLocation);
        buffer.add(lexeme);
      }
      return new LexemeBuffer<TokenTypeBase>(buffer);
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
  }
}
