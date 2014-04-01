package ru.spbau.preprocessing.api.conditions;

import ru.spbau.preprocessing.api.preprocessor.PreprocessorLanguageConditionalNode;

public interface PresenceConditionFactory {
  PresenceCondition getTrue();

  PresenceCondition getFalse();

  /**
   * Returned presence condition's value should be computed exactly once and before this method returns.
   */
  PresenceCondition create(PreprocessorLanguageConditionalNode.PreprocessorLanguageCondition langCondition, ConditionalContext containingContext);
}
