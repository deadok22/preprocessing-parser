package ru.spbau.preprocessing.erlang.preprocessor.macros;

import ru.spbau.preprocessing.api.macros.MacroCall;
import ru.spbau.preprocessing.erlang.ErlangToken;
import ru.spbau.preprocessing.lexer.lexemegraph.Lexeme;

import java.util.List;

public class ErlangMacroCall implements MacroCall<ErlangToken> {
  private final String myMacroName;
  private final List<List<Lexeme<ErlangToken>>> myArgumentList;
  private final int myLexemesCount;

  public ErlangMacroCall(String macroName, List<List<Lexeme<ErlangToken>>> argumentList, int lexemesCount) {
    myMacroName = macroName;
    myArgumentList = argumentList;
    myLexemesCount = lexemesCount;
  }

  @Override
  public String getMacroName() {
    return myMacroName;
  }

  @Override
  public int getArity() {
    return myArgumentList == null ? -1 : myArgumentList.size();
  }

  @Override
  public List<List<Lexeme<ErlangToken>>> getArgumentsList() {
    return myArgumentList;
  }

  @Override
  public int getLexemesCount() {
    return myLexemesCount;
  }
}
