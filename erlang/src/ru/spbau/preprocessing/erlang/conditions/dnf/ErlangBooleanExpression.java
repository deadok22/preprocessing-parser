package ru.spbau.preprocessing.erlang.conditions.dnf;

public abstract class ErlangBooleanExpression {
  protected ErlangBooleanExpression() {
  }

  public boolean isTrue() {
    return false;
  }

  public boolean isFalse() {
    return false;
  }

  public abstract ErlangBooleanExpression and(ErlangBooleanExpression ebe);
  public abstract ErlangBooleanExpression or(ErlangBooleanExpression ebe);
  public abstract ErlangBooleanExpression not();

  @Override
  public abstract String toString();

  public abstract boolean equals(Object o);
  public abstract int hashCode();
}
