package ru.spbau.preprocessing.erlang;

import com.google.common.io.Resources;
import org.junit.Rule;
import org.junit.rules.TestName;
import ru.spbau.preprocessing.erlang.files.ErlangFileSystemSourceFile;
import ru.spbau.preprocessing.lexer.PreprocessingLexer;
import ru.spbau.preprocessing.lexer.lexemegraph.LexemeGraphNode;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;

public abstract class ErlangAbstractFileResultTests {
  protected static final String CHARSET = "UTF8";
  @Rule
  public TestName myTestName = new TestName();

  protected void checkResult(String actual) throws IOException {
    String expected = readFile(getExpectedResultFileName());
    assertEquals(expected, actual);
  }

  protected LexemeGraphNode buildLexemes() throws IOException {
    ErlangFileSystemSourceFile sourceFile = new ErlangFileSystemSourceFile(getTestDirectoryFile(getInputFileName()));
    PreprocessingLexer<ErlangToken> lexer = new PreprocessingLexer<ErlangToken>(new ErlangLanguageProvider(), sourceFile);
    return lexer.buildLexemeGraph();
  }

  protected String getInputFileName() {
    return myTestName.getMethodName() + ".erl";
  }

  protected String getExpectedResultFileName() {
    return myTestName.getMethodName() + "-expected.txt";
  }

  protected String readFile(String fileName) throws IOException {
    return Resources.toString(getTestDirectoryFile(fileName).toURI().toURL(), Charset.forName(CHARSET));
  }

  protected File getTestDirectoryFile(String fileName) {
    return new File(getTestDataPath() + fileName);
  }

  protected abstract String getTestDataPath();
}
