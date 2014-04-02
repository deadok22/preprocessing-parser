-undef(UNDEFINED_MACRO).
-ifdef(UNDEFINED_MACRO).
foo() -> ok.
-else.
bar() -> ok.
-endif.