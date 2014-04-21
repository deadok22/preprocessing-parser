package ru.spbau.preprocessing.parser.earley;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import ru.spbau.preprocessing.api.LanguageProvider;
import ru.spbau.preprocessing.api.conditions.PresenceCondition;
import ru.spbau.preprocessing.lexer.lexemegraph.*;
import ru.spbau.preprocessing.parser.earley.ast.*;
import ru.spbau.preprocessing.parser.earley.grammar.*;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class EarleyParser<TokenTypeBase> {
  private final LanguageProvider<TokenTypeBase> myLanguageProvider;
  private final EarleyGrammar myGrammar;

  public EarleyParser(LanguageProvider<TokenTypeBase> languageProvider, EarleyGrammar grammar) {
    myLanguageProvider = languageProvider;
    myGrammar = grammar;
  }

  public EarleyAstNode parse(LexemeGraphNode lexemeGraph) {
    EarleyChart chart = new EarleyChart();
    EarleySymbol startSymbol = myGrammar.getStartSymbol();
    Set<EarleyProduction> productions = myGrammar.getProductions(startSymbol.getName());
    final EarleyChart.State state = chart.newState();
    state.addItems(Lists.transform(Lists.newArrayList(productions), new Function<EarleyProduction, EarleyChart.Item>() {
      @Override
      public EarleyChart.Item apply(EarleyProduction production) {
        return state.createItem(production, myLanguageProvider.createPresenceConditionFactory().getTrue());
      }
    }));
    EarleyParsingVisitor visitor = new EarleyParsingVisitor(chart);
    lexemeGraph.accept(visitor);
    return visitor.getParseTree();
  }

  private class EarleyParsingVisitor implements LexemeGraphVisitor {
    private final EarleyChart myChart;

    private EarleyParsingVisitor(EarleyChart chart) {
      myChart = chart;
    }

    public EarleyAstNode getParseTree() {
      EarleySymbol startSymbol = myGrammar.getStartSymbol();
      List<EarleyChart.Item> completedStartSymbolParses = myChart.lastState().getCompletionsOf((EarleyNonTerminal) startSymbol);

      //input was not matched.
      if (completedStartSymbolParses.isEmpty()) {
        return null;
      }

      return buildAlternatives(completedStartSymbolParses, myChart.lastState());
    }

    @Override
    public void visitForkNode(LexemeGraphForkNode forkNode) {
      //TODO implement
      throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void visitLangNode(LexemeGraphLangNode langNode) {
      @SuppressWarnings("unchecked") List<Lexeme<?>> lexemes = (List<Lexeme<?>>) langNode.getLexemes();
      for (Lexeme<?> lexeme : lexemes) {
        doEarleyStep(lexeme, langNode.getPresenceCondition());
      }
    }

    private void doEarleyStep(Lexeme<?> lexeme, PresenceCondition presenceCondition) {
      EarleyTerminal<Object> terminal = new EarleyTerminal<Object>(lexeme.getType());
      EarleyChart.State currentState = myChart.lastState();
      EarleyChart.State nextState = myChart.newState();

      LinkedHashSet<EarleyChart.Item> addToCurrentState = Sets.newLinkedHashSet();
      LinkedHashSet<EarleyChart.Item> addToNextState = Sets.newLinkedHashSet();

      //TODO this loop can be optimized:
      //TODO - items can be added rightaway and considered in predict, scan and complete
      //TODO - newly added items can be cached
      do {
        currentState.addItems(addToCurrentState);
        nextState.addItems(addToNextState);

        predict(currentState, addToCurrentState, presenceCondition);
        scan(terminal, currentState, addToNextState);
        complete(currentState, addToCurrentState);
      } while (changesWillBeMade(currentState, nextState, addToCurrentState, addToNextState));
    }

    private boolean changesWillBeMade(EarleyChart.State currentState, EarleyChart.State nextState,
                                      LinkedHashSet<EarleyChart.Item> addToCurrentState, LinkedHashSet<EarleyChart.Item> addToNextState) {
      return !currentState.containsAll(addToCurrentState) || !nextState.containsAll(addToNextState);
    }

    private void predict(final EarleyChart.State currentState, LinkedHashSet<EarleyChart.Item> addToCurrentState, final PresenceCondition presenceCondition) {
      for (EarleyChart.Item item : currentState) {
        EarleySymbol nextExpectedSymbol = item.getNextExpectedSymbol();
        if (nextExpectedSymbol != null && !nextExpectedSymbol.isTerminal()) {
          Set<EarleyProduction> productions = myGrammar.getProductions(nextExpectedSymbol.getName());
          addToCurrentState.addAll(Collections2.transform(productions, new Function<EarleyProduction, EarleyChart.Item>() {
            @Override
            public EarleyChart.Item apply(EarleyProduction production) {
              return currentState.createItem(production, presenceCondition);
            }
          }));
        }
      }
    }

    private void scan(EarleySymbol nextSymbol, EarleyChart.State currentState, LinkedHashSet<EarleyChart.Item> addToNextState) {
      for (EarleyChart.Item item : currentState) {
        EarleySymbol nextExpectedSymbol = item.getNextExpectedSymbol();
        if (Objects.equal(nextExpectedSymbol, nextSymbol)) {
          addToNextState.add(item.matchOneSymbol());
        }
      }
    }

    private void complete(EarleyChart.State currentState, LinkedHashSet<EarleyChart.Item> addToCurrentState) {
      for (EarleyChart.Item item : currentState) {
        if (item.isComplete()) {
          scan(item.getProduction().getLeft(), item.getStartState(), addToCurrentState);
        }
      }
    }

    private EarleyAstNode buildAlternatives(List<EarleyChart.Item> startSymbolParses, EarleyChart.State state) {
      //TODO handle ambiguities which have the same presence condition
      //TODO restore symbols' text
      //TODO think of a merge strategy
      //each symbol could have been obtained using different productions

      List<EarleyConditionalBranchNode> branches = Lists.newArrayListWithExpectedSize(startSymbolParses.size());
      for (EarleyChart.Item parse : startSymbolParses) {
        EarleyAstNode subtree = buildSubtree(parse, state);
        if (subtree == null) return null;
        branches.add(new EarleyConditionalBranchNode(parse.getPresenceCondition(), Collections.singletonList(subtree)));
      }

      return new EarleyAlternativesNode(branches);
    }

    private EarleyAstNode buildSubtree(EarleyChart.Item item, EarleyChart.State itemCompletionState) {
      assert item.isComplete();

      EarleyProduction production = item.getProduction();
      List<EarleySymbol> productionSymbols = production.getRight();

      EarleyNonTerminal nodeSymbol = production.getLeft();
      List<EarleyAstNode> reveresedChildrenList = Lists.newArrayListWithExpectedSize(productionSymbols.size());

      Set<EarleyChart.State> states = Sets.newHashSet();
      Set<EarleyChart.State> newStates = Sets.newHashSet();
      newStates.add(itemCompletionState);

      //TODO handle presence conditions
      for (int i = productionSymbols.size() - 1; i >= 0; i--) {
        Set<EarleyChart.State> tmp = states;
        states = newStates;
        newStates = tmp;
        newStates.clear();

        EarleySymbol symbol = productionSymbols.get(i);

        if (symbol.isTerminal()) {
          //TODO add different symbols for different completion states
          //noinspection unchecked
          reveresedChildrenList.add(new EarleyLeafNode((EarleyTerminal) symbol));
          //this part of production was matched by scanning
          for (EarleyChart.State state : states) {
            EarleyChart.State previousState = state.previousState();
            if (previousState != null) {
              newStates.add(previousState);
            }
          }
        }
        else {
          for (EarleyChart.State state : states) {
            List<EarleyChart.Item> completions = state.getCompletionsOf((EarleyNonTerminal) symbol);
            EarleyAstNode alternativesNode = buildAlternatives(completions, state);
            reveresedChildrenList.add(alternativesNode);
            newStates.addAll(Collections2.transform(completions, new Function<EarleyChart.Item, EarleyChart.State>() {
              @Override
              public EarleyChart.State apply(EarleyChart.Item item) {
                return item.getStartState();
              }
            }));
          }
        }
      }

      return new EarleyCompositeNode(nodeSymbol, Lists.reverse(reveresedChildrenList));
    }
  }
}
