package ru.spbau.preprocessing.erlang;

import org.junit.Test;
import ru.spbau.preprocessing.lexer.PreprocessingLexer;
import ru.spbau.preprocessing.lexer.lexemegraph.LexemeGraphNode;

import java.io.IOException;
import java.io.StringWriter;

public class ErlangPreprocessingLexerTests extends ErlangAbstractFileResultTests {
  //tests without macro substitution
  @Test public void freeMacro()                  throws Exception { doTest(); }
  @Test public void definedMacro()               throws Exception { doTest(); }
  @Test public void undefinedMacro()             throws Exception { doTest(); }
  @Test public void definitionDefinesMacro()     throws Exception { doTest(); }
  @Test public void undefinitionUndefinesMacro() throws Exception { doTest(); }
  @Test public void macroIsAllowedToBeFree()     throws Exception { doTest(); }

  //tests with macro substitution
  @Test public void notReachingDefinitionDoesNotDefineMacro() throws Exception { doTest(); }
  @Test public void undefinedMacrosAreNotExpanded() throws Exception { doTest(); }
  @Test public void expandMultiplyDefinedMacro()    throws Exception { doTest(); }
  @Test public void expandDefinedMacro1()           throws Exception { doTest(); }
  @Test public void expandDefinedMacro2()           throws Exception { doTest(); }
  @Test public void expandMultipleCalls()           throws Exception { doTest(); }
  @Test public void expandCallAfterMultipleCalls()  throws Exception { doTest(); }

  @Override
  protected String getTestDataPath() {
    return "erlang/testData/preprocessingLexer/";
  }

  private void doTest() throws Exception {
    String input = readFile(getInputFileName());
    LexemeGraphNode actualLexemeGraph = buildLexemes(input);
    String actualLexemes = getLexemeGraphRepr(actualLexemeGraph);
    checkResult(actualLexemes);
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
}
