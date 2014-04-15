-ifdef(X).
-define(MACRO, "HELLO").
-else.
-define(MACRO, "WORLD").
-endif.

foo() ->
    io:format(?MACRO).