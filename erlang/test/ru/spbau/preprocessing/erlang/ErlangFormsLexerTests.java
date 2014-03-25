package ru.spbau.preprocessing.erlang;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.spbau.preprocessing.erlang.preprocessor.ErlangFormsLexer;

import java.util.Arrays;
import java.util.Collection;

import static junit.framework.Assert.assertEquals;
import static org.junit.runners.Parameterized.Parameter;
import static org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ErlangFormsLexerTests {
  @Parameters
  public static Collection<Object[]> testData() {
    return Arrays.asList(new Object[][] {
                    {"b() -> ok.", 1},
                    {"-ifdef(MACRO).\nb() -> ok.\n-endif.", 5},
                    {"b%foo\n() -> ok.", 1}
            }
    );
  }

  @Parameter
  public String myInput;

  @Parameter(value = 1)
  public Integer myExpectedFormsCount;

  @Test
  public void test() throws Exception {
    ErlangFormsLexer lexer = new ErlangFormsLexer(myInput);
    int formsCount = 0;
    while (lexer.advance() != null) formsCount++;
    assertEquals(myExpectedFormsCount.intValue(), formsCount);
  }
}
