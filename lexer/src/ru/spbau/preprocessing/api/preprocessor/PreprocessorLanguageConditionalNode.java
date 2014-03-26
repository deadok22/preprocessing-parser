package ru.spbau.preprocessing.api.preprocessor;

import java.util.List;

/**
 * Represents a condition and conditionally compiled code associated with it.
 */
public interface PreprocessorLanguageConditionalNode extends PreprocessorLanguageNode {
  /**
   * Guarding condition.
   *
   * @return a condition object or null if it is an alternative branch like #else branch in
   *         cpp's #if ... #elif ... #else ... #end construct
   */
  PreprocessorLanguageCondition getConditionExpression();
  List<? extends PreprocessorLanguageNode> getCode();

  /**
   * A preprocessor language condition for conditionally compiled block of code.
   */
  public interface PreprocessorLanguageCondition {}

  /**
   * Macro defined condition for conditionally compiled block of code.
   */
  public interface PreprocessorLanguageMacroDefinedCondition extends PreprocessorLanguageCondition {
    String getMacroName();

    /**
     * A macro defined condition is positive if it corresponds to
     * cpp's #ifdef or Erlang's -ifdef construct.
     *
     * @return true if it is a positive macro defined condition.
     */
    boolean isPositiveCondition();
  }
}
