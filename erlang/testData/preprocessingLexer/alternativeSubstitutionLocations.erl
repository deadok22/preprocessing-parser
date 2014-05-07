-ifdef(MACRO).
    -define(X, 1 + 1).
-else.
    -define(X, 10 * 10).
-endif.

foo() ->
    ?X.