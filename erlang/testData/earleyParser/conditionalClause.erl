-ifdef(FOO_NOT_OK).
    -define(FOO, ok; foo(not_ok) -> not_ok).
-else.
    -define(FOO, ok).
-endif.

foo(ok) ->
    ?FOO.