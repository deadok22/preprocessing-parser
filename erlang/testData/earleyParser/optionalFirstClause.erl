-ifdef(FOO_NOT_OK).
    -define(FOO, foo() -> not_ok;).
-endif.

?FOO
foo() -> ok.