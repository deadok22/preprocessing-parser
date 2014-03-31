package ru.spbau.preprocessing.erlang.conditions;

import ru.spbau.preprocessing.api.conditions.MacroDefinitionsTable;
import ru.spbau.preprocessing.api.conditions.PresenceCondition;

public abstract class ErlangPresenceCondition implements PresenceCondition {
  static final ErlangPresenceCondition TRUE = new ErlangPresenceCondition() {
    @Override
    public ErlangPresenceCondition and(PresenceCondition presenceCondition) {
      assert presenceCondition instanceof ErlangPresenceCondition;
      return (ErlangPresenceCondition) presenceCondition;
    }

    @Override
    public ErlangPresenceCondition or(PresenceCondition presenceCondition) {
      return this;
    }

    @Override
    public ErlangPresenceCondition not() {
      return FALSE;
    }

    @Override
    public Value evaluate(MacroDefinitionsTable mdt) {
      return Value.TRUE;

    }
  };

  static final ErlangPresenceCondition FALSE = new ErlangPresenceCondition() {
    @Override
    public ErlangPresenceCondition and(PresenceCondition presenceCondition) {
      return this;
    }

    @Override
    public ErlangPresenceCondition or(PresenceCondition presenceCondition) {
      assert presenceCondition instanceof ErlangPresenceCondition;
      return (ErlangPresenceCondition) presenceCondition;
    }

    @Override
    public ErlangPresenceCondition not() {
      return TRUE;
    }

    @Override
    public Value evaluate(MacroDefinitionsTable mdt) {
      return Value.FALSE;
    }
  };

  protected ErlangPresenceCondition() {
  }

  @Override
  public ErlangPresenceCondition and(PresenceCondition presenceCondition) {
    assert presenceCondition instanceof ErlangPresenceCondition;
    if (presenceCondition == TRUE) return this;
    if (presenceCondition == FALSE) return FALSE;
    return new ErlangAndPresenceCondition(this, (ErlangPresenceCondition) presenceCondition);
  }

  @Override
  public ErlangPresenceCondition or(PresenceCondition presenceCondition) {
    assert presenceCondition instanceof ErlangPresenceCondition;
    if (presenceCondition == FALSE) return this;
    if (presenceCondition == TRUE) return TRUE;
    return new ErlangOrPresenceCondition(this, (ErlangPresenceCondition) presenceCondition);
  }

  @Override
  public ErlangPresenceCondition not() {
    final ErlangPresenceCondition thiz = this;
    return new ErlangPresenceCondition() {
      @Override
      public ErlangPresenceCondition not() {
        return thiz;
      }

      @Override
      public Value evaluate(MacroDefinitionsTable mdt) {
        return thiz.evaluate(mdt).not();
      }
    };
  }

  @Override
  public abstract Value evaluate(MacroDefinitionsTable mdt);

  static class ErlangMacroDefinedCondition extends ErlangPresenceCondition {
    private final String myMacroName;

    ErlangMacroDefinedCondition(String macroName) {
      myMacroName = macroName;
    }

    @Override
    public Value evaluate(MacroDefinitionsTable mdt) {
      switch (mdt.getMacroDefinitionState(myMacroName)) {
        case DEFINED: return Value.TRUE;
        case UNDEFINED: return Value.FALSE;
        default: return Value.VARIANCE;
      }
    }
  }

  private static abstract class ErlangBinaryConditionExpression extends ErlangPresenceCondition {
    protected final ErlangPresenceCondition myLeft;
    protected final ErlangPresenceCondition myRight;

    protected ErlangBinaryConditionExpression(ErlangPresenceCondition left, ErlangPresenceCondition right) {
      myLeft = left;
      myRight = right;
    }
  }

  private static class ErlangAndPresenceCondition extends ErlangBinaryConditionExpression {
    public ErlangAndPresenceCondition(ErlangPresenceCondition left, ErlangPresenceCondition right) {
      super(left, right);
    }

    @Override
    public Value evaluate(MacroDefinitionsTable mdt) {
      Value leftValue = myLeft.evaluate(mdt);
      if (leftValue == Value.FALSE) {
        return Value.FALSE;
      }
      Value rightValue = myRight.evaluate(mdt);
      if (rightValue == Value.FALSE) {
        return Value.FALSE;
      }
      if (leftValue == Value.VARIANCE || rightValue == Value.VARIANCE) {
        return Value.VARIANCE;
      }
      return Value.TRUE;
    }
  }

  private class ErlangOrPresenceCondition extends ErlangBinaryConditionExpression {
    public ErlangOrPresenceCondition(ErlangPresenceCondition left, ErlangPresenceCondition right) {
      super(left, right);
    }

    @Override
    public Value evaluate(MacroDefinitionsTable mdt) {
      Value leftValue = myLeft.evaluate(mdt);
      if (leftValue == Value.TRUE) {
        return Value.TRUE;
      }
      Value rightValue = myRight.evaluate(mdt);
      if (rightValue == Value.TRUE) {
        return Value.TRUE;
      }
      if (leftValue == Value.VARIANCE || rightValue == Value.VARIANCE) {
        return Value.VARIANCE;
      }
      return Value.FALSE;
    }
  }
}
