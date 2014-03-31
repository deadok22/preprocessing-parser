package ru.spbau.preprocessing.api;

import ru.spbau.preprocessing.api.conditions.PresenceConditionFactory;
import ru.spbau.preprocessing.api.preprocessor.PreprocessorLanguageParser;

public interface LanguageProvider<TokenTypeBase> {
  LanguageLexer<TokenTypeBase> createLanguageLexer();
  PreprocessorLanguageParser createPreprocessorLanguageParser();
  PresenceConditionFactory createPresenceConditionFactory();
}
