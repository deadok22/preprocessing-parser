package ru.spbau.preprocessing.api.preprocessor;

import java.util.List;

/**
 * A parser which breaks a source file into preprocessor directives, code segments and conditionals.
 * It doesn't explicitly use LanguageLexer as preprocessor language may be described in a more sound way.
 */
public interface PreprocessorLanguageParser {
  List<PreprocessorLanguageNode> parse(CharSequence text);
}
