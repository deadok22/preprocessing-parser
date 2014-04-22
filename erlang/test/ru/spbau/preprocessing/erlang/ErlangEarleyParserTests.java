package ru.spbau.preprocessing.erlang;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import ru.spbau.preprocessing.lexer.PreprocessingLexer;
import ru.spbau.preprocessing.lexer.lexemegraph.LexemeGraphNode;
import ru.spbau.preprocessing.parser.earley.EarleyParser;
import ru.spbau.preprocessing.parser.earley.ast.EarleyAstNode;
import ru.spbau.preprocessing.parser.earley.ast.EarleyAstPrinter;
import ru.spbau.preprocessing.parser.earley.grammar.EarleyGrammar;
import ru.spbau.preprocessing.parser.earley.grammar.EarleyGrammarBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import static ru.spbau.preprocessing.erlang.ErlangToken.COMMENT;
import static ru.spbau.preprocessing.erlang.ErlangToken.WHITESPACE;

public class ErlangEarleyParserTests extends ErlangAbstractFileResultTests {
  @Rule public ErlangEarleyParserCreatorRule myParserCreator = new ErlangEarleyParserCreatorRule();

  @Test public void endlinesAndComment() throws IOException { doTest(); }
  @Test public void comment()            throws IOException { doTest(); }


  @Override
  protected String getTestDataPath() {
    return "erlang/testData/earleyParser/";
  }

  private void doTest() throws IOException {
    String input = readFile(getInputFileName());
    EarleyAstNode parseResult = parse(input);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintWriter printWriter = new PrintWriter(outputStream, true);
    parseResult.accept(new EarleyAstPrinter(printWriter));
    String result = outputStream.toString(CHARSET);
    printWriter.close();
    checkResult(result);
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
              .rule(comment).nonTerminal(comment).nonTerminal(comment).completeRule()

              .rule(whitespace).terminal(WHITESPACE).completeRule()
              .rule(whitespace).nonTerminal(whitespace).nonTerminal(whitespace).completeRule()

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
