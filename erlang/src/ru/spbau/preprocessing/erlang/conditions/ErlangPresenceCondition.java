package ru.spbau.preprocessing.erlang.conditions;

import ru.spbau.preprocessing.api.conditions.PresenceCondition;

public class ErlangPresenceCondition implements PresenceCondition {
  public static final ErlangPresenceCondition TRUE = new ErlangPresenceCondition(Value.TRUE);
  public static final ErlangPresenceCondition FALSE = new ErlangPresenceCondition(Value.FALSE);

  private final Value myValue;

  private ErlangPresenceCondition(Value value) {
    myValue = value;
  }

  @Override
  public PresenceCondition and(PresenceCondition presenceCondition) {
    return new ErlangAndPresenceCondition(this, presenceCondition);
  }

  @Override
  public PresenceCondition or(PresenceCondition presenceCondition) {
    return new ErlangOrPresenceCondition(this, presenceCondition);
  }

  @Override
  public PresenceCondition not() {
    return new ErlangNotPresenceCondition(this);
  }

  @Override
  public final Value value() {
    return myValue;
  }

  @Override
  public String toString() {
    return String.valueOf(myValue);
  }

  private static final class ErlangNotPresenceCondition extends ErlangPresenceCondition {
    private final ErlangPresenceCondition myArgument;

    ErlangNotPresenceCondition(ErlangPresenceCondition argument) {
      super(argument.value().not());
      myArgument = argument;
    }

    @Override
    public String toString() {
      return "! " + myArgument;
    }
  }

  private static abstract class ErlangBinaryOperationPresenceCondition extends ErlangPresenceCondition {
    private final PresenceCondition myLeft;
    private final PresenceCondition myRight;

    protected ErlangBinaryOperationPresenceCondition(Value value, PresenceCondition left, PresenceCondition right) {
      super(value);
      myLeft = left;
      myRight = right;
    }

    protected abstract String getOperation();

    @Override
    public String toString() {
      return "(" + myLeft + " " + getOperation() + " " + myRight + ")";
    }
  }

  private static final class ErlangAndPresenceCondition extends ErlangBinaryOperationPresenceCondition {
    protected ErlangAndPresenceCondition(PresenceCondition left, PresenceCondition right) {
      super(left.value().and(right.value()), left, right);
    }

    @Override
    protected String getOperation() {
      return "&";
    }
  }

  private static final class ErlangOrPresenceCondition extends ErlangBinaryOperationPresenceCondition {
    private ErlangOrPresenceCondition(PresenceCondition left, PresenceCondition right) {
      super(left.value().or(right.value()), left, right);
    }

    @Override
    protected String getOperation() {
      return "|";
    }
  }

  static final class ErlangMacroDefinedPresenceCondition extends ErlangPresenceCondition {
    private final String myMacroName;

    protected ErlangMacroDefinedPresenceCondition(Value value, String macroName) {
      super(value);
      myMacroName = macroName;
    }

    @Override
    public String toString() {
      return "(is_defined " + myMacroName + ")";
    }
  }
}
