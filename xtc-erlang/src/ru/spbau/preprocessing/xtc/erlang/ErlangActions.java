package ru.spbau.preprocessing.xtc.erlang;

public class ErlangActions extends ErlangActionsBase {

  @Override
  public boolean isComplete(int id) {
    return getValueType(id) == ValueType.NODE;
  }
}
