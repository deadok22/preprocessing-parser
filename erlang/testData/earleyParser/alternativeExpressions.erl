-ifdef(X).
-define(EXPR, * 19).
-else.
-define(EXPR, + 18).
-endif.

foo() ->
    (11 ?EXPR) - 94.