-ifdef(X).
-define(MACRO, nan).
-else.
-define(MACRO, 10).
-endif.

foo() ->
    ?MACRO.