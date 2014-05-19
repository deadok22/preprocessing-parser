
package ru.spbau.preprocessing.xtc.erlang;

import xtc.lang.cpp.Syntax.Layout;
import xtc.lang.cpp.Syntax.PreprocessorTag;
import xtc.lang.cpp.Syntax.Language;

public enum ErlangTag implements xtc.lang.cpp.Syntax.LanguageTag {


AFTER(getID("AFTER"), "after"),
WHEN(getID("WHEN"), "when"),
BEGIN(getID("BEGIN"), "begin"),
END(getID("END"), "end"),
OF(getID("OF"), "of"),
CASE(getID("CASE"), "case"),
FUN(getID("FUN"), "fun"),
TRY(getID("TRY"), "try"),
CATCH(getID("CATCH"), "catch"),
IF(getID("IF"), "if"),
RECEIVE(getID("RECEIVE"), "receive"),

MATCH(getID("MATCH"), ":="),
ASSOC(getID("ASSOC"), "=>"),

BIN_START(getID("BIN_START"), "<<"),
BIN_END(getID("BIN_END"), ">>"),
OP_PLUS(getID("OP_PLUS"), "+"),
OP_MINUS(getID("OP_MINUS"), "-"),
OP_AR_MUL(getID("OP_AR_MUL"), "*"),
OP_AR_DIV(getID("OP_AR_DIV"), "/"),
DIV(getID("DIV"), "div"),
REM(getID("REM"), "rem"),
OR(getID("OR"), "or"),
XOR(getID("XOR"), "xor"),
BOR(getID("BOR"), "bor"),
BXOR(getID("BXOR"), "bxor"),
BSL(getID("BSL"), "bsl"),
BSR(getID("BSR"), "bsr"),
AND(getID("AND"), "and"),
BAND(getID("BAND"), "band"),
OP_EQ_EQ(getID("OP_EQ_EQ"), "=="),
OP_DIV_EQ(getID("OP_DIV_EQ"), "/="),
OP_EQ_COL_EQ(getID("OP_EQ_COL_EQ"), "=:="),
OP_EQ_DIV_EQ(getID("OP_EQ_DIV_EQ"), "=/="),
OP_LT(getID("OP_LT"), "<"),
OP_EQ_LT(getID("OP_EQ_LT"), "=<"),
OP_GT(getID("OP_GT"), ">"),
OP_GT_EQ(getID("OP_GT_EQ"), ">="),
NOT(getID("NOT"), "not"),
BNOT(getID("BNOT"), "bnot"),
OP_PLUS_PLUS(getID("OP_PLUS_PLUS"), "++"),
OP_MINUS_MINUS(getID("OP_MINUS_MINUS"), "--"),
OP_EQ(getID("OP_EQ"), "="),
OP_EXL(getID("OP_EXL"), "!"),
OP_LT_MINUS(getID("OP_LT_MINUS"), "<-"),
OP_LT_EQ(getID("OP_LT_EQ"), "<="),
ANDALSO(getID("ANDALSO"), "andalso"),
ORELSE(getID("ORELSE"), "orelse"),

 INTEGER(getID("INTEGER"), null, false),
 FLOAT(getID("FLOAT"), null, false),
 CHAR(getID("CHAR"), null, false),
 STRING(getID("STRING"), null, false),

 ATOM(getID("ATOM"), null, true),
 VAR(getID("VAR"), null, true),

 PAR_LEFT(getID("PAR_LEFT"), "("),
 PAR_RIGHT(getID("PAR_RIGHT"), ")"),
 CURLY_LEFT(getID("CURLY_LEFT"), "{"),
 CURLY_RIGHT(getID("CURLY_RIGHT"), "}"),
 BRACKET_LEFT(getID("BRACKET_LEFT"), "["),
 BRACKET_RIGHT(getID("BRACKET_RIGHT"), "]"),
 DOT(getID("DOT"), "."),
 DOT_DOT(getID("DOT_DOT"), ".."),
 DOT_DOT_DOT(getID("DOT_DOT_DOT"), "..."),
 COLON(getID("COLON"), ":"),
 COLON_COLON(getID("COLON_COLON"), "::"),
 OR_OR(getID("OR_OR"), "||"),
 OP_OR(getID("OP_OR"), "|"),
 SEMI(getID("SEMI"), ";"),
 COMMA(getID("COMMA"), ","),
 QMARK(getID("QMARK"), "?"),
 ARROW(getID("ARROW"), "->"),
 RADIX(getID("RADIX"), "#"),


  ;
  private final int id;
  private final String text;
  private final boolean hasName;
  private final PreprocessorTag ppTag;

  ErlangTag(int id, String text, boolean hasName, PreprocessorTag ppTag) {
    this.id = id;
    this.text = text;
    this.hasName = hasName;
    this.ppTag = ppTag;
  }

  ErlangTag(int id, String text, boolean hasName) {
    this(id, text, hasName, PreprocessorTag.NONE);
  }

  ErlangTag(int id, String text) {
    this(id, text, isName(text), PreprocessorTag.NONE);
  }

  ErlangTag(int id, String text, PreprocessorTag ppTag) {
    this(id, text, isName(text), ppTag);
  }

  public int getID() {
    return id;
  }

  public String getText() {
    return text;
  }

  public boolean hasName() {
    return hasName;
  }

  public PreprocessorTag ppTag() {
    return ppTag;
  }

  static boolean isName(String name) {
    if (null == name || name.length() == 0) return false;

    return Character.isLetter(name.charAt(0)) || '_' == name.charAt(0);
  }

  static int getID(String token) {
    for (int id = 0; id < ErlangForkMergeParserTables.YYNTOKENS; id++) {
      if (ErlangForkMergeParserTables.yytname.table[id].equals(token)) {
        return id;
      }
    }

    System.err.println("error: invalid token name");
    System.exit(1);

    return -1;
  }

}
