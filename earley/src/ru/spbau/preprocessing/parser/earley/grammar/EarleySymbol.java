package ru.spbau.preprocessing.parser.earley.grammar;

public interface EarleySymbol {
  public abstract String getName();
  public abstract boolean isTerminal();
}
