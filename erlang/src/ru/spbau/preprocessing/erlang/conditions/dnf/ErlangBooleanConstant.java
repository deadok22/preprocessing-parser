package ru.spbau.preprocessing.erlang.conditions.dnf;

public abstract class ErlangBooleanConstant extends ErlangBooleanExpression {
  public static final ErlangBooleanExpression TRUE = new ErlangBooleanConstant() {
    @Override
    public boolean isTrue() {
      return true;
    }

    @Override
    public ErlangBooleanExpression and(ErlangBooleanExpression ebe) {
      return ebe;
    }

    @Override
    public ErlangBooleanExpression or(ErlangBooleanExpression ebe) {
      return this;
    }

    @Override
    public ErlangBooleanExpression not() {
      return FALSE;
    }

    @Override
    public String toString() {
      return "TRUE";
    }
  };

  public static final ErlangBooleanExpression FALSE = new ErlangBooleanConstant() {
    @Override
    public boolean isFalse() {
      return true;
    }

    @Override
    public ErlangBooleanExpression and(ErlangBooleanExpression ebe) {
      return this;
    }

    @Override
    public ErlangBooleanExpression or(ErlangBooleanExpression ebe) {
      return ebe;
    }

    @Override
    public ErlangBooleanExpression not() {
      return TRUE;
    }

    @Override
    public String toString() {
      return "FALSE";
    }
  };

  private ErlangBooleanConstant() {
  }

  @Override
  public boolean equals(Object o) {
    return o == this;
  }

  @Override
  public int hashCode() {
    return isTrue() ? 1 : 0;
  }
}
