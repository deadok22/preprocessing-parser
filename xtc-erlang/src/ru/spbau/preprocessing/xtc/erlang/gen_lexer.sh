#!/bin/bash

cpp erlang.l > erlang.l.flex

java -jar ../../../../../../../lib/jflex-1.5.1.jar erlang.l.flex

rm -f erlang.l.flex