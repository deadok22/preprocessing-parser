package ru.spbau.preprocessing.erlang;

import org.junit.Test;
import ru.spbau.preprocessing.parser.earley.grammar.EarleyGrammar;
import ru.spbau.preprocessing.parser.earley.grammar.EarleyGrammarBuilder;

import java.io.IOException;

import static ru.spbau.preprocessing.erlang.ErlangToken.*;

public class SequenceParserTests extends ErlangAbstractEarleyParserTests {
  @Test public void simple() throws IOException { doTest(); }
  @Test public void firstOptional() throws IOException { doTest(); }

  @Override
  protected String getTestDataPath() {
    return "erlang/testData/sequenceParser/";
  }

  @Override
  protected EarleyGrammar createGrammar() {
    return EarleyGrammarBuilder.grammar("forms")

            .rule("forms").nonTerminal("form").completeRule()
            .rule("forms").nonTerminal("forms").nonTerminal("form").completeRule()

            .rule("form").terminal(ATOM).terminal(DOT).completeRule()

            .ignoreTerminal(WHITESPACE)
            .ignoreTerminal(COMMENT)

            .build()
            ;
  }
}
