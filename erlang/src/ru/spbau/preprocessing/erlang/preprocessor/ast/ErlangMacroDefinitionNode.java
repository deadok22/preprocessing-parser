package ru.spbau.preprocessing.erlang.preprocessor.ast;

import ru.spbau.preprocessing.api.preprocessor.PreprocessorLanguageMacroDefinitionNode;
import ru.spbau.preprocessing.api.preprocessor.PreprocessorLanguageNodeVisitor;

import java.util.List;

public class ErlangMacroDefinitionNode extends ErlangPreprocessorNode
        implements PreprocessorLanguageMacroDefinitionNode {
  private final String myMacroName;
  private List<String> myMacroParameterNames;
  private final int myMacroBodyStartOffset;
  private final int myMacroBodyEndOffset;

  public ErlangMacroDefinitionNode(CharSequence buffer, int startOffset, int endOffset,
                                   String name, List<String> paramNames,
                                   int macroBodyStartOffset, int macroBodyEndOffset) {
    super(buffer, startOffset, endOffset);
    myMacroName = name;
    myMacroParameterNames = paramNames;
    myMacroBodyStartOffset = macroBodyStartOffset;
    myMacroBodyEndOffset = macroBodyEndOffset;
  }

  @Override
  public String getName() {
    return myMacroName;
  }

  @Override
  public int getArity() {
    return myMacroParameterNames == null ? -1 : myMacroParameterNames.size();
  }

  @Override
  public List<String> getParameterNames() {
    return myMacroParameterNames;
  }

  @Override
  public String getSubstitution(List<String> macroArguments) {
    //TODO
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public void accept(PreprocessorLanguageNodeVisitor visitor) {
    visitor.visitMacroDefinition(this);
  }
}
