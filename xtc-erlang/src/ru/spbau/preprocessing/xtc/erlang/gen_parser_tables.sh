#!/bin/bash

bison -o erlang.tab.c erlang.y

gcc -DYYDEBUG -DYYPRINT\(a,b,c\) -o gen_tables gen_tables.c

./gen_tables > ErlangForkMergeParserTables.java

rm -f erlang.tab.c gen_tables