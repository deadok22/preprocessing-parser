package ru.spbau.preprocessing.parser.earley.grammar;

import com.google.common.collect.SetMultimap;

import java.util.Set;

public class EarleyGrammar {
  private final EarleySymbol myStartSymbol;
  private final SetMultimap<String, EarleyProduction> myProductions;
  private final Set<EarleySymbol> myIgnoredSymbols;

  public EarleyGrammar(EarleySymbol startSymbol, SetMultimap<String, EarleyProduction> productions, Set<EarleySymbol> ignoredSymbols) {
    myStartSymbol = startSymbol;
    myProductions = productions;
    myIgnoredSymbols = ignoredSymbols;
  }

  public EarleySymbol getStartSymbol() {
    return myStartSymbol;
  }

  public Set<EarleyProduction> getProductions(String name) {
    return myProductions.get(name);
  }

  public boolean isIgnoredSymbol(EarleySymbol symbol) {
    return myIgnoredSymbols.contains(symbol);
  }
}
