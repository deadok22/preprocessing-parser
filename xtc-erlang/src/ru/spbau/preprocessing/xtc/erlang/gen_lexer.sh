#!/bin/bash

cpp -DLEXER erlang.l | grep -v "^#" > erlang.l.flex

java -jar ../../../../../../../lib/jflex-1.5.1.jar erlang.l.flex

rm -f erlang.l.flex