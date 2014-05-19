/*
 * xtc - The eXTensible Compiler
 * Copyright (C) 2009-2012 New York University
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
 * USA.
 */
package ru.spbau.preprocessing.xtc.erlang;

public class ErlangForkMergeParserTables {
  public static final int YYFINAL = 10;
  public static final int YYLAST = 15;
  public static final int YYNTOKENS = 74;
  public static final int YYNNTS = 11;
  public static final int YYNRULES = 15;
  public static final int YYNSTATES = 25;
  public static final int YYUNDEFTOK = 2;
  public static final int YYMAXUTOK = 328;
  public static final int YYEOF = 0;
  public static final int YYPACT_NINF = -62;
  public static final int YYTABLE_NINF = -1;

  public static class yytranslate {
    public static final int[] table = {
    0, 2, 2, 2, 2, 2, 2, 2, 2, 2,
    2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
    2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
    2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
    2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
    2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
    2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
    2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
    2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
    2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
    2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
    2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
    2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
    2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
    2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
    2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
    2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
    2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
    2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
    2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
    2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
    2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
    2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
    2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
    2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
    2, 2, 2, 2, 2, 2, 1, 2, 3, 4,
    5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
    15, 16, 17, 18, 19, 20, 21, 22, 23, 24,
    25, 26, 27, 28, 29, 30, 31, 32, 33, 34,
    35, 36, 37, 38, 39, 40, 41, 42, 43, 44,
    45, 46, 47, 48, 49, 50, 51, 52, 53, 54,
    55, 56, 57, 58, 59, 60, 61, 62, 63, 64,
    65, 66, 67, 68, 69, 70, 71, 72, 73
    };
  }

  public static class yyprhs {
    public static final int[] table = {
    0, 1539, 2824, 4365, 5907, 7193, 8479, 10276, 75, 19455,
    65356, 65356, 7501, 20223, 20479, 20036
    };
  }

  public static class yyrhs {
    public static final int[] table = {
    75, 19455, -180, -180, 7501, 20223, 20479, 20036, 20479, 2303,
    20816, 21503, 1791, -172, 21013, 2303, 16127, -193, 6482, -174,
    -174, 0, 0, 0, 19456, 19788, 21328, 22358, 23898, 25440,
    26468, 27498, 25892, 25710, 25856, 29298, 29295, 9216, 28277, 25956,
    26982, 25966
    };
  }

  public static class yytname {
    public static final String[] table = {
    "$end",
    "error",
    "$undefined",
    "AFTER",
    "AND",
    "ANDALSO",
    "ARROW",
    "ASSOC",
    "ATOM",
    "BAND",
    "BEGIN",
    "BIN_END",
    "BIN_START",
    "BNOT",
    "BOR",
    "BRACKET_LEFT",
    "BRACKET_RIGHT",
    "BSL",
    "BSR",
    "BXOR",
    "CASE",
    "CATCH",
    "CHAR",
    "COLON",
    "COLON_COLON",
    "COMMA",
    "CURLY_LEFT",
    "CURLY_RIGHT",
    "DIV",
    "DOT",
    "DOT_DOT",
    "DOT_DOT_DOT",
    "END",
    "FLOAT",
    "FUN",
    "IF",
    "INTEGER",
    "MATCH",
    "NOT",
    "OF",
    "OP_AR_DIV",
    "OP_AR_MUL",
    "OP_DIV_EQ",
    "OP_EQ",
    "OP_EQ_COL_EQ",
    "OP_EQ_DIV_EQ",
    "OP_EQ_EQ",
    "OP_EQ_LT",
    "OP_EXL",
    "OP_GT",
    "OP_GT_EQ",
    "OP_LT",
    "OP_LT_EQ",
    "OP_LT_MINUS",
    "OP_MINUS",
    "OP_MINUS_MINUS",
    "OP_OR",
    "OP_PLUS",
    "OP_PLUS_PLUS",
    "OR",
    "ORELSE",
    "OR_OR",
    "PAR_LEFT",
    "PAR_RIGHT",
    "QMARK",
    "RADIX",
    "RECEIVE",
    "REM",
    "SEMI",
    "STRING",
    "TRY",
    "VAR",
    "WHEN",
    "XOR",
    "$accept",
    "Forms",
    "Form",
    "Function",
    "FunctionClauses",
    "FunctionClause",
    "ClauseArgs",
    "ClauseBody",
    "Expr",
    "ArgumentList",
    "Exprs"
    };
  }

  public static class yytoknum {
    public static final int[] table = {
    0, 256, 257, 258, 259, 260, 261, 262, 263, 264,
    265, 266, 267, 268, 269, 270, 271, 272, 273, 274,
    275, 276, 277, 278, 279, 280, 281, 282, 283, 284,
    285, 286, 287, 288, 289, 290, 291, 292, 293, 294,
    295, 296, 297, 298, 299, 300, 301, 302, 303, 304,
    305, 306, 307, 308, 309, 310, 311, 312, 313, 314,
    315, 316, 317, 318, 319, 320, 321, 322, 323, 324,
    325, 326, 327, 328
    };
  }

  public static class yyr1 {
    public static final int[] table = {
    18944, 19275, 19788, 20046, 20559, 21073, 21330, 21588, 512, 258,
    258, 259, 259, 514, 513, 259
    };
  }

  public static class yyr2 {
    public static final int[] table = {
    0, 2, 2, 1, 2, 1, 3, 1, 3, 1,
    2, 2, 1, 2, 3, 1
    };
  }

  public static class yydefact {
    public static final int[] table = {
    0, 768, 1280, 7, 2304, 513, 4, 13, 1544, 12,
    2575, 11, 65294, 770, 1284, 2054, 5136, 5385, 0, 0,
    0, 0, 0, 0, 50939
    };
  }

  public static class yydefgoto {
    public static final int[] table = {
    767, 1027, 1541, 4104, 2324, 21, 0, 0, 0, 0,
    0
    };
  }

  public static class yypact {
    public static final int[] table = {
    -14597, -15871, -15640, -13885, -15868, -15678, -1086, -1854, -15678, -1854,
    -15630, -1854, -15678, 2754, 706, -15678, -4158, -15678, 0, 0,
    0, 0, 0, 0, 2578
    };
  }

  public static class yypgoto {
    public static final int[] table = {
    -15678, -15862, -15870, -15678, -15633, 194, 0, 0, 0, 0,
    0
    };
  }

  public static class yytable {
    public static final int[] table = {
    2578, 278, 3079, 3352, 270, 5903, 4875, 4352, 8, 2067,
    7486, 17431, 2111, 6406, 5378, 3583
    };
  }

  public static class yycheck {
    public static final int[] table = {
    8, 2067, 7486, 17431, 2111, 6406, 5378, 3583, 2048, 19531,
    20045, 15951, 21328, 19456, 17437, 1599
    };
  }

  public static class yystos {
    public static final int[] table = {
    2048, 19531, 20045, 15951, 21328, 19456, 17437, 1599, 20049, 5384,
    21586, 6482, 29778, 27503, 28261, 9504, 8307, 40, 29806, 29285,
    8301, 29477, 10272, 21248, 24948
    };
  }

}
