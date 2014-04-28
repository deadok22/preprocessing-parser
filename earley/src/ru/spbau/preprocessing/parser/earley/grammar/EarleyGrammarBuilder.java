package ru.spbau.preprocessing.parser.earley.grammar;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

public class EarleyGrammarBuilder {
  private EarleySymbol myStartSymbol;
  private SetMultimap<String, EarleyProduction> myProductions;
  private Set<EarleySymbol> myIgnoredSymbols;

  private EarleyGrammarBuilder(EarleySymbol startSymbol) {
    myStartSymbol = startSymbol;
    myProductions = LinkedHashMultimap.create();
    myIgnoredSymbols = Sets.newHashSet();
  }

  public RuleBuilder rule(String ruleName) {
    return new RuleBuilder(ruleName);
  }

  public EarleyGrammarBuilder ignoreTerminal(Object token) {
    myIgnoredSymbols.add(new EarleyTerminal<Object>(token));
    return this;
  }

  public EarleyGrammar build() {
    EarleyGrammar grammar = new EarleyGrammar(myStartSymbol, myProductions, myIgnoredSymbols);
    myStartSymbol = null;
    myProductions = null;
    myIgnoredSymbols = null;
    return grammar;
  }

  public static EarleyGrammarBuilder grammar(String startSymbolName) {
    return new EarleyGrammarBuilder(new EarleyNonTerminal(startSymbolName));
  }

  public class RuleBuilder {
    private String myRuleName;
    private List<EarleySymbol> mySymbols;

    public RuleBuilder(String ruleName) {
      myRuleName = ruleName;
      mySymbols = Lists.newArrayList();
    }

    public RuleBuilder terminal(Object token) {
      mySymbols.add(new EarleyTerminal<Object>(token));
      return this;
    }

    public RuleBuilder nonTerminal(String name) {
      mySymbols.add(new EarleyNonTerminal(name));
      return this;
    }

    public EarleyGrammarBuilder completeRule() {
      EarleyProduction production = new EarleyProduction(new EarleyNonTerminal(myRuleName), mySymbols);
      myProductions.put(production.getName(), production);
      mySymbols = null;
      myRuleName = null;
      return EarleyGrammarBuilder.this;
    }
  }
}
