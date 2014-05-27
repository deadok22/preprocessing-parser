package ru.spbau.preprocessing.erlang;

import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import ru.spbau.preprocessing.lexer.lexemegraph.LexemeGraphNode;
import ru.spbau.preprocessing.parser.earley.ast.EarleyAstNode;
import ru.spbau.preprocessing.parser.earley.ast.EarleyAstPrinter;
import ru.spbau.preprocessing.parser.earley.grammar.EarleyGrammar;
import ru.spbau.preprocessing.parser.earley.parser.EarleyParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import static junit.framework.TestCase.assertNotNull;

public abstract class ErlangAbstractEarleyParserTests extends ErlangAbstractFileResultTests {
  @Rule
  public ErlangEarleyParserCreatorRule myParserCreator = new ErlangEarleyParserCreatorRule();

  protected void doTest() throws IOException {
    EarleyAstNode parseResult = parse();
    assertNotNull(parseResult);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintWriter printWriter = new PrintWriter(outputStream, true);
    parseResult.accept(new EarleyAstPrinter(printWriter));
    String result = outputStream.toString(CHARSET);
    printWriter.close();
    checkResult(result);
  }

  protected EarleyAstNode parse() throws IOException {
    LexemeGraphNode lexemes = buildLexemes();
    return myParserCreator.getParser().parse(lexemes);
  }

  protected abstract EarleyGrammar createGrammar();

  public class ErlangEarleyParserCreatorRule extends TestWatcher {
    private EarleyParser myParser;

    @Override
    protected void starting(Description description) {
      myParser = new EarleyParser(createGrammar(), new ErlangLanguageProvider().createPresenceConditionFactory());
    }

    public EarleyParser getParser() {
      return myParser;
    }
  }
}
