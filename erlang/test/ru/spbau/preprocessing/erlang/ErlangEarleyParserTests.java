package ru.spbau.preprocessing.erlang;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import ru.spbau.preprocessing.lexer.lexemegraph.LexemeGraphNode;
import ru.spbau.preprocessing.parser.earley.ast.EarleyAstNode;
import ru.spbau.preprocessing.parser.earley.ast.EarleyAstPrinter;
import ru.spbau.preprocessing.parser.earley.parser.EarleyParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import static junit.framework.TestCase.assertNotNull;

public class ErlangEarleyParserTests extends ErlangAbstractFileResultTests {
  @Rule public ErlangEarleyParserCreatorRule myParserCreator = new ErlangEarleyParserCreatorRule();

  @Test public void functionWithTwoClauses()         throws Exception { doTest(); }
  @Test public void singleFunction()                 throws Exception { doTest(); }
  @Test public void twoFunctions()                   throws Exception { doTest(); }
  @Test public void functionWithArgs()               throws Exception { doTest(); }
  @Test public void conditionallyCompiledFunction()  throws Exception { doTest(); }
  @Test public void conditionallyCompiledFunction2() throws Exception { doTest(); }
  @Test public void conditionallyCompiledFunctions() throws Exception { doTest(); }
  @Test public void alternativeFunctionDefinitions() throws Exception { doTest(); }
  @Test public void alternativeSubstitution()        throws Exception { doTest(); }
  @Test public void alternativeExpressions()         throws Exception { doTest(); }
  @Test public void conditionalClause()              throws Exception { doTest(); }

  @Override
  protected String getTestDataPath() {
    return "erlang/testData/earleyParser/";
  }

  private void doTest() throws IOException {
    EarleyAstNode parseResult = parse();
    assertNotNull(parseResult);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintWriter printWriter = new PrintWriter(outputStream, true);
    parseResult.accept(new EarleyAstPrinter(printWriter));
    String result = outputStream.toString(CHARSET);
    printWriter.close();
    checkResult(result);
  }

  private EarleyAstNode parse() throws IOException {
    LexemeGraphNode lexemes = buildLexemes();
    return myParserCreator.getParser().parse(lexemes);
  }

  public static class ErlangEarleyParserCreatorRule extends TestWatcher {
    private EarleyParser myParser;

    @Override
    protected void starting(Description description) {
      myParser = new EarleyParser(ErlangEarleyGrammar.createGrammar(), new ErlangLanguageProvider().createPresenceConditionFactory());
    }

    public EarleyParser getParser() {
      return myParser;
    }
  }
}
