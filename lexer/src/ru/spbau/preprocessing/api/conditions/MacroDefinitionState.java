package ru.spbau.preprocessing.api.conditions;

/**
 * An enumeration of all possible macro definition states at some point in program.
 */
public enum MacroDefinitionState {
  /**
   * A macro is either defined or multiply-defined at this point
   * in all possible program configurations.
   */
  DEFINED,

  /**
   * A macro is undefined in all possible program configurations, that is,
   * this point is preceded by macro undefinition(s).
   */
  UNDEFINED,

  /**
   * A macro can be both defined and undefined in different configurations of a program.
   * It can be caused by a macro being free or by having a macro defined at one configuration branch
   * and either not defined or undefined in another one.
   */
  FREE
}
