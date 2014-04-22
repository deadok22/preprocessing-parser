package ru.spbau.preprocessing.erlang;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import ru.spbau.preprocessing.erlang.preprocessor.ErlangPreprocessorLanguageParserTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ErlangFormsLexerTests.class,
        ErlangPreprocessorLanguageParserTests.class,
        ErlangPreprocessingLexerTests.class,
        ErlangEarleyParserTests.class
})
public class ErlangTest {
}
