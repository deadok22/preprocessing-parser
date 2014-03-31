package ru.spbau.preprocessing.api.conditions;

/**
 * Abstraction of code's presence condition, that is a conjunction of all preceding
 * conditions in conditional directives.
 */
public interface PresenceCondition {
  PresenceCondition and(PresenceCondition presenceCondition);
  PresenceCondition or(PresenceCondition presenceCondition);
  PresenceCondition not(PresenceCondition presenceCondition);
  Value evaluate(MacroDefinitionsTable mdt);

  /**
   * Presence condition's value
   */
  public enum Value {
    TRUE,

    FALSE,

    /**
     * A presence condition's value depends on free macros
     */
    VARIANCE
  }
}
