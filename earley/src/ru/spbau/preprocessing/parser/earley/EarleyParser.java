package ru.spbau.preprocessing.parser.earley;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import ru.spbau.preprocessing.api.LanguageProvider;
import ru.spbau.preprocessing.api.conditions.PresenceCondition;
import ru.spbau.preprocessing.lexer.lexemegraph.*;
import ru.spbau.preprocessing.parser.earley.ast.EarleyAstNode;
import ru.spbau.preprocessing.parser.earley.grammar.EarleyGrammar;
import ru.spbau.preprocessing.parser.earley.grammar.EarleyProduction;
import ru.spbau.preprocessing.parser.earley.grammar.EarleySymbol;
import ru.spbau.preprocessing.parser.earley.grammar.EarleyTerminal;

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
      //TODO implement
      throw new UnsupportedOperationException("Not implemented");
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
  }
}
