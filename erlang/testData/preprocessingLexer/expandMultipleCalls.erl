-define(M, foo).

-ifdef(X).
    -define(M(), bar).
-endif.


foobar() ->
% this should transform into
% bar() if X is defined
% or to foo()() if X is not defined
    ?M()().

foo() ->
    fun () -> ok end.

bar() ->
    ok.