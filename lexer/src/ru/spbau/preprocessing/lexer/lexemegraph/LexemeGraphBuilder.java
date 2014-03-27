package ru.spbau.preprocessing.lexer.lexemegraph;

import java.util.ArrayList;
import java.util.List;

public class LexemeGraphBuilder<TokenTypeBase> {
  private final LexemeGraphForkNode myTopForkNode;
  private final int myTopForkNodeChildIdx;

  private LexemeGraphNode myTopNode;
  private LexemeGraphNode myLastNode;

  private List<Lexeme<TokenTypeBase>> myLexemeBuffer;

  public LexemeGraphBuilder() {
    this(null, -1);
  }

  private LexemeGraphBuilder(LexemeGraphForkNode topForkNode, int topForkNodeChildIdx) {
    myTopForkNode = topForkNode;
    myTopForkNodeChildIdx = topForkNodeChildIdx;
  }

  public void addLexeme(Lexeme<TokenTypeBase> lexeme) {
    ensureLexemeBufferExists();
    myLexemeBuffer.add(lexeme);
  }

  public List<LexemeGraphBuilder<TokenTypeBase>> fork(int alternativesCount) {
    completeLangNode();
    ArrayList<LexemeGraphNode> childNodes = new ArrayList<LexemeGraphNode>(alternativesCount);
    for (int i = 0; i < alternativesCount; i++) {
      childNodes.add(null);
    }
    LexemeGraphForkNode forkNode = new LexemeGraphForkNode(childNodes);
    completeNode(forkNode);
    List<LexemeGraphBuilder<TokenTypeBase>> branchBuilders = new ArrayList<LexemeGraphBuilder<TokenTypeBase>>(alternativesCount);
    for (int i = 0; i < alternativesCount; i++) {
      branchBuilders.add(new LexemeGraphBuilder<TokenTypeBase>(forkNode, i));
    }
    //TODO track branch builders' completion
    return branchBuilders;
  }

  public LexemeGraphNode build() {
    completeLangNode();
    if (myTopForkNode != null) {
      myTopForkNode.setChild(myTopForkNodeChildIdx, myTopNode);
    }
    return myTopNode;
  }

  private void ensureLexemeBufferExists() {
    if (myLexemeBuffer == null) {
      myLexemeBuffer = new ArrayList<Lexeme<TokenTypeBase>>();
    }
  }

  private void completeLangNode() {
    if (myLexemeBuffer == null) return;
    completeNode(new LexemeGraphLangNode<TokenTypeBase>(myLexemeBuffer));
    myLexemeBuffer = null;
  }

  private void completeNode(LexemeGraphNode node) {
    if (myTopNode == null) {
      myTopNode = node;
    }
    if (myLastNode != null) {
      myLastNode.setNext(node);
    }
    myLastNode = node;
  }
}
