package ru.spbau.preprocessing.parser.earley.grammar;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.SetMultimap;

import java.util.List;

public class EarleyGrammarBuilder {
  private EarleySymbol myStartSymbol;
  private SetMultimap<String, EarleyProduction> myProductions;

  private EarleyGrammarBuilder(EarleySymbol startSymbol) {
    myStartSymbol = startSymbol;
    myProductions = LinkedHashMultimap.create();
  }

  public RuleBuilder rule(String ruleName) {
    return new RuleBuilder(ruleName);
  }

  public EarleyGrammar build() {
    EarleyGrammar grammar = new EarleyGrammar(myStartSymbol, myProductions);
    myStartSymbol = null;
    myProductions = null;
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
