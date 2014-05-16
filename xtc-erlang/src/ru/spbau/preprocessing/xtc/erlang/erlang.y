%token AFTER
%token AND
%token ANDALSO
%token ARROW
%token ASSOC
%token ATOM
%token BAND
%token BEGIN
%token BIN_END
%token BIN_START
%token BNOT
%token BOR
%token BRACKET_LEFT
%token BRACKET_RIGHT
%token BSL
%token BSR
%token BXOR
%token CASE
%token CATCH
%token CHAR
%token COLON
%token COLON_COLON
%token COMMA
%token CURLY_LEFT
%token CURLY_RIGHT
%token DIV
%token DOT
%token DOT_DOT
%token DOT_DOT_DOT
%token END
%token FLOAT
%token FUN
%token IF
%token INTEGER
%token MATCH
%token NOT
%token OF
%token OP_AR_DIV
%token OP_AR_MUL
%token OP_DIV_EQ
%token OP_EQ
%token OP_EQ_COL_EQ
%token OP_EQ_DIV_EQ
%token OP_EQ_EQ
%token OP_EQ_LT
%token OP_EXL
%token OP_GT
%token OP_GT_EQ
%token OP_LT
%token OP_LT_EQ
%token OP_LT_MINUS
%token OP_MINUS
%token OP_MINUS_MINUS
%token OP_OR
%token OP_PLUS
%token OP_PLUS_PLUS
%token OR
%token ORELSE
%token OR_OR
%token PAR_LEFT
%token PAR_RIGHT
%token QMARK
%token RADIX
%token RECEIVE
%token REM
%token SEMI
%token STRING
%token TRY
%token VAR
%token WHEN
%token XOR

%%

Forms:
    Forms Form
    | Form;

Form:
    Function DOT;

Function:
    FunctionClauses;

FunctionClauses:
    FunctionClause SEMI FunctionClauses
    | FunctionClause;

FunctionClause:
    ATOM ClauseArgs ClauseBody;

ClauseArgs:
    ArgumentList;

ClauseBody:
    ARROW Exprs;

Expr:
    CATCH Expr
    | ATOM; /*TODO: replace with real expressions*/

ArgumentList:
    PAR_LEFT PAR_RIGHT; /*TODO: replace with real argument list*/

Exprs:
    Expr COMMA Expr
    | Expr;