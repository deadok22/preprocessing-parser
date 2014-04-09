-define(MACRO, "HELLO").
-define(MACRO(X, Y), "HELLO").
-undef(MACRO).

foo() ->
    ?MACRO,
    ?MACRO(1, 2).