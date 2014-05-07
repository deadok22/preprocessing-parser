package ru.spbau.preprocessing.erlang.files;

import ru.spbau.preprocessing.api.files.FileSystemSourceFile;
import ru.spbau.preprocessing.api.files.SourceFile;
import ru.spbau.preprocessing.api.preprocessor.PreprocessorLanguageFileInclusionNode;
import ru.spbau.preprocessing.erlang.preprocessor.ast.ErlangInclusionAttributeNode;

import java.io.File;
import java.io.IOException;

public class ErlangFileSystemSourceFile extends FileSystemSourceFile {
  // a file which included this file
  private SourceFile myIncludeDirectiveOwner;

  public ErlangFileSystemSourceFile(File file) {
    this(null, file);
  }

  private ErlangFileSystemSourceFile(SourceFile includeDirectiveOwner, File file) {
    super(file);
    myIncludeDirectiveOwner = includeDirectiveOwner;
  }

  @Override
  public SourceFile resolveInclusion(PreprocessorLanguageFileInclusionNode inclusionNode) throws IOException {
    assert inclusionNode instanceof ErlangInclusionAttributeNode;
    ErlangInclusionAttributeNode includeAttribute = (ErlangInclusionAttributeNode) inclusionNode;
    SourceFile resolutionResult = resolveRelativeToThisFile(includeAttribute);
    return resolutionResult != null ? resolutionResult :
            myIncludeDirectiveOwner != null ? myIncludeDirectiveOwner.resolveInclusion(inclusionNode) : null;
  }

  private SourceFile resolveRelativeToThisFile(ErlangInclusionAttributeNode includeAttribute) throws IOException {
    switch (includeAttribute.getResolutionStrategy()) {
      case INCLUDE: {
        File resolutionResult = new File(getFile().getParentFile(), includeAttribute.getIncludePath());
        if (resolutionResult.isFile() && resolutionResult.canRead()) {
          return new ErlangFileSystemSourceFile(this, resolutionResult);
        }
        break;
      }
      default:
        //TODO include_lib
        throw new UnsupportedOperationException("Unsupported include resolution strategy");
    }
    return null;
  }
}
