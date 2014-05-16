
# define LANGUAGE(token, text)                                        \
  text {                                                              \
    Language<ErlangTag> syntax = new Language<ErlangTag>(token);      \
    syntax.setLocation(new Location(fileName, yyline+1, yycolumn+1)); \
    return syntax;                                                    \
  }

# define TEXT(token, regex, hasName)                                \
  regex {                                                           \
    Text<ErlangTag> syntax = new Text<ErlangTag>(token, yytext());  \
    syntax.setLocation(new Location(fileName, yyline+1, yycolumn)); \
    return syntax;                                                  \
  }


package ru.spbau.preprocessing.xtc.erlang;

import xtc.tree.Location;

import xtc.lang.cpp.Syntax.Language;
import xtc.lang.cpp.Syntax.PreprocessorTag;
import xtc.lang.cpp.Syntax.Text;
import xtc.lang.cpp.Syntax.Layout;
import xtc.lang.cpp.Syntax.EOF;
import xtc.lang.cpp.Syntax;

import static ru.spbau.preprocessing.xtc.erlang.ErlangTag.*;

%%

%{

  /** Used to build strings of comments. */
  private StringBuilder comment = null;

  /** Used to track whether comments have newline characters. */
  private boolean commentnl = false;

  /** The name of the file being lexed. */
  private String fileName = null;

  /** Returns the name of the file being lexed. */
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }


%}

%class ErlangLexer
%unicode
%public

%function _next
%type Syntax

%table

/* This hex range is the same as octal \O00 - \O37 */
ControlCharacter = [\000 - \037]

CommentLine = "%"[^\r\n]*
Comment = {CommentLine} {CommentLine}*
Whitespace = ([ \t\n] | {ControlCharacter})+
ErlangUppercase = [A-Z]
ErlangLowercase = [a-z]
ErlangLetter = {ErlangUppercase} | {ErlangLowercase}
ErlangDigit = [0-9]
InputCharacter = [^\n]

DecimalLiteral = [0-9]+
ExplicitRadixLiteral = [0-9]{1,2} "#" [0-9a-fA-F]+

IntegerLiteral = {DecimalLiteral} | {ExplicitRadixLiteral}

ExponentPart = [Ee] [+-]? {DecimalLiteral}
FloatLiteral = {DecimalLiteral} "." {DecimalLiteral} {ExponentPart}?


OctalEscape = \\ [0-7]{1,3}
ControlName = [@A-Z\[\\\]\^_] /* this is the octal range \100 - \137 */
ControlEscape = \\ \^ {ControlName}
EscapeSequence = \\\" | "\\b" | "\\d" | "\\e" | "\\f" | "\\n" | "\\r" | "\\s" | "\\t" | "\\v" | "\\'" | "\\\\" | "\\[" | "\\{" | "\\]" | "\\}" | "\\`" | "\\$" | "\\=" | "\\%" | "\\," | "\\." | "\\_" | {ControlEscape} | {OctalEscape}

CharLiteralChar = {InputCharacter} | {EscapeSequence}
CharLiteral = \$ {CharLiteralChar} | \$

/* Without the \\\" at the start the lexer won't find it, for unknown reasons */
ESC = "\\" ( [^] )
CHAR = {ESC} | [^\'\"\\]
STRING_BAD1 = \" ({CHAR} | \') *
StringLiteral = {STRING_BAD1} \"

NameChar = {ErlangLetter} | {ErlangDigit} | @ | _
NameChars = {NameChar}+

QuotedCharacter = \\' | {EscapeSequence}  | [^'\\] /* [a-zA-Z0-9#_.@,;:!?/&%$+*~\^-] */
AtomLiteral = ({ErlangLowercase} {NameChar}*) | "''" | (' {QuotedCharacter}+ ')

Variable = (_ {NameChars}) | ({ErlangUppercase} {NameChars}?) | _

%%
 {Comment}                     { return new Layout(yytext(), false); }
 {Whitespace}                  { return new Layout(yytext(), false); }

LANGUAGE(AFTER, "after")
LANGUAGE(WHEN, "when")
LANGUAGE(BEGIN, "begin")
LANGUAGE(END, "end")
LANGUAGE(OF, "of")
LANGUAGE(CASE, "case")
LANGUAGE(FUN, "fun")
LANGUAGE(TRY, "try")
LANGUAGE(CATCH, "catch")
LANGUAGE(IF, "if")
LANGUAGE(RECEIVE, "receive")

LANGUAGE(MATCH, ":=")
LANGUAGE(ASSOC, "=>")

LANGUAGE(BIN_START, "<<")
LANGUAGE(BIN_END, ">>")
LANGUAGE(OP_PLUS, "+")
LANGUAGE(OP_MINUS, "-")
LANGUAGE(OP_AR_MUL, "*")
LANGUAGE(OP_AR_DIV, "/")
LANGUAGE(DIV, "div")
LANGUAGE(REM, "rem")
LANGUAGE(OR, "or")
LANGUAGE(XOR, "xor")
LANGUAGE(BOR, "bor")
LANGUAGE(BXOR, "bxor")
LANGUAGE(BSL, "bsl")
LANGUAGE(BSR, "bsr")
LANGUAGE(AND, "and")
LANGUAGE(BAND, "band")
LANGUAGE(OP_EQ_EQ, "==")
LANGUAGE(OP_DIV_EQ, "/=")
LANGUAGE(OP_EQ_COL_EQ, "=:=")
LANGUAGE(OP_EQ_DIV_EQ, "=/=")
LANGUAGE(OP_LT, "<")
LANGUAGE(OP_EQ_LT, "=<")
LANGUAGE(OP_GT, ">")
LANGUAGE(OP_GT_EQ, ">=")
LANGUAGE(NOT, "not")
LANGUAGE(BNOT, "bnot")
LANGUAGE(OP_PLUS_PLUS, "++")
LANGUAGE(OP_MINUS_MINUS, "--")
LANGUAGE(OP_EQ, "=")
LANGUAGE(OP_EXL, "!")
LANGUAGE(OP_LT_MINUS, "<-")
LANGUAGE(OP_LT_EQ, "<=")
LANGUAGE(ANDALSO, "andalso")
LANGUAGE(ORELSE, "orelse")

 TEXT(INTEGER,{IntegerLiteral},false)
 TEXT(FLOAT,{FloatLiteral},false)
 TEXT(CHAR,{CharLiteral},false)
 TEXT(STRING,{StringLiteral},false)

 TEXT(ATOM,{AtomLiteral},true)
 TEXT(VAR,{Variable},true)

 LANGUAGE(PAR_LEFT, "(")
 LANGUAGE(PAR_RIGHT, ")")
 LANGUAGE(CURLY_LEFT, "{")
 LANGUAGE(CURLY_RIGHT, "}")
 LANGUAGE(BRACKET_LEFT, "[")
 LANGUAGE(BRACKET_RIGHT, "]")
 LANGUAGE(DOT, ".")
 LANGUAGE(DOT_DOT, "..")
 LANGUAGE(DOT_DOT_DOT, "...")
 LANGUAGE(COLON, ":")
 LANGUAGE(COLON_COLON, "::")
 LANGUAGE(OR_OR, "||")
 LANGUAGE(OP_OR, "|")
 LANGUAGE(SEMI, ";")
 LANGUAGE(COMMA, ",")
 LANGUAGE(QMARK, "?")
 LANGUAGE(ARROW, "->")
 LANGUAGE(RADIX, "#")
