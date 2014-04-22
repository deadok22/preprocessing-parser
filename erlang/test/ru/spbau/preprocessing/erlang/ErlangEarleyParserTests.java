package ru.spbau.preprocessing.erlang;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import ru.spbau.preprocessing.lexer.PreprocessingLexer;
import ru.spbau.preprocessing.lexer.lexemegraph.LexemeGraphNode;
import ru.spbau.preprocessing.parser.earley.EarleyParser;
import ru.spbau.preprocessing.parser.earley.ast.EarleyAstNode;
import ru.spbau.preprocessing.parser.earley.grammar.EarleyGrammar;
import ru.spbau.preprocessing.parser.earley.grammar.EarleyGrammarBuilder;

import java.io.IOException;

import static ru.spbau.preprocessing.erlang.ErlangToken.COMMENT;
import static ru.spbau.preprocessing.erlang.ErlangToken.WHITESPACE;

public class ErlangEarleyParserTests {
  @Rule public ErlangEarleyParserCreatorRule myParserCreator = new ErlangEarleyParserCreatorRule();

  @Test public void testWhitespace1() throws IOException {
    String ws = "    ";
    EarleyAstNode parse = parse(ws);
    Assert.assertNotNull(parse);
  }

  private EarleyAstNode parse(String text) throws IOException {
    LexemeGraphNode lexemes = buildLexemes(text);
    return myParserCreator.getParser().parse(lexemes);
  }

  private LexemeGraphNode buildLexemes(String text) throws IOException {
    PreprocessingLexer<ErlangToken> lexer = new PreprocessingLexer<ErlangToken>(new ErlangLanguageProvider(), text);
    return lexer.buildLexemeGraph();
  }

  public static class ErlangEarleyParserCreatorRule extends TestWatcher {
    private EarleyParser<ErlangToken> myParser;

    @Override
    protected void starting(Description description) {
      //TODO provide a full grammar
      String file = "File";
      String fileContent = "FileContent";
      String comment = "Comment";
      String whitespace = "Whitespace";

      EarleyGrammar grammar = EarleyGrammarBuilder.grammar(file)

              .rule(comment).terminal(COMMENT).completeRule()
              .rule(comment).terminal(COMMENT).nonTerminal(comment).completeRule()

              .rule(whitespace).terminal(WHITESPACE).completeRule()
              .rule(whitespace).terminal(WHITESPACE).nonTerminal(whitespace).completeRule()

              .rule(fileContent).nonTerminal(whitespace).completeRule()
              .rule(fileContent).nonTerminal(comment).completeRule()
              .rule(fileContent).nonTerminal(fileContent).nonTerminal(fileContent).completeRule()

              .rule(file).nonTerminal(fileContent).completeRule()

              .build();

      myParser = new EarleyParser<ErlangToken>(new ErlangLanguageProvider(), grammar);
    }

    public EarleyParser<ErlangToken> getParser() {
      return myParser;
    }
  }
}
