package ru.spbau.preprocessing.erlang;

import org.junit.Test;
import ru.spbau.preprocessing.lexer.lexemegraph.LexemeGraphNode;

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

  //tests with file inclusion
  @Test public void fooInclusion()                    throws Exception { doTest(); }
  @Test public void conditionalFooInclusion()         throws Exception { doTest(); }
  @Test public void macrosFromIncludesAreDefined()    throws Exception { doTest(); }
  @Test public void macrosFromIncludesAreExpanded()   throws Exception { doTest(); }
  @Test public void macrosInsideIncludesAreExpanded() throws Exception { doTest(); }

  @Override
  protected String getTestDataPath() {
    return "erlang/testData/preprocessingLexer/";
  }

  private void doTest() throws Exception {
    LexemeGraphNode actualLexemeGraph = buildLexemes();
    String actualLexemes = getLexemeGraphRepr(actualLexemeGraph);
    checkResult(actualLexemes);
  }

  private String getLexemeGraphRepr(LexemeGraphNode graph) {
    StringWriter stringWriter = new StringWriter();
    ErlangLexemeGraphPrinterVisitor printer = new ErlangLexemeGraphPrinterVisitor(stringWriter, false);
    graph.accept(printer);
    return stringWriter.toString();
  }
}
