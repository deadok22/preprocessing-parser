package ru.spbau.preprocessing.erlang.preprocessor;

/*
 * This lexer returns a lexeme per erlang form, whitespace or comment outside erlang form.
 */

%%

%{
  static final Integer ERLANG_FORM = 0;

  public ErlangFormsLexer(CharSequence text) {
    this(new java.io.StringReader(text.toString()));
  }

  private int myFormStart = 0;
  private int myFormLength = 0;

  public final int getFormStart(){
    return myFormStart;
  }

  public final int getFormEnd(){
    return myFormStart + myFormLength;
  }

  private Integer form() {
    formStarted();
    return formEnded(false);
  }

  private void formStarted() {
    myFormStart = myFormStart + myFormLength;
    myFormLength = yylength();
    yybegin(FORM);
  }

  private Integer formEnded() {
    return formEnded(true);
  }

  private Integer formEnded(boolean addCurrentToken) {
    if (addCurrentToken) myFormLength += yylength();
    yybegin(YYINITIAL);
    return ERLANG_FORM;
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

<YYINITIAL>         {Comment}                       { return form(); }
<YYINITIAL>         {Whitespace}                    { return form(); }
<YYINITIAL>         {NotWhitespaceFormToken}        { formStarted(); }
<FORM>              {FormToken}                     { myFormLength += yylength(); }
<FORM>              "."/ ({WhitespaceChar} | "%")   { return formEnded(); }
<FORM>              <<EOF>>                         { return formEnded(); }