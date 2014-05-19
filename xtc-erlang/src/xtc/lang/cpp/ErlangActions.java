package xtc.lang.cpp;

import ru.spbau.preprocessing.xtc.erlang.ErlangActionsBase;
import xtc.util.Runtime;

public class ErlangActions extends ErlangActionsBase {
  private final Runtime myRuntime;

  public ErlangActions(Runtime myRuntime) {
    this.myRuntime = myRuntime;
  }

  @Override
  public boolean isComplete(int id) {
    return getValueType(id) == ValueType.NODE;
  }

  @Override
  public Context getInitialContext() {
    return new CParsingContext(myRuntime);
  }
}
