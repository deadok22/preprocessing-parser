package ru.spbau.preprocessing.erlang;

import ru.spbau.preprocessing.parser.earley.grammar.EarleyGrammar;
import ru.spbau.preprocessing.parser.earley.grammar.EarleyGrammarBuilder;

import static ru.spbau.preprocessing.erlang.ErlangToken.*;
import static ru.spbau.preprocessing.erlang.ErlangToken.COMMENT;
import static ru.spbau.preprocessing.erlang.ErlangToken.WHITESPACE;

public class ErlangEarleyGrammar {

  public static EarleyGrammar createGrammar() {
    // erlang grammar: https://github.com/erlang/otp/blob/master/lib/stdlib/src/erl_parse.yrl

    //nonterminals
    String forms = "Forms";
    String form = "Form";
    String function = "Function";
    String function_clauses = "Function clauses";
    String function_clause = "Function clause";
    String clause_args = "Clause args";
    String clause_body = "Clause body";
    String argument_list = "Argument list";
    String exprs = "Expressions";
    String expr = "Expression";
    String expr_100 = expr + "_100";
    String expr_150 = expr + "_150";
    String expr_160 = expr + "_160";
    String expr_200 = expr + "_200";
    String expr_300 = expr + "_300";
    String expr_400 = expr + "_400";
    String expr_500 = expr + "_500";
    String expr_600 = expr + "_600";
    String expr_700 = expr + "_700";
    String expr_800 = expr + "_800";
    String comp_op = "Comparison operator";
    String list_op = "List operation";
    String add_op = "Additive operator";
    String mult_op = "Multiplicative operator";
    String prefix_op = "Prefix operator";
    String function_call = "Function call";
//      String record_expr = "Record expression";
    String expr_max = "Max expression";
    String atomic = "Atomic";
    String strings = "Strings";
//      String record_tuple = "Record tuple";
//      String record_fields = "Record fields";
//      String record_field = "Record field";

    return EarleyGrammarBuilder.grammar(forms)

      .rule(forms).nonTerminal(forms).nonTerminal(form).completeRule()
      .rule(forms).nonTerminal(form).completeRule()

        //TODO attribute
      .rule(form).nonTerminal(function).terminal(DOT).completeRule()
        //TODO rule

      .rule(function).nonTerminal(function_clauses).completeRule()

      .rule(function_clauses).nonTerminal(function_clause).completeRule()
      .rule(function_clauses).nonTerminal(function_clause).terminal(SEMI).nonTerminal(function_clauses).completeRule()

        //TODO add clause guard (requires empty rules support)
      .rule(function_clause).terminal(ATOM).nonTerminal(clause_args).nonTerminal(clause_body).completeRule()

      .rule(clause_args).nonTerminal(argument_list).completeRule()

      .rule(clause_body).terminal(ARROW).nonTerminal(exprs).completeRule()

      .rule(expr).terminal(CATCH).nonTerminal(expr).completeRule()
      .rule(expr).nonTerminal(expr_100).completeRule()

      .rule(expr_100).nonTerminal(expr_150).terminal(OP_EQ).nonTerminal(expr_100).completeRule()
      .rule(expr_100).nonTerminal(expr_150).terminal(OP_EXL).nonTerminal(expr_100).completeRule()
      .rule(expr_100).nonTerminal(expr_150).completeRule()

      .rule(expr_150).nonTerminal(expr_160).terminal(ORELSE).nonTerminal(expr_150).completeRule()
      .rule(expr_150).nonTerminal(expr_160).completeRule()

      .rule(expr_160).nonTerminal(expr_200).terminal(ANDALSO).nonTerminal(expr_160).completeRule()
      .rule(expr_160).nonTerminal(expr_200).completeRule()

      .rule(expr_200).nonTerminal(expr_300).nonTerminal(comp_op).nonTerminal(expr_300).completeRule()
      .rule(expr_200).nonTerminal(expr_300).completeRule()

      .rule(expr_300).nonTerminal(expr_400).nonTerminal(list_op).nonTerminal(expr_300).completeRule()
      .rule(expr_300).nonTerminal(expr_400).completeRule()

      .rule(expr_400).nonTerminal(expr_400).nonTerminal(add_op).nonTerminal(expr_500).completeRule()
      .rule(expr_400).nonTerminal(expr_500).completeRule()

      .rule(expr_500).nonTerminal(expr_500).nonTerminal(mult_op).nonTerminal(expr_600).completeRule()
      .rule(expr_500).nonTerminal(expr_600).completeRule()

      .rule(expr_600).nonTerminal(prefix_op).nonTerminal(expr_700).completeRule()
        //TODO map_expr
      .rule(expr_600).nonTerminal(expr_700).completeRule()

      .rule(expr_700).nonTerminal(function_call).completeRule()
//              .rule(expr_700).nonTerminal(record_expr).completeRule()
      .rule(expr_700).nonTerminal(expr_800).completeRule()

      .rule(expr_800).nonTerminal(expr_max).terminal(COLON).nonTerminal(expr_max).completeRule()
      .rule(expr_800).nonTerminal(expr_max).completeRule()

      .rule(expr_max).terminal(VAR).completeRule()
      .rule(expr_max).nonTerminal(atomic).completeRule()
//              .rule(expr_max).nonTerminal(list).completeRule()
//              .rule(expr_max).nonTerminal(binary).completeRule()
//              .rule(expr_max).nonTerminal(list_comprehension).completeRule()
//              .rule(expr_max).nonTerminal(binary_comprehension).completeRule()
//              .rule(expr_max).nonTerminal(tuple).completeRule()
      .rule(expr_max).terminal(PAR_LEFT).nonTerminal(expr).terminal(PAR_RIGHT).completeRule()
      .rule(expr_max).terminal(BEGIN).nonTerminal(exprs).terminal(END).completeRule()
//              .rule(expr_max).nonTerminal(if_expr).completeRule()
//              .rule(expr_max).nonTerminal(case_expr).completeRule()
//              .rule(expr_max).nonTerminal(receive_expr).completeRule()
//              .rule(expr_max).nonTerminal(fun_expr).completeRule()
//              .rule(expr_max).nonTerminal(try_expr).completeRule()

//              .rule(record_expr).terminal(RADIX).terminal(ATOM).terminal(DOT).terminal(ATOM).completeRule()
//              .rule(record_expr).terminal(RADIX).terminal(ATOM).nonTerminal(record_tuple).completeRule()
//              .rule(record_expr).nonTerminal(expr_max).terminal(RADIX).terminal(ATOM).terminal(DOT).terminal(ATOM).completeRule()
//              .rule(record_expr).nonTerminal(expr_max).terminal(RADIX).terminal(ATOM).nonTerminal(record_tuple).completeRule()
//              .rule(record_expr).nonTerminal(record_expr).terminal(RADIX).terminal(ATOM).terminal(DOT).terminal(ATOM).completeRule()
//              .rule(record_expr).nonTerminal(record_expr).terminal(RADIX).terminal(ATOM).nonTerminal(record_tuple).completeRule()
//
//              .rule(record_tuple).terminal(CURLY_LEFT).terminal(CURLY_RIGHT).completeRule()
//              .rule(record_tuple).terminal(CURLY_LEFT).nonTerminal(record_fields).terminal(CURLY_RIGHT).completeRule()
//
//              .rule(record_fields).nonTerminal(record_field).completeRule()
//              .rule(record_fields).nonTerminal(record_field).terminal(COMMA).nonTerminal(record_fields).completeRule()
//
//              .rule(record_field).terminal(VAR).terminal(OP_EQ).nonTerminal(expr).completeRule()
//              .rule(record_field).terminal(ATOM).terminal(OP_EQ).nonTerminal(expr).completeRule()

      .rule(function_call).nonTerminal(expr_800).nonTerminal(argument_list).completeRule()

      .rule(argument_list).terminal(PAR_LEFT).terminal(PAR_RIGHT).completeRule()
      .rule(argument_list).terminal(PAR_LEFT).nonTerminal(exprs).terminal(PAR_RIGHT).completeRule()

      .rule(exprs).nonTerminal(expr).completeRule()
      .rule(exprs).nonTerminal(expr).terminal(COMMA).nonTerminal(exprs).completeRule()

      .rule(atomic).terminal(CHAR).completeRule()
      .rule(atomic).terminal(INTEGER).completeRule()
      .rule(atomic).terminal(FLOAT).completeRule()
      .rule(atomic).terminal(ATOM).completeRule()
      .rule(atomic).nonTerminal(strings).completeRule()

      .rule(strings).terminal(STRING).completeRule()
      .rule(strings).terminal(STRING).nonTerminal(strings).completeRule()

      .rule(prefix_op).terminal(OP_PLUS).completeRule()
      .rule(prefix_op).terminal(OP_MINUS).completeRule()
      .rule(prefix_op).terminal(BNOT).completeRule()
      .rule(prefix_op).terminal(NOT).completeRule()

      .rule(mult_op).terminal(OP_AR_DIV).completeRule()
      .rule(mult_op).terminal(OP_AR_MUL).completeRule()
      .rule(mult_op).terminal(DIV).completeRule()
      .rule(mult_op).terminal(REM).completeRule()
      .rule(mult_op).terminal(BAND).completeRule()
      .rule(mult_op).terminal(AND).completeRule()

      .rule(add_op).terminal(OP_PLUS).completeRule()
      .rule(add_op).terminal(OP_MINUS).completeRule()
      .rule(add_op).terminal(BOR).completeRule()
      .rule(add_op).terminal(BXOR).completeRule()
      .rule(add_op).terminal(BSL).completeRule()
      .rule(add_op).terminal(BSR).completeRule()
      .rule(add_op).terminal(OR).completeRule()
      .rule(add_op).terminal(XOR).completeRule()

      .rule(list_op).terminal(OP_PLUS_PLUS).completeRule()
      .rule(list_op).terminal(OP_MINUS_MINUS).completeRule()

      .rule(comp_op).terminal(OP_EQ_EQ).completeRule()
      .rule(comp_op).terminal(OP_DIV_EQ).completeRule()
      .rule(comp_op).terminal(OP_EQ_LT).completeRule()
      .rule(comp_op).terminal(OP_LT).completeRule()
      .rule(comp_op).terminal(OP_GT_EQ).completeRule()
      .rule(comp_op).terminal(OP_GT).completeRule()
      .rule(comp_op).terminal(OP_EQ_COL_EQ).completeRule()
      .rule(comp_op).terminal(OP_EQ_DIV_EQ).completeRule()

      .ignoreTerminal(COMMENT)
      .ignoreTerminal(WHITESPACE)

      .build();
  }
}
