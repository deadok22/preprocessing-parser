package ru.spbau.preprocessing.parser.earley.parser;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import ru.spbau.preprocessing.api.conditions.PresenceCondition;
import ru.spbau.preprocessing.api.conditions.PresenceConditionFactory;
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
  private final PresenceConditionFactory myPresenceConditionFactory;

  public EarleyParser(EarleyGrammar grammar, PresenceConditionFactory presenceConditionFactory) {
    myGrammar = grammar;
    myPresenceConditionFactory = presenceConditionFactory;
  }

  public EarleyAstNode parse(LexemeGraphNode lexemeGraphNode) {
    EarleyRecognizer recognizer = new EarleyRecognizer(myPresenceConditionFactory, myGrammar);
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
    Set<EarleyReduction> reductions = excludeReductionAmbiguities(item, descriptor.getReductions(), column);

    if (reductions.size() == 1) {
      EarleyReduction reduction = reductions.iterator().next();
      EarleyAstNode nodeForReduction = buildNodeForReduction(reduction, column);
      reversedChildren.add(nodeForReduction);
      buildNodesForPredecessor(reversedChildren, predecessor, reduction.getStartColumn());
    }
    else {
      List<EarleyConditionalBranchNode> branchNodes = Lists.newArrayListWithExpectedSize(reductions.size());
      for (EarleyReduction reduction : reductions) {
        EarleyAstNode nodeForReduction = buildNodeForReduction(reduction, column);
        List<EarleyAstNode> reversedBranchNodes = Lists.newArrayList();
        reversedBranchNodes.add(nodeForReduction);
        buildNodesForPredecessor(reversedBranchNodes, predecessor, reduction.getStartColumn());
        branchNodes.add(new EarleyConditionalBranchNode(null, Lists.reverse(reversedBranchNodes)));
      }
      reversedChildren.add(new EarleyAlternativesNode(branchNodes));
    }
  }

  private EarleyAstNode buildNodeForReduction(EarleyReduction reduction, EarleyChartColumn column) {
    if (reduction.getSymbol().isTerminal()) {
      //noinspection unchecked
      return new EarleyLeafNode<Object>((EarleyTerminal<Object>) reduction.getTerminal());
    }
    else {
      return buildCompleteNode(reduction.getCompletedItem(), column);
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
      for (EarleyItemDescriptor predecessorDescriptor : predecessorDescriptors) {
        List<EarleyAstNode> reversedBranchItems = Lists.newArrayList();
        buildNodesForPreviousSymbols(reversedBranchItems, predecessor, predecessorDescriptor, predecessorEndColumn);
        branchNodes.add(new EarleyConditionalBranchNode(null, Lists.reverse(reversedBranchItems)));
      }
      reversedChildren.add(new EarleyAlternativesNode(branchNodes));
    }
  }

  private Set<EarleyReduction> excludeReductionAmbiguities(EarleyItem currentItem, Set<EarleyReduction> reductions, EarleyChartColumn column) {
    HashMultimap<PresenceCondition, EarleyReduction> possibleAmbiguities = HashMultimap.create();
    for (EarleyReduction reduction : reductions) {
      if (reduction.getTerminal() == null) {
        EarleyItem reductionItem = reduction.getCompletedItem();
        Set<EarleyItemDescriptor> descriptors = column.getDescriptors(reductionItem);
        for (EarleyItemDescriptor descriptor : descriptors) {
          possibleAmbiguities.put(descriptor.getPresenceCondition(), reduction);
        }
      }
      else {
        //TODO consider presence conditions for reductions obtained via lexeme consuming
        // for now we'll use a presence condition of it's parent, which is wrong and can lead to incorrect parse trees
        Set<EarleyItemDescriptor> descriptors = column.getDescriptors(currentItem);
        PresenceCondition presenceCondition = myPresenceConditionFactory.getFalse();
        for (EarleyItemDescriptor descriptor : descriptors) {
          presenceCondition = presenceCondition.or(descriptor.getPresenceCondition());
        }
        possibleAmbiguities.put(presenceCondition, reduction);
      }
    }

    //TODO this ambiguity resolution is primitive - you should use grammar rules precedence, associativity, etc. here.
    if (currentItem.getProduction().isLeftAssociative()) {
      Set<EarleyReduction> unambiguousReductionSet = Sets.newHashSet();
      for (PresenceCondition presenceCondition : possibleAmbiguities.keySet()) {
        EarleyReduction rightmostStartingItem = null;
        for (EarleyReduction reduction : possibleAmbiguities.get(presenceCondition)) {
          if (rightmostStartingItem == null || rightmostStartingItem.getStartColumn().isBefore(reduction.getStartColumn())) {
            rightmostStartingItem = reduction;
          }
        }
        unambiguousReductionSet.add(rightmostStartingItem);
      }
      return unambiguousReductionSet;
    }

    return reductions;
  }
}
