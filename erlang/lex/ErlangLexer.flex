package ru.spbau.preprocessing.erlang;

import java.io.IOException;

import static ru.spbau.preprocessing.erlang.ErlangToken.*;

%%

%{
  private ErlangToken myTokenType;

  // implementation of ru.spbau.preprocessing.api.LanguageLexer<ErlangToken>

  public void advance() throws IOException {
    myTokenType = next();
  }

  public ErlangToken tokenType() {
    return myTokenType;
  }

  public int tokenStartOffset() {
    return zzStartRead;
  }

  public int tokenEndOffset() {
    return zzMarkedPos;
  }

  //TODO make sure it works (it may not)
  public void start(CharSequence buffer, int start, int end) {
    zzBuffer = buffer.toString().toCharArray();
    zzCurrentPos = zzMarkedPos = zzStartRead = start;
    zzAtEOF  = false;
    zzAtBOL = true;
    zzEndRead = end;
    yybegin(YYINITIAL);
    myTokenType = null;
  }
%}

%class ErlangLexer
%implements ru.spbau.preprocessing.api.LanguageLexer<ErlangToken>
%unicode
%public

%function next
%type ErlangToken

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

Variable = (_ {NameChars}) | ({ErlangUppercase} {NameChars}?)

UniversalPattern = _

%%
 {Comment}                     { return COMMENT; }
 {Whitespace}                  { return WHITESPACE; }

 "after"                       { return AFTER; }
 "when"                        { return WHEN; }
 "begin"                       { return BEGIN; }
 "end"                         { return END; }
 "of"                          { return OF; }
 "case"                        { return CASE; }
 "fun"                         { return FUN; }
 "try"                         { return TRY; }
 "catch"                       { return CATCH; }
 "if"                          { return IF; }
 "receive"                     { return RECEIVE; }

 ":="                          { return MATCH; }
 "=>"                          { return ASSOC; }

 "<<"                           { return BIN_START; }
 ">>"                           { return BIN_END; }
 "+"                            { return OP_PLUS; }
 "-"                            { return OP_MINUS; }
 "*"                            { return OP_AR_MUL; }
 "/"                            { return OP_AR_DIV; }
 "div"                          { return DIV; }
 "rem"                          { return REM; }
 "or"                           { return OR; }
 "xor"                          { return XOR; }
 "bor"                          { return BOR; }
 "bxor"                         { return BXOR; }
 "bsl"                          { return BSL; }
 "bsr"                          { return BSR; }
 "and"                          { return AND; }
 "band"                         { return BAND; }
 "=="                           { return OP_EQ_EQ; }
 "/="                           { return OP_DIV_EQ; }
 "=:="                          { return OP_EQ_COL_EQ; }
 "=/="                          { return OP_EQ_DIV_EQ; }
 "<"                            { return OP_LT; }
 "=<"                           { return OP_EQ_LT; }
 ">"                            { return OP_GT; }
 ">="                           { return OP_GT_EQ; }
 "not"                          { return NOT; }
 "bnot"                         { return BNOT; }
 "++"                           { return OP_PLUS_PLUS; }
 "--"                           { return OP_MINUS_MINUS; }
 "="                            { return OP_EQ; }
 "!"                            { return OP_EXL; }
 "<-"                           { return OP_LT_MINUS; }
 "<="                           { return OP_LT_EQ; }
 "andalso"                      { return ANDALSO; }
 "orelse"                       { return ORELSE; }

 {IntegerLiteral}              { return INTEGER; }
 {FloatLiteral}                { return FLOAT; }
 {UniversalPattern}            { return UNI_PATTERN; }

 {CharLiteral}                 { return CHAR; }
 {StringLiteral}               { return STRING; }
 {AtomLiteral}                 { return ATOM; }
 {Variable}                    { return VAR; }

  "("                           { return PAR_LEFT; }
  ")"                           { return PAR_RIGHT; }
  "{"                           { return CURLY_LEFT; }
  "}"                           { return CURLY_RIGHT; }
  "["                           { return BRACKET_LEFT; }
  "]"                           { return BRACKET_RIGHT; }
  "."                           { return DOT; }
  ".."                          { return DOT_DOT; }
  "..."                         { return DOT_DOT_DOT; }
  ":"                           { return COLON; }
  "::"                          { return COLON_COLON; }
  "||"                          { return OR_OR; }
  "|"                           { return OP_OR; }
  ";"                           { return SEMI; }
  ","                           { return COMMA; }
  "?"                           { return QMARK; }
  "->"                          { return ARROW; }
  "#"                           { return RADIX; }
