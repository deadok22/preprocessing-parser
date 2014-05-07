package ru.spbau.preprocessing.api.files;

import ru.spbau.preprocessing.api.preprocessor.PreprocessorLanguageFileInclusionNode;

import java.io.IOException;

public interface SourceFile {
  String getPath();
  String loadText() throws IOException;
  SourceFile resolveInclusion(PreprocessorLanguageFileInclusionNode inclusionNode) throws IOException;
}
