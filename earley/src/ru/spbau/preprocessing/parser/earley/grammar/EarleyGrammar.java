package ru.spbau.preprocessing.parser.earley.grammar;

import com.google.common.collect.SetMultimap;

import java.util.Set;

public class EarleyGrammar {
  private final EarleySymbol myStartSymbol;
  private final SetMultimap<String, EarleyProduction> myProductions;

  public EarleyGrammar(EarleySymbol startSymbol, SetMultimap<String, EarleyProduction> productions) {
    myStartSymbol = startSymbol;
    myProductions = productions;
  }

  public EarleySymbol getStartSymbol() {
    return myStartSymbol;
  }

  public Set<EarleyProduction> getProductions(String name) {
    return myProductions.get(name);
  }
}
