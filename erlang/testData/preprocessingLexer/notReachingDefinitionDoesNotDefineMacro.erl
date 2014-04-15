-ifdef(X).
    -define(X, ok).
-else.
    -ifdef(X).
        foo() -> ok.
    -endif.
-endif.