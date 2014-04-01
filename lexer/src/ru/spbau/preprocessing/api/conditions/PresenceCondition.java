package ru.spbau.preprocessing.api.conditions;

/**
 * Abstraction of code's presence condition, that is a conjunction of all preceding
 * conditions in conditional directives.
 */
public interface PresenceCondition {
  PresenceCondition and(PresenceCondition presenceCondition);
  PresenceCondition or(PresenceCondition presenceCondition);
  PresenceCondition not();
  Value value();

  /**
   * Presence condition's value
   */
  public enum Value {
    TRUE {
      @Override
      public Value not() {
        return FALSE;
      }

      @Override
      public Value and(Value value) {
        return value;
      }

      @Override
      public Value or(Value value) {
        return TRUE;
      }
    },

    FALSE {
      @Override
      public Value not() {
        return TRUE;
      }

      @Override
      public Value and(Value value) {
        return FALSE;
      }

      @Override
      public Value or(Value value) {
        return value;
      }
    },

    /**
     * A presence condition's value depends on free macros
     */
    VARIANCE {
      @Override
      public Value or(Value value) {
        return value == TRUE ? TRUE : VARIANCE;
      }
    };

    public Value not() {
      return VARIANCE;
    }

    public Value and(Value value) {
      return VARIANCE;
    }

    public Value or(Value value) {
      return VARIANCE;
    }
  }
}
