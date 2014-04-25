package ru.spbau.preprocessing.parser.earley.parser;

import com.google.common.collect.Lists;
import ru.spbau.preprocessing.lexer.lexemegraph.LexemeGraphNode;
import ru.spbau.preprocessing.parser.earley.ast.*;
import ru.spbau.preprocessing.parser.earley.grammar.EarleyGrammar;
import ru.spbau.preprocessing.parser.earley.grammar.EarleyNonTerminal;
import ru.spbau.preprocessing.parser.earley.grammar.EarleySymbol;
import ru.spbau.preprocessing.parser.earley.grammar.EarleyTerminal;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class EarleyParser {
  private final EarleyGrammar myGrammar;

  public EarleyParser(EarleyGrammar grammar) {
    myGrammar = grammar;
  }

  public EarleyAstNode parse(LexemeGraphNode lexemeGraphNode) {
    EarleyRecognizer recognizer = new EarleyRecognizer(myGrammar);
    lexemeGraphNode.accept(recognizer);
    EarleyChart chart = recognizer.completeChart();
    return buildAst(chart);
  }

  private EarleyAstNode buildAst(EarleyChart chart) {
    EarleySymbol startSymbol = myGrammar.getStartSymbol();
    EarleyChartColumn lastColumn = chart.lastColumn();

    List<EarleyItem> completedStartSymbolItems = Lists.newArrayList();
    for (EarleyItem item : lastColumn) {
      if (item.isComplete() && startSymbol.equals(item.getSymbol()) && chart.isFirstColumn(item.getStartColumn())) {
        completedStartSymbolItems.add(item);
      }
    }

    //input does not match grammar
    if (completedStartSymbolItems.isEmpty()) {
      return null;
    }

    return buildNode(completedStartSymbolItems, lastColumn);
  }

  /**
   * Build a subtree for a set of different completion variants of the same symbol.
   *
   * @param completedItems a set of completed items with the same span, but possibly different productions/presence conditions
   * @param column         an Earley chart column where completed items were completed.
   * @return a subtree or null if a subtree cannot be parsed.
   */
  private EarleyAstNode buildNode(Collection<EarleyItem> completedItems, EarleyChartColumn column) {
    assert !completedItems.isEmpty();

    //TODO resolve ambiguities and see if you can reduce the completed items set

    if (completedItems.size() == 1) {
      return buildCompleteNode(completedItems.iterator().next(), column);
    }

    //TODO make sure you pass alternatives and not ambiguities here.
    return buildAlternatives(completedItems, column);
  }

  private EarleyAlternativesNode buildAlternatives(Collection<EarleyItem> completedItems, EarleyChartColumn column) {
    //TODO handle presence conditions
    List<EarleyConditionalBranchNode> alternatives = Lists.newArrayListWithExpectedSize(completedItems.size());
    EarleyAlternativesNode alternativesNode = new EarleyAlternativesNode(alternatives);
    for (EarleyItem item : completedItems) {
      EarleyAstNode branchBody = buildCompleteNode(item, column);
      EarleyConditionalBranchNode branchNode = new EarleyConditionalBranchNode(null, Collections.singletonList(branchBody));
      alternatives.add(branchNode);
    }
    return alternativesNode;
  }

  private EarleyAstNode buildCompleteNode(EarleyItem item, EarleyChartColumn column) {
    assert item.isComplete();

    // each descriptor corresponds to one different previous item
    Set<EarleyItemDescriptor> descriptors = column.getDescriptors(item);
    //TODO resolve ambiguities
    assert !descriptors.isEmpty();

    if (descriptors.size() == 1) {
      return buildNodeForDescriptor(item, descriptors.iterator().next(), column);
    }

    //these are either unresolved ambiguities or are under different presence conditions
    List<EarleyConditionalBranchNode> alternatives = Lists.newArrayListWithExpectedSize(descriptors.size());
    EarleyAlternativesNode alternativesNode = new EarleyAlternativesNode(alternatives);
    for (EarleyItemDescriptor descriptor : descriptors) {
      EarleyAstNode branchBody = buildNodeForDescriptor(item, descriptor, column);
      EarleyConditionalBranchNode branchNode = new EarleyConditionalBranchNode(null, Collections.singletonList(branchBody));
      alternatives.add(branchNode);
    }
    return alternativesNode;
  }

  private EarleyAstNode buildNodeForDescriptor(EarleyItem item, EarleyItemDescriptor descriptor, EarleyChartColumn column) {
    EarleyNonTerminal symbol = item.getSymbol();
    List<EarleyAstNode> reversedChildren = Lists.newArrayList();
    buildNodesForPreviousSymbols(reversedChildren, item, descriptor, column);
    return new EarleyCompositeNode(symbol, Lists.reverse(reversedChildren));
  }

  private void buildNodesForPreviousSymbols(List<EarleyAstNode> reversedChildren, EarleyItem item, EarleyItemDescriptor descriptor, EarleyChartColumn column) {
    if (item.getLastMatchedSymbol() == null) return;
    EarleyItem predecessor = descriptor.getPredecessor();

    Set<EarleyItem> reductionItems = descriptor.getReductionItems();
    if (reductionItems.isEmpty()) {
      EarleySymbol lastMatchedSymbol = item.getLastMatchedSymbol();
      assert lastMatchedSymbol.isTerminal();
      //noinspection unchecked
      reversedChildren.add(new EarleyLeafNode<Object>((EarleyTerminal<Object>) lastMatchedSymbol));
      buildNodesForPredecessor(reversedChildren, predecessor, column.previousColumn());
    }
    else {
      reductionItems = excludeReductionAmbiguities(item, reductionItems);
      if (reductionItems.size() == 1) {
        EarleyItem reductionItem = reductionItems.iterator().next();
        EarleyAstNode nodeForReduction = buildCompleteNode(reductionItem, column);
        reversedChildren.add(nodeForReduction);
        buildNodesForPredecessor(reversedChildren, predecessor, reductionItem.getStartColumn());
      }
      else {
        List<EarleyConditionalBranchNode> branchNodes = Lists.newArrayListWithExpectedSize(reductionItems.size());
        for (EarleyItem reductionItem : reductionItems) {
          EarleyAstNode nodeForReduction = buildCompleteNode(reductionItem, column);
          List<EarleyAstNode> reversedBranchNodes = Lists.newArrayList();
          reversedBranchNodes.add(nodeForReduction);
          buildNodesForPredecessor(reversedBranchNodes, predecessor, reductionItem.getStartColumn());
          branchNodes.add(new EarleyConditionalBranchNode(null, Lists.reverse(reversedBranchNodes)));
        }
        reversedChildren.add(new EarleyAlternativesNode(branchNodes));
      }
    }
  }

  private void buildNodesForPredecessor(List<EarleyAstNode> reversedChildren, EarleyItem predecessor, EarleyChartColumn predecessorEndColumn) {
    Set<EarleyItemDescriptor> predecessorDescriptors = predecessorEndColumn.getDescriptors(predecessor);
    if (predecessorDescriptors.isEmpty()) return;
    if (predecessorDescriptors.size() == 1) {
      buildNodesForPreviousSymbols(reversedChildren, predecessor, predecessorDescriptors.iterator().next(), predecessorEndColumn);
    }
    else {
      List<EarleyConditionalBranchNode> branchNodes = Lists.newArrayListWithExpectedSize(predecessorDescriptors.size());
      for (int i = 0; i < predecessorDescriptors.size(); i++) {
        List<EarleyAstNode> reversedBranchItems = Lists.newArrayList();
        for (EarleyItemDescriptor predecessorDescriptor : predecessorDescriptors) {
          buildNodesForPreviousSymbols(reversedBranchItems, predecessor, predecessorDescriptor, predecessorEndColumn);
        }
        branchNodes.add(new EarleyConditionalBranchNode(null, Lists.reverse(reversedBranchItems)));
      }
      reversedChildren.add(new EarleyAlternativesNode(branchNodes));
    }
  }

  //TODO improve
  private Set<EarleyItem> excludeReductionAmbiguities(EarleyItem currentItem, Set<EarleyItem> reductions) {
    if (currentItem.getProduction().isLeftAssociative()) {
      EarleyItem rightmostStartingItem = null;
      for (EarleyItem reduction : reductions) {
        if (rightmostStartingItem == null || rightmostStartingItem.getStartColumn().isBefore(reduction.getStartColumn())) {
          rightmostStartingItem = reduction;
        }
      }
      return Collections.singleton(rightmostStartingItem);
    }
    return reductions;
  }
}
