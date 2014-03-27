package ru.spbau.preprocessing.erlang.preprocessor.ast;

import ru.spbau.preprocessing.api.preprocessor.PreprocessorLanguageMacroUndefinitionNode;
import ru.spbau.preprocessing.api.preprocessor.PreprocessorLanguageNodeVisitor;

public class ErlangMacroUndefinitionNode extends ErlangPreprocessorNode
        implements PreprocessorLanguageMacroUndefinitionNode {
  private final String myMacroName;

  public ErlangMacroUndefinitionNode(CharSequence buffer, int startOffset, int endOffset, String macroName) {
    super(buffer, startOffset, endOffset);
    myMacroName = macroName;
  }

  @Override
  public String getName() {
    return myMacroName;
  }

  @Override
  public void accept(PreprocessorLanguageNodeVisitor visitor) {
    visitor.visitMacroUndefinition(this);
  }
}
