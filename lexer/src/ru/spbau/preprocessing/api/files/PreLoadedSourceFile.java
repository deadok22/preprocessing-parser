package ru.spbau.preprocessing.api.files;

import java.io.IOException;

public abstract class PreLoadedSourceFile implements SourceFile {
  private final String myPath;
  private final String myText;

  protected PreLoadedSourceFile(String path, String text) {
    myPath = path;
    myText = text;
  }

  @Override
  public String getPath() {
    return myPath;
  }

  @Override
  public String loadText() throws IOException {
    return myText;
  }
}
