package ru.spbau.preprocessing.api.preprocessor;

public abstract class PreprocessorLanguageNodeVisitor {
  public abstract void visit(PreprocessorLanguageNode node);
  public abstract void visitMacroDefinition(PreprocessorLanguageMacroDefinitionNode node);
  public abstract void visitMacroUndefinition(PreprocessorLanguageMacroDefinitionNode node);
  public abstract void visitAlternatives(PreprocessorLanguageAlternativesNode node);
  public abstract void visitFileInclusion(PreprocessorLanguageFileInclusionNode node);
  public abstract void visitConditionalNode(PreprocessorLanguageConditionalNode node);
}
