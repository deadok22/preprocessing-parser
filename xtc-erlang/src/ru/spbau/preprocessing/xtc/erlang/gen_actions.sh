#!/bin/bash

./gen_actions.pl < erlang.y | java -cp ../../../../../../../lib/xtc.jar xtc.lang.cpp.ActionGenerator ErlangActionsBase > ErlangActionsBase.java