#!/bin/bash

java -classpath out/production/benchmark:/usr/local/share/scala/scala-2.10.2/lib/scala-library.jar:out/production/earley:out/production/lexer:lib/guava-16.0.1.jar:lib/jsr305-1.3.9.jar:out/production/erlang:lib/junit-4.11.jar:lib/hamcrest-core-1.3.jar:out/production/TypeChef-erlang:lib/TypeChef-0.3.4.jar ru.spbau.preprocessing.benchmark.Benchmark $*