package ru.spbau.preprocessing.erlang.preprocessor;

import ru.spbau.preprocessing.api.preprocessor.PreprocessorLanguageNode;
import ru.spbau.preprocessing.api.preprocessor.PreprocessorLanguageParser;
import ru.spbau.preprocessing.erlang.ErlangLexer;
import ru.spbau.preprocessing.erlang.ErlangToken;
import ru.spbau.preprocessing.erlang.preprocessor.ast.ErlangInclusionAttribute;
import ru.spbau.preprocessing.erlang.preprocessor.ast.ErlangMacroUndefinitionNode;
import ru.spbau.preprocessing.erlang.preprocessor.ast.ErlangPreprocessorNode;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import static ru.spbau.preprocessing.erlang.ErlangToken.*;

public class ErlangPreprocessorLanguageParser implements PreprocessorLanguageParser {
  @Override
  public List<PreprocessorLanguageNode> parse(CharSequence text) throws IOException {
    ArrayList<PreprocessorLanguageNode> parseResult = new ArrayList<PreprocessorLanguageNode>();
    ErlangFormsLexer formsLexer = new ErlangFormsLexer(text);
    SingleFormParser singleFormParser = new SingleFormParser();
    while (formsLexer.advance() != null) {
      singleFormParser.reset(text, formsLexer.getFormStart(), formsLexer.getFormEnd());
      //TODO try parse preprocessor directive
      //no preprocessor directive parsed - this is a code node
      parseResult.add(singleFormParser.parseCodeNode());
    }
    return parseResult;
  }

  private final class SingleFormParser {
    private CharSequence myText;
    private int myFormStartOffset;
    private int myFormEndOffset;

    public void reset(CharSequence text, int formStartOffset, int formEndOffset) {
      myText = text;
      myFormStartOffset = formStartOffset;
      myFormEndOffset = formEndOffset;
    }

    public ErlangPreprocessorNode parseCodeNode() {
      return new ErlangPreprocessorNode(myText, myFormStartOffset, myFormEndOffset);
    }

    public ErlangPreprocessorNode parsePreprocessorAttribute() {
      ErlangLexer lexer = new ErlangLexer((Reader) null);
      lexer.start(myText, myFormStartOffset, myFormEndOffset);
      try {
        ErlangToken token = lexer.next();
        if (token != OP_MINUS) return null;
        skipWhitespaceAndComment(lexer);
        token = lexer.tokenType();
        if (token != ATOM) return null;
        String attr = lexer.yytext();
        if ("include".equals(attr)) {
          return parseInclusion(lexer, ErlangInclusionAttribute.ErlangIncludeResolutionStrategy.INCLUDE);
        }
        else if ("include_lib".equals(attr)) {
          return parseInclusion(lexer, ErlangInclusionAttribute.ErlangIncludeResolutionStrategy.INCLUDE_LIB);
        }
        else if ("undef".equals(attr)) {
          return parseMacroUndefinition(lexer);
        }
        else if ("ifdef".equals(attr)) {
          //TODO
          throw new UnsupportedOperationException("Not implemented");
        }
        else if ("ifndef".equals(attr)) {
          //TODO
          throw new UnsupportedOperationException("Not implemented");
        }
        //TODO these should only be allowed after -ifdef or -ifndef
        else if ("else".equals(attr)) {
          //TODO
          throw new UnsupportedOperationException("Not implemented");
        }
        else if ("endif".equals(attr)) {
          //TODO
          throw new UnsupportedOperationException("Not implemented");
        }
        else {
          return null;
        }
      } catch (IOException e) {
        return null;
      }
    }

    private ErlangInclusionAttribute parseInclusion(ErlangLexer lexer,
                                                    ErlangInclusionAttribute.ErlangIncludeResolutionStrategy type) throws IOException {
      skipWhitespaceAndComment(lexer);
      if (PAR_LEFT != lexer.tokenType()) return null;
      skipWhitespaceAndComment(lexer);
      if (STRING != lexer.tokenType()) return null;
      String includePath = myText.subSequence(lexer.tokenStartOffset() + 1, lexer.tokenEndOffset() - 1).toString();
      return isRightParDotEndOfForm(lexer) ? new ErlangInclusionAttribute(myText, myFormStartOffset, myFormEndOffset, includePath, type) : null;
    }

    private ErlangMacroUndefinitionNode parseMacroUndefinition(ErlangLexer lexer) throws IOException {
      String macroName = parseMacroNameAttribute(lexer);
      return macroName != null ? new ErlangMacroUndefinitionNode(myText, myFormStartOffset, myFormEndOffset, macroName) : null;
    }

    private String parseMacroNameAttribute(ErlangLexer lexer) throws IOException {
      skipWhitespaceAndComment(lexer);
      if (PAR_LEFT != lexer.tokenType()) return null;
      skipWhitespaceAndComment(lexer);
      if (ATOM != lexer.tokenType() && VAR != lexer.tokenType()) return null;
      //TODO quoted macro names
      String macroName = lexer.yytext();
      return isRightParDotEndOfForm(lexer) ? macroName : null;
    }

    private boolean isRightParDotEndOfForm(ErlangLexer lexer) throws IOException {
      skipWhitespaceAndComment(lexer);
      if (PAR_RIGHT != lexer.tokenType()) return false;
      skipWhitespaceAndComment(lexer);
      return DOT == lexer.tokenType() && lexer.next() == null;
    }
  }

  private static void skipWhitespaceAndComment(ErlangLexer lexer) throws IOException {
    ErlangToken t = lexer.next();
    while (t == WHITESPACE || t == COMMENT) {
      t = lexer.next();
    }
  }
}
