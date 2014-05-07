package ru.spbau.preprocessing.api.files;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public abstract class FileSystemSourceFile implements SourceFile {
  private final File myFile;

  protected FileSystemSourceFile(File file) {
    myFile = file;
  }

  public File getFile() {
    return myFile;
  }

  @Override
  public String getPath() {
    return myFile.getPath();
  }

  @Override
  public String loadText() throws IOException {
    return Files.toString(myFile, Charset.forName("UTF8"));
  }
}
