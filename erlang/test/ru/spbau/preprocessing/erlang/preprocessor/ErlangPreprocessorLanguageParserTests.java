package ru.spbau.preprocessing.erlang.preprocessor;

import org.junit.Test;
import ru.spbau.preprocessing.api.preprocessor.PreprocessorLanguageNode;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ErlangPreprocessorLanguageParserTests {
  @Test
  public void testFunction() throws Exception {
    String fun = "foo() -> ok.";
    List<? extends PreprocessorLanguageNode> result = parse(fun);
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(fun, result.get(0).getText());
  }

  @Test
  public void testTwoFunctions() throws Exception {
    String fun1 = "foo() -> ok.";
    String endl = "\n";
    String fun2 = "bar() -> ok.";
    List<? extends PreprocessorLanguageNode> result = parse(fun1 + endl + fun2);
    assertNotNull(result);
    assertEquals(3, result.size());
    assertEquals(fun1, result.get(0).getText());
    assertEquals(endl, result.get(1).getText());
    assertEquals(fun2, result.get(2).getText());
  }

  private List<? extends PreprocessorLanguageNode> parse(String text) throws IOException {
    return new ErlangPreprocessorLanguageParser().parse(text);
  }
}
