package ru.spbau.preprocessing.lexer;

import ru.spbau.preprocessing.api.conditions.ConditionalContext;
import ru.spbau.preprocessing.api.conditions.PresenceCondition;
import ru.spbau.preprocessing.api.conditions.PresenceConditionFactory;
import ru.spbau.preprocessing.api.preprocessor.PreprocessorLanguageMacroDefinitionNode;
import ru.spbau.preprocessing.api.preprocessor.PreprocessorLanguageMacroUndefinitionNode;

public class ConditionalContextImpl implements ConditionalContext {
  private final PresenceConditionFactory myPresenceConditionFactory;
  private final MacroDefinitionsTableImpl myMacroDefinitionsTable;
  private PresenceCondition myCurrentPresenceCondition;

  public ConditionalContextImpl(PresenceConditionFactory presenceConditionFactory) {
    this(presenceConditionFactory, new MacroDefinitionsTableImpl(), presenceConditionFactory.getTrue());
  }

  public ConditionalContextImpl(PresenceConditionFactory presenceConditionFactory,
                                MacroDefinitionsTableImpl macroDefinitionsTable,
                                PresenceCondition currentPresenceCondition) {
    myPresenceConditionFactory = presenceConditionFactory;
    myMacroDefinitionsTable = macroDefinitionsTable;
    myCurrentPresenceCondition = currentPresenceCondition;
  }

  @Override
  public MacroDefinitionsTableImpl getMacroTable() {
    return myMacroDefinitionsTable;
  }

  @Override
  public PresenceCondition getCurrentPresenceCondition() {
    return myCurrentPresenceCondition;
  }

  public void defineMacro(PreprocessorLanguageMacroDefinitionNode definition) {
    myMacroDefinitionsTable.define(definition, myCurrentPresenceCondition);
  }

  public void undefineMacro(PreprocessorLanguageMacroUndefinitionNode undefinition) {
    myMacroDefinitionsTable.undefine(undefinition, myCurrentPresenceCondition);
  }

  public ConditionalContextImpl andCondition(PresenceCondition condition) {
    myCurrentPresenceCondition = myCurrentPresenceCondition.and(condition);
    return this;
  }

  /**
   * All conditional contexts share the same macro definitions table,
   * but presence conditions for each conditional context should be different.
   */
  public ConditionalContextImpl copy() {
    return new ConditionalContextImpl(myPresenceConditionFactory, myMacroDefinitionsTable, myCurrentPresenceCondition);
  }
}
