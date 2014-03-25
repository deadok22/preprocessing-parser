package ru.spbau.preprocessing.erlang.preprocessor;

/*
 * This lexer returns a lexeme per erlang form, whitespace or comment outside erlang form.
 */

%%

%{
  static final int ERLANG_FORM = 0;

  public ErlangFormsLexer(CharSequence text) {
    this(new java.io.StringReader(text.toString()));
  }
%}


%class ErlangFormsLexer
%unicode
%public

%function advance
%type Integer

%table
ControlCharacter = [\000 - \037]

Comment = "%"[^\r\n]*

WhitespaceChar = [ \t\n] | {ControlCharacter}
Whitespace = {WhitespaceChar}+

ESC = "\\" ( [^] )
CHAR = {ESC} | [^\'\"\\]
STRING_BAD1 = \" ({CHAR} | \') *
StringLiteral = {STRING_BAD1} \"

OctalEscape = \\ [0-7]{1,3}
ControlName = [@A-Z\[\\\]\^_] /* this is the octal range \100 - \137 */
ControlEscape = \\ \^ {ControlName}
EscapeSequence = \\\" | "\\b" | "\\d" | "\\e" | "\\f" | "\\n" | "\\r" | "\\s" | "\\t" | "\\v" | "\\'" | "\\\\" | "\\[" | "\\{" | "\\]" | "\\}" | "\\`" | "\\$" | "\\=" | "\\%" | "\\," | "\\." | "\\_" | {ControlEscape} | {OctalEscape}
QuotedAtomLiteral = '(\\' | {EscapeSequence}  | [^'\\])+'

CharLiteral = \$.

NotWhitespaceFormToken = {StringLiteral} | {QuotedAtomLiteral} | {CharLiteral} | .
FormToken = {Comment} | {Whitespace} | {NotWhitespaceFormToken}

%state FORM

%%

<YYINITIAL>         {Comment}                       { return ERLANG_FORM; }
<YYINITIAL>         {Whitespace}                    { return ERLANG_FORM; }
<YYINITIAL>         {NotWhitespaceFormToken}        { yybegin(FORM); }
<FORM>              {FormToken}                     { }
<FORM>              "."/ ({WhitespaceChar} | "%")   { yybegin(YYINITIAL); return ERLANG_FORM; }
<FORM>              <<EOF>>                         { yybegin(YYINITIAL); return ERLANG_FORM; }