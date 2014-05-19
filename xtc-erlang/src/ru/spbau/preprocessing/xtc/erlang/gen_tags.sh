#!/bin/bash

cpp -DTAG erlang.l | grep -v "^#" > ErlangTag.java