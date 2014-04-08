package ru.spbau.preprocessing.erlang.preprocessor.macros;

import com.google.common.collect.ImmutableMap;
import ru.spbau.preprocessing.api.macros.MacroCall;
import ru.spbau.preprocessing.api.macros.MacroCallParser;
import ru.spbau.preprocessing.api.macros.MacroCallParserState;
import ru.spbau.preprocessing.erlang.ErlangToken;
import ru.spbau.preprocessing.lexer.lexemegraph.Lexeme;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static ru.spbau.preprocessing.erlang.ErlangToken.*;

public final class ErlangMacroCallParser implements MacroCallParser<ErlangToken> {
  private static ImmutableMap<ErlangToken, ErlangToken> BRACES =
          ImmutableMap.<ErlangToken, ErlangToken>builder()
                  .put(PAR_LEFT, PAR_RIGHT)
                  .put(CURLY_LEFT, CURLY_RIGHT)
                  .put(BRACKET_LEFT, BRACKET_RIGHT)
                  .put(BIN_START, BIN_END)
                  .put(IF, END)
                  .put(CASE, END)
                  .put(BEGIN, END)
                  .put(TRY, END)
                  .put(FUN, END) // this is incorrect for 'fun mod:foo/0' expressions
                  .put(RECEIVE, END)
                  .build();

  private final boolean myIsFunctionLikeCallParser;
  private final Deque<ErlangToken> myRightBracesStack = new ArrayDeque<ErlangToken>();
  private boolean myIsFailed;
  private int myLexemesConsumed;
  private Expectation myExpectation;
  private String myMacroName;
  private List<List<Lexeme<ErlangToken>>> myCallArguments;
  private List<Lexeme<ErlangToken>> myCurrentArgument;

  public ErlangMacroCallParser(boolean parseArguments) {
    myIsFunctionLikeCallParser = parseArguments;
    reset();
  }

  @Override
  public void reset() {
    myRightBracesStack.clear();
    myIsFailed = false;
    myLexemesConsumed = 0;
    myMacroName = null;
    myCallArguments = null;
    myExpectation = Expectation.QMARK;
    myCurrentArgument = null;
  }

  @Override
  public void consumeLexeme(Lexeme<ErlangToken> lexeme) {
    assert getState() == MacroCallParserState.PARSING;

    ErlangToken token = lexeme.getType();
    switch (myExpectation) {
      case QMARK: {
        if (token == QMARK) {
          myExpectation = Expectation.MACRO_NAME;
        }
        else myIsFailed = true;
        break;
      }
      case MACRO_NAME: {
        if (token == WHITESPACE || token == COMMENT) break;
        if (token == ATOM || token == VAR) {
          myMacroName = lexeme.getText();
          myExpectation = myIsFunctionLikeCallParser ? Expectation.ARGS_LIST : Expectation.NOTHING;
        }
        else myIsFailed = true;
        break;
      }
      case ARGS_LIST: {
        if (token == WHITESPACE || token == COMMENT) break;
        if (token == PAR_LEFT) {
          myExpectation = Expectation.ARGUMENT;
        }
        break;
      }
      case ARGUMENT: {
        if (myRightBracesStack.isEmpty() && (token == COMMA || token == PAR_RIGHT)) {
          completeArgument();
          if (token == COMMA) {
            myExpectation = Expectation.ARGUMENT;
            myCurrentArgument = new ArrayList<Lexeme<ErlangToken>>();
          }
          else {
            myExpectation = Expectation.NOTHING;
          }
        }
        else {
          if (BRACES.containsKey(token)) {
            myRightBracesStack.add(BRACES.get(token));
          }
          else if (myRightBracesStack.peekLast() == token) {
            myRightBracesStack.removeLast();
          }
          argumentLexeme(lexeme);
        }
        break;
      }
      default:
        throw new AssertionError("Unexpected token type");
    }
    myLexemesConsumed++;
  }

  @Override
  public MacroCallParserState getState() {
    if (myIsFailed) return MacroCallParserState.NOT_PARSED;
    if (myExpectation == Expectation.NOTHING) return MacroCallParserState.PARSED;
    return MacroCallParserState.PARSING;
  }

  @Override
  public MacroCall<ErlangToken> getParsedCall() {
    if (getState() != MacroCallParserState.PARSED) return null;
    return new ErlangMacroCall(myMacroName, myCallArguments, myLexemesConsumed);
  }

  private void argumentLexeme(Lexeme<ErlangToken> lexeme) {
    if (myCurrentArgument == null) {
      myCurrentArgument = new ArrayList<Lexeme<ErlangToken>>();
    }
    myCurrentArgument.add(lexeme);
  }

  private void completeArgument() {
    if (myCallArguments == null) {
      myCallArguments = new ArrayList<List<Lexeme<ErlangToken>>>();
    }
    if (myCurrentArgument != null) {
      myCallArguments.add(myCurrentArgument);
      myCurrentArgument = null;
    }
  }

  private static enum Expectation {
    QMARK, MACRO_NAME, ARGS_LIST, ARGUMENT, NOTHING
  }
}
