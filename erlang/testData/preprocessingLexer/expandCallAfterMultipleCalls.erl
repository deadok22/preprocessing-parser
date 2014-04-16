-define(M, foo).

-ifdef(X).
    -define(M(Y), bar).
-endif.

-define(ARG, ok).

foobar() ->
% this should transform into
% bar() if X is defined
% or to foo(ok)() if X is not defined
    ?M(?ARG)().

foo(X) ->
    fun () -> X end.

bar() ->
    ok.