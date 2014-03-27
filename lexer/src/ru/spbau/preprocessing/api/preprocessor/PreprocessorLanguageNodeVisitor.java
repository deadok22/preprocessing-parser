package ru.spbau.preprocessing.api.preprocessor;

import java.util.List;

public abstract class PreprocessorLanguageNodeVisitor {
  public abstract void visit(PreprocessorLanguageNode node);
  public abstract void visitNodes(List<? extends PreprocessorLanguageNode> nodes);
  public abstract void visitMacroDefinition(PreprocessorLanguageMacroDefinitionNode node);
  public abstract void visitMacroUndefinition(PreprocessorLanguageMacroUndefinitionNode node);
  public abstract void visitAlternatives(PreprocessorLanguageAlternativesNode node);
  public abstract void visitFileInclusion(PreprocessorLanguageFileInclusionNode node);
  public abstract void visitConditional(PreprocessorLanguageConditionalNode node);
}
