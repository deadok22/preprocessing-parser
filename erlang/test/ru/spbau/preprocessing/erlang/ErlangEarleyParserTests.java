package ru.spbau.preprocessing.erlang;

import org.junit.Test;
import ru.spbau.preprocessing.parser.earley.grammar.EarleyGrammar;

public class ErlangEarleyParserTests extends ErlangAbstractEarleyParserTests {
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
  @Test public void optionalFirstClause()            throws Exception { doTest(); }

  @Override
  protected String getTestDataPath() {
    return "erlang/testData/earleyParser/";
  }

  @Override
  protected EarleyGrammar createGrammar() {
    return ErlangEarleyGrammar.createGrammar();
  }
}
