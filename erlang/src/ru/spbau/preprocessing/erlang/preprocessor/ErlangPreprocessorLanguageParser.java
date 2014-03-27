package ru.spbau.preprocessing.erlang.preprocessor;

import ru.spbau.preprocessing.api.preprocessor.PreprocessorLanguageParser;
import ru.spbau.preprocessing.erlang.ErlangLexer;
import ru.spbau.preprocessing.erlang.ErlangToken;
import ru.spbau.preprocessing.erlang.preprocessor.ast.*;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import static ru.spbau.preprocessing.erlang.ErlangToken.*;

public class ErlangPreprocessorLanguageParser implements PreprocessorLanguageParser {
  @Override
  public List<ErlangPreprocessorNode> parse(CharSequence text) throws IOException {
    ErlangFormsLexer formsLexer = new ErlangFormsLexer(text);
    return parseConditionalCode(text, formsLexer);
  }

  /**
   * Parses forms until first occurrence of -else or -endif.
   * Nested conditionals are parsed recursively.
   */
  private static List<ErlangPreprocessorNode> parseConditionalCode(CharSequence text, ErlangFormsLexer formsLexer) throws IOException {
    ArrayList<ErlangPreprocessorNode> parseResult = new ArrayList<ErlangPreprocessorNode>();
    while (formsLexer.advance() != null) {
      ErlangPreprocessorNode node = parseNode(text, formsLexer);
      if (node instanceof ErlangConditionalAttributeNode) break;
      if (node == null) return null;
      parseResult.add(node);
    }
    return parseResult;
  }

  private static ErlangPreprocessorNode parseNode(CharSequence text, ErlangFormsLexer formsLexer) throws IOException {
    SingleFormParser singleFormParser = new SingleFormParser();
    singleFormParser.reset(text, formsLexer.getFormStart(), formsLexer.getFormEnd());
    ErlangPreprocessorNode preprocessorAttribute = singleFormParser.parsePreprocessorAttribute();
    // it's -ifdef or -ifndef
    if (preprocessorAttribute instanceof ErlangMacroDefinedConditionAttributeNode) {
      ErlangMacroDefinedConditionAttributeNode conditionalGuard = (ErlangMacroDefinedConditionAttributeNode) preprocessorAttribute;
      return parseAlternatives(text, formsLexer, conditionalGuard);
    }
    else {
      return preprocessorAttribute != null ? preprocessorAttribute : singleFormParser.parseCodeNode();
    }
  }

  private static ErlangAlternativesNode parseAlternatives(CharSequence text, ErlangFormsLexer formsLexer,
                                                          ErlangMacroDefinedConditionAttributeNode conditionalGuard) throws IOException {
    ErlangConditionalNode conditionTrueBranch = new ErlangConditionalNode(conditionalGuard, parseConditionalCode(text, formsLexer));
    ErlangConditionalAttributeNode elseOrEndIf = parseElseOrEndIfAttribute(text, formsLexer.getFormStart(), formsLexer.getFormEnd());
    if (elseOrEndIf == null) return null; // ill-formed file
    ErlangConditionalNode conditionFalseBranch = null;
    if (elseOrEndIf.getType() == ErlangConditionalAttributeNode.Type.ELSE) {
      conditionFalseBranch = new ErlangConditionalNode(elseOrEndIf, parseConditionalCode(text, formsLexer));
      elseOrEndIf = parseElseOrEndIfAttribute(text, formsLexer.getFormStart(), formsLexer.getFormEnd());
    }
    return elseOrEndIf != null && elseOrEndIf.getType() == ErlangConditionalAttributeNode.Type.ENDIF ?
            new ErlangAlternativesNode(conditionTrueBranch, conditionFalseBranch, elseOrEndIf) :
            null; //ill-formed
  }

  private static ErlangConditionalAttributeNode parseElseOrEndIfAttribute(CharSequence text, int formStartOffset, int formEndOffset) {
    SingleFormParser singleFormParser = new SingleFormParser();
    singleFormParser.reset(text, formStartOffset, formEndOffset);
    ErlangPreprocessorNode attribute = singleFormParser.parsePreprocessorAttribute();
    return (attribute instanceof ErlangConditionalAttributeNode) ? (ErlangConditionalAttributeNode) attribute : null;
  }

  private static final class SingleFormParser {
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
          return parseInclusion(lexer, ErlangInclusionAttributeNode.ErlangIncludeResolutionStrategy.INCLUDE);
        }
        else if ("include_lib".equals(attr)) {
          return parseInclusion(lexer, ErlangInclusionAttributeNode.ErlangIncludeResolutionStrategy.INCLUDE_LIB);
        }
        else if ("undef".equals(attr)) {
          return parseMacroUndefinition(lexer);
        }
        else if ("define".equals(attr)) {
          return parseMacroDefinition(lexer);
        }
        else if ("ifdef".equals(attr)) {
          return parseMacroDefinedCondition(lexer, true);
        }
        else if ("ifndef".equals(attr)) {
          return parseMacroDefinedCondition(lexer, false);
        }
        else if ("else".equals(attr)) {
          return parseElseOrEndif(lexer, ErlangConditionalAttributeNode.Type.ELSE);
        }
        else if ("endif".equals(attr)) {
          return parseElseOrEndif(lexer, ErlangConditionalAttributeNode.Type.ENDIF);
        }
      } catch (IOException ignored) {
      }
      return null;
    }

    private ErlangInclusionAttributeNode parseInclusion(ErlangLexer lexer,
                                                        ErlangInclusionAttributeNode.ErlangIncludeResolutionStrategy type) throws IOException {
      skipWhitespaceAndComment(lexer);
      if (lexer.tokenType() != PAR_LEFT) return null;
      skipWhitespaceAndComment(lexer);
      if (lexer.tokenType() != STRING) return null;
      String includePath = myText.subSequence(lexer.tokenStartOffset() + 1, lexer.tokenEndOffset() - 1).toString();
      return isRightParDotEndOfForm(lexer) ? new ErlangInclusionAttributeNode(myText, myFormStartOffset, myFormEndOffset, includePath, type) : null;
    }

    private ErlangMacroUndefinitionNode parseMacroUndefinition(ErlangLexer lexer) throws IOException {
      String macroName = parseMacroNameAttribute(lexer);
      return macroName != null ? new ErlangMacroUndefinitionNode(myText, myFormStartOffset, myFormEndOffset, macroName) : null;
    }

    private ErlangMacroDefinitionNode parseMacroDefinition(ErlangLexer lexer) throws IOException {
      skipWhitespaceAndComment(lexer);
      if (lexer.tokenType() != PAR_LEFT) return null;
      skipWhitespaceAndComment(lexer);
      String macroName = macroName(lexer);
      if (macroName == null) return null;
      skipWhitespaceAndComment(lexer);
      List<String> parameterNames = null;
      if (lexer.tokenType() == PAR_LEFT) {
        parameterNames = new ArrayList<String>();
        while (true) {
          skipWhitespaceAndComment(lexer);
          if (lexer.tokenType() == VAR) {
            parameterNames.add(lexer.yytext());
            skipWhitespaceAndComment(lexer);
            if (lexer.tokenType() == COMMA) continue;
          }
          if (lexer.tokenType() == PAR_RIGHT) {
            skipWhitespaceAndComment(lexer);
            break;
          }
          else {
            return null;
          }
        }
      }
      if (lexer.tokenType() != COMMA) {
        return null;
      }
      lexer.advance();

      //consume macro body
      int macroBodyStartOffset = lexer.tokenStartOffset();
      int macroBodyEndOffset = -1;
      while (lexer.tokenType() != null) {
        int tokenOffset = lexer.tokenStartOffset();
        if (lexer.tokenType() == PAR_RIGHT) {
          skipWhitespaceAndComment(lexer);
          if (lexer.tokenType() == DOT && lexer.next() == null) {
            macroBodyEndOffset = tokenOffset;
          }
        }
        else {
          lexer.advance();
        }
      }

      return macroBodyEndOffset >= macroBodyStartOffset ?
              new ErlangMacroDefinitionNode(myText, myFormStartOffset, myFormEndOffset, macroName, parameterNames, macroBodyStartOffset, macroBodyEndOffset) :
              null;
    }

    private ErlangMacroDefinedConditionAttributeNode parseMacroDefinedCondition(ErlangLexer lexer, boolean isPositive) throws IOException {
      String macroName = parseMacroNameAttribute(lexer);
      return macroName != null ?
              new ErlangMacroDefinedConditionAttributeNode(myText, myFormStartOffset, myFormEndOffset, macroName, isPositive) :
              null;
    }

    private ErlangConditionalAttributeNode parseElseOrEndif(ErlangLexer lexer, ErlangConditionalAttributeNode.Type type) throws IOException {
      skipWhitespaceAndComment(lexer);
      return lexer.tokenType() == DOT && lexer.next() == null ?
              new ErlangConditionalAttributeNode(myText, myFormStartOffset, myFormEndOffset, type) :
              null;
    }

    private String parseMacroNameAttribute(ErlangLexer lexer) throws IOException {
      skipWhitespaceAndComment(lexer);
      if (lexer.tokenType() != PAR_LEFT) return null;
      skipWhitespaceAndComment(lexer);
      String macroName = macroName(lexer);
      return isRightParDotEndOfForm(lexer) && macroName != null ? macroName : null;
    }

    private String macroName(ErlangLexer lexer) {
      //TODO quoted macro names
      return lexer.tokenType() == ATOM || lexer.tokenType() == VAR ? lexer.yytext() : null;
    }

    private boolean isRightParDotEndOfForm(ErlangLexer lexer) throws IOException {
      skipWhitespaceAndComment(lexer);
      if (lexer.tokenType() != PAR_RIGHT) return false;
      skipWhitespaceAndComment(lexer);
      return lexer.tokenType() == DOT && lexer.next() == null;
    }

    private static void skipWhitespaceAndComment(ErlangLexer lexer) throws IOException {
      ErlangToken t = lexer.next();
      while (isWhitespaceOrComment(t)) {
        t = lexer.next();
      }
    }

    private static boolean isWhitespaceOrComment(ErlangToken token) {
      return token == WHITESPACE || token == COMMENT;
    }
  }
}
