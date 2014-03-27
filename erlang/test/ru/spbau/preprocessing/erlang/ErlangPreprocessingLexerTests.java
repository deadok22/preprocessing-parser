package ru.spbau.preprocessing.erlang;

import org.junit.Test;
import ru.spbau.preprocessing.lexer.PreprocessingLexer;
import ru.spbau.preprocessing.lexer.lexemegraph.LexemeGraphForkNode;
import ru.spbau.preprocessing.lexer.lexemegraph.LexemeGraphLangNode;
import ru.spbau.preprocessing.lexer.lexemegraph.LexemeGraphNode;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class ErlangPreprocessingLexerTests {
  @Test
  public void testSimpleConditional() throws Exception {
    String ifdef = "-ifdef(FOO).";
    String condTrue = "\nfoo() -> ok.\n";
    String elze = "-else.";
    String condFalse = "\nnot_foo() -> ok.\n";
    String endif = "-endif.";
    LexemeGraphNode lexemeGraphNode = buildLexemes(ifdef + condTrue + elze + condFalse + endif);
    assertTrue(lexemeGraphNode instanceof LexemeGraphForkNode);
    assertNull(lexemeGraphNode.next()); // it's a single fork node, so no successors are expected

    LexemeGraphForkNode forkNode = (LexemeGraphForkNode) lexemeGraphNode;
    List<LexemeGraphNode> alternatives = forkNode.getChildren();
    assertNotNull(alternatives);
    assertEquals(2, alternatives.size());

    LexemeGraphNode condTrueLexemes = alternatives.get(0);
    assertTrue(condTrueLexemes instanceof LexemeGraphLangNode);
    assertSameLexemesInNode((LexemeGraphLangNode) condTrueLexemes, condTrue);

    LexemeGraphNode condFalseLexemes = alternatives.get(1);
    assertTrue(condFalseLexemes instanceof LexemeGraphLangNode);
    assertSameLexemesInNode((LexemeGraphLangNode) condFalseLexemes, condFalse);
  }

  private void assertSameLexemesInNode(LexemeGraphLangNode node, String text) throws Exception {
    LexemeGraphNode expectedLexemes = buildLexemes(text);
    assertTrue(expectedLexemes instanceof LexemeGraphLangNode);
    assertEquals(((LexemeGraphLangNode) expectedLexemes).getLexemes(), node.getLexemes());
  }

  private LexemeGraphNode buildLexemes(String text) throws IOException {
    PreprocessingLexer<ErlangToken> lexer = new PreprocessingLexer<ErlangToken>(new ErlangLanguageProvider(), text);
    return lexer.buildLexemeGraph();
  }
}
