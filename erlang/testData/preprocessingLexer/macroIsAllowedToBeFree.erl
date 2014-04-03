-ifdef(A).
-define(X, ).
-endif.

%% at this point there should be two entries in macro definitions table
%% one corresponds to -define on the second line
%% and another one is a free macro definition in case A is not undefined.
%% Thus, the a macro can be both defined and free at the following -ifdef.

-ifdef(X).
    foo() -> ok.
-else.
    foo() -> ok.
-endif.