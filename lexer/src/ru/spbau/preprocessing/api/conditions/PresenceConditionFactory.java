package ru.spbau.preprocessing.api.conditions;

import ru.spbau.preprocessing.api.preprocessor.PreprocessorLanguageConditionalNode;

public interface PresenceConditionFactory {
  PresenceCondition getTrue();
  PresenceCondition getFalse();
  PresenceCondition create(PreprocessorLanguageConditionalNode.PreprocessorLanguageCondition langCondition);
}
