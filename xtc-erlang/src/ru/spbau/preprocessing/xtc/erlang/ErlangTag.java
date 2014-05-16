package ru.spbau.preprocessing.xtc.erlang;

import xtc.lang.cpp.Syntax;

public enum ErlangTag implements Syntax.LanguageTag {
  AFTER("after"),
  AND("and"),
  ANDALSO("andalso"),
  ARROW("->"),
  ASSOC("=>"),
  ATOM(null, true),
  BAND("band"),
  BEGIN("begin"),
  BIN_END(">>"),
  BIN_START("<<"),
  BNOT("bnot"),
  BOR("bor"),
  BRACKET_LEFT("["),
  BRACKET_RIGHT("]"),
  BSL("bsl"),
  BSR("bsr"),
  BXOR("bxor"),
  CASE("case"),
  CATCH("catch"),
  CHAR(null, false),
  COLON(":"),
  COLON_COLON("::"),
  COMMA(",", false, Syntax.PreprocessorTag.COMMA),
  CURLY_LEFT("{"),
  CURLY_RIGHT("}"),
  DIV("div"),
  DOT("."),
  DOT_DOT(".."),
  DOT_DOT_DOT("..."),
  END("end"),
  FLOAT(null, false),
  FUN("fun"),
  IF("if"),
  INTEGER(null, false),
  MATCH(":="),
  NOT("not"),
  OF("of"),
  OP_AR_DIV("/"),
  OP_AR_MUL("*"),
  OP_DIV_EQ("/="),
  OP_EQ("="),
  OP_EQ_COL_EQ("=:="),
  OP_EQ_DIV_EQ("=/="),
  OP_EQ_EQ("=="),
  OP_EQ_LT("=<"),
  OP_EXL("!"),
  OP_GT(">"),
  OP_GT_EQ("<="),
  OP_LT("<"),
  OP_LT_EQ("<="),
  OP_LT_MINUS("<-"),
  OP_MINUS("-"),
  OP_MINUS_MINUS("--"),
  OP_OR("|"),
  OP_PLUS("+"),
  OP_PLUS_PLUS("++"),
  OR("or"),
  ORELSE("orelse"),
  OR_OR("||"),
  PAR_LEFT("(", false, Syntax.PreprocessorTag.OPEN_PAREN),
  PAR_RIGHT(")", false, Syntax.PreprocessorTag.CLOSE_PAREN),
  QMARK("?"),
  RADIX("#"),
  RECEIVE("receive"),
  REM("rem"),
  SEMI(";"),
  STRING(null, false),
  TRY("try"),
  VAR(null, true),
  WHEN("when"),
  XOR("xor"),

  //TODO make sure you need these
  WHITESPACE(null, false),
  COMMENT(null, false);


  private final int myId;
  private final String myText;
  private final boolean myHasName;
  private final Syntax.PreprocessorTag myPreprocessorTag;

  ErlangTag(String myText) {
    this(myText, false);
  }

  ErlangTag(String myText, boolean myHasName) {
    this(myText, myHasName, Syntax.PreprocessorTag.NONE);
  }

  ErlangTag(String myText, boolean myHasName, Syntax.PreprocessorTag myPreprocessorTag) {
    this.myId = getId(this.toString());
    this.myText = myText;
    this.myHasName = myHasName;
    this.myPreprocessorTag = myPreprocessorTag;
  }

  @Override
  public int getID() {
    return myId;
  }

  @Override
  public String getText() {
    return myText;
  }

  @Override
  public boolean hasName() {
    return myHasName;
  }

  @Override
  public Syntax.PreprocessorTag ppTag() {
    return myPreprocessorTag;
  }

  private static int getId(String token) {
    //TODO implement me


    return -1;
  }
}
