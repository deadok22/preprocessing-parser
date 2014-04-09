-define(MACRO(X, Y), "HELLO").

foo() ->
    io:format(?MACRO(foo(), <<0:8>>)).