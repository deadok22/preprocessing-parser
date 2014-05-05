-ifdef(FOO_0).

foo() ->
    ok.

-else.

foo(_) ->
    ok.

-endif.
