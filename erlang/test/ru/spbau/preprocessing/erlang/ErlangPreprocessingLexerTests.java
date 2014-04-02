package ru.spbau.preprocessing.erlang;

import com.google.common.io.Resources;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import ru.spbau.preprocessing.lexer.PreprocessingLexer;
import ru.spbau.preprocessing.lexer.lexemegraph.LexemeGraphNode;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;

public class ErlangPreprocessingLexerTests {
  @Rule
  public TestName myTestName = new TestName();

  //tests without macro substitution
  @Test public void freeMacro()                  throws Exception { doTest(); }
  @Test public void definedMacro()               throws Exception { doTest(); }
  @Test public void undefinedMacro()             throws Exception { doTest(); }
  @Test public void definitionDefinesMacro()     throws Exception { doTest(); }
  @Test public void undefinitionUndefinesMacro() throws Exception { doTest(); }

  private static final String TEST_DATA_PATH = "erlang/testData/preprocessingLexer/";

  private void doTest() throws Exception {
    String input = readFile(getInputFileName());
    LexemeGraphNode actualLexemeGraph = buildLexemes(input);
    String actualLexemes = getLexemeGraphRepr(actualLexemeGraph);
    String expectedLexemes = readFile(getExpectedResultFileName());
    assertEquals(expectedLexemes, actualLexemes);
  }

  private LexemeGraphNode buildLexemes(String text) throws IOException {
    PreprocessingLexer<ErlangToken> lexer = new PreprocessingLexer<ErlangToken>(new ErlangLanguageProvider(), text);
    return lexer.buildLexemeGraph();
  }

  private String getLexemeGraphRepr(LexemeGraphNode graph) {
    StringWriter stringWriter = new StringWriter();
    ErlangLexemeGraphPrinterVisitor printer = new ErlangLexemeGraphPrinterVisitor(stringWriter, false);
    graph.accept(printer);
    return stringWriter.toString();
  }

  private String getInputFileName() {
    return myTestName.getMethodName() + ".erl";
  }

  private String getExpectedResultFileName() {
    return myTestName.getMethodName() + "-expected.txt";
  }

  private static String readFile(String fileName) throws IOException {
    return Resources.toString(new File(TEST_DATA_PATH + fileName).toURI().toURL(), Charset.forName("UTF-8"));
  }
}
