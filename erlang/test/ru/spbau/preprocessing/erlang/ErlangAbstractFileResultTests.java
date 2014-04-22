package ru.spbau.preprocessing.erlang;

import com.google.common.io.Resources;
import org.junit.Rule;
import org.junit.rules.TestName;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;

public abstract class ErlangAbstractFileResultTests {
  @Rule
  public TestName myTestName = new TestName();

  protected void checkResult(String actual) throws IOException {
    String expected = readFile(getExpectedResultFileName());
    assertEquals(expected, actual);
  }

  protected String getInputFileName() {
    return myTestName.getMethodName() + ".erl";
  }

  protected String getExpectedResultFileName() {
    return myTestName.getMethodName() + "-expected.txt";
  }

  protected String readFile(String fileName) throws IOException {
    return Resources.toString(new File(getTestDataPath() + fileName).toURI().toURL(), Charset.forName("UTF-8"));
  }

  protected abstract String getTestDataPath();
}
