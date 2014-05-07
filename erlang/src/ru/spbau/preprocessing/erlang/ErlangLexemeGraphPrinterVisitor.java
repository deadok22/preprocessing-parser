package ru.spbau.preprocessing.erlang;

import com.google.common.base.Objects;
import ru.spbau.preprocessing.api.conditions.PresenceCondition;
import ru.spbau.preprocessing.lexer.lexemegraph.*;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;

public class ErlangLexemeGraphPrinterVisitor implements LexemeGraphVisitor, Closeable {
  private final Writer myWriter;
  private final boolean myShouldClose;

  private boolean myShouldPrintLocation;
  private String myRootPath; // root path is not printed if print location is enabled.

  private int myDepth = 0;

  public ErlangLexemeGraphPrinterVisitor(Writer writer) {
    this(writer, true);
  }

  public ErlangLexemeGraphPrinterVisitor(Writer writer, boolean shouldClose) {
    myWriter = writer;
    myShouldClose = shouldClose;
  }

  public void setPrintLocation(boolean printLocation) {
    myShouldPrintLocation = printLocation;
  }

  public void setRootPath(String path) {
    myRootPath = path;
  }

  @Override
  public void visitForkNode(LexemeGraphForkNode forkNode) {
    printLine("FORK", forkNode.getPresenceCondition());
    myDepth++;
    for (LexemeGraphNode child : forkNode.getChildren()) {
      printLine("BRANCH", child.getPresenceCondition());
      myDepth++;
      child.accept(this);
      myDepth--;
    }
    myDepth--;
  }

  @Override
  public void visitLangNode(LexemeGraphLangNode langNode) {
    printLine("LANG", langNode.getPresenceCondition());
    myDepth++;
    for (Object lexeme : langNode.getLexemes()) {
      printLine(String.valueOf(lexeme) + (myShouldPrintLocation ? (" - " + getLexemeLocationString(lexeme)) : ""));
    }
    myDepth--;
  }

  @Override
  public void close() throws IOException {
    if (myShouldClose) {
      myWriter.close();
    }
  }

  private String getLexemeLocationString(Object lexeme) {
    LexemeLocation location = ((Lexeme) lexeme).getLocation();
    String path = location.getSourceFile().getPath();
    return location.getStartOffset() + (Objects.equal(path, myRootPath) ? "" : (" @" + path));
  }

  private void printLine(String lineText, PresenceCondition presenceCondition) {
    printLine(lineText + "   " + presenceCondition + "   " + presenceCondition.value());
  }

  private void printLine(String lineText) {
    try {
      for (int i = 0; i < myDepth; i++) {
        myWriter.append(' ');
      }
      myWriter.append(lineText);
      myWriter.append('\n');
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
