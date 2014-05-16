package xtc.lang.cpp;

import ru.spbau.preprocessing.xtc.erlang.ErlangTag;

import java.io.IOException;

public class ErlangTokenCreator implements TokenCreator {
  @Override
  public Syntax.Language<?> createStringLiteral(String str) {
    return new Syntax.Text<ErlangTag>(ErlangTag.STRING, str);
  }

  @Override
  public Syntax.Language<?> createIntegerConstant(int i) {
    return new Syntax.Text<ErlangTag>(ErlangTag.INTEGER, Integer.toString(i));
  }

  @Override
  public Syntax.Language<?> createIdentifier(String ident) {
    //TODO use VAR or ATOM depending on input text
    return new Syntax.Text<ErlangTag>(ErlangTag.ATOM, ident);
  }

  @Override
  public Syntax.Language<?> pasteTokens(Syntax.Language<?> t1, Syntax.Language<?> t2) throws IOException {
    throw new UnsupportedOperationException("Token pasting for Erlang is not implemented");
  }
}