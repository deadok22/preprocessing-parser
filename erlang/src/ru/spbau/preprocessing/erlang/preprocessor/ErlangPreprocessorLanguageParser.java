package ru.spbau.preprocessing.erlang.preprocessor;

import ru.spbau.preprocessing.api.preprocessor.PreprocessorLanguageNode;
import ru.spbau.preprocessing.api.preprocessor.PreprocessorLanguageParser;
import ru.spbau.preprocessing.erlang.preprocessor.ast.ErlangPreprocessorNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ErlangPreprocessorLanguageParser implements PreprocessorLanguageParser {
  @Override
  public List<PreprocessorLanguageNode> parse(CharSequence text) throws IOException {
    ArrayList<PreprocessorLanguageNode> parseResult = new ArrayList<PreprocessorLanguageNode>();
    ErlangFormsLexer formsLexer = new ErlangFormsLexer(text);
    while (formsLexer.advance() != null) {
      //TODO try parse preprocessor directive
      //no preprocessor directive parsed - this is a code node
      parseResult.add(new ErlangPreprocessorNode(text, formsLexer.getFormStart(), formsLexer.getFormEnd()));
    }
    return parseResult;
  }
}
