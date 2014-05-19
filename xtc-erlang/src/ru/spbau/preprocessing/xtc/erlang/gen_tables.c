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

#include <stdio.h>

// Empty definitions to avoid errors from c.tab.c.
int yylex() {}
int yyerror() {}
int l() {}

#include "erlang.tab.c"

int main() {
  int i;
  
  printf("/*\n");
  printf(" * xtc - The eXTensible Compiler\n");
  printf(" * Copyright (C) 2009-2012 New York University\n");
  printf(" *\n");
  printf(" * This program is free software; you can redistribute it and/or\n");
  printf(" * modify it under the terms of the GNU General Public License\n");
  printf(" * version 2 as published by the Free Software Foundation.\n");
  printf(" *\n");
  printf(" * This program is distributed in the hope that it will be useful,\n");
  printf(" * but WITHOUT ANY WARRANTY; without even the implied warranty of\n");
  printf(" * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n");
  printf(" * GNU General Public License for more details.\n");
  printf(" *\n");
  printf(" * You should have received a copy of the GNU General Public License\n");
  printf(" * along with this program; if not, write to the Free Software\n");
  printf(" * Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,\n");
  printf(" * USA.\n");
  printf(" */\n");
  printf("package ru.spbau.preprocessing.xtc.erlang;\n");
  printf("\n");

  printf("public class ErlangForkMergeParserTables {\n");
# define printint(macro) printf("  public static final int " #macro " = %d;\n", macro);
  printint(YYFINAL);
  printint(YYLAST);
  printint(YYNTOKENS);
  printint(YYNNTS);
  printint(YYNRULES);
  printint(YYNSTATES);
  printint(YYUNDEFTOK);
  printint(YYMAXUTOK);
  printint(YYEOF);
  printint(YYPACT_NINF);
  printint(YYTABLE_NINF);

  printf("\n");
  
  print_uint8("yytranslate", yytranslate, YYMAXUTOK);
  print_uint16("yyprhs", yyprhs, YYNRULES);
  print_int16("yyrhs", yyrhs, yyprhs[YYNRULES] + yyr2[YYNRULES]);
  print_char("yytname", yytname, YYNTOKENS + YYNNTS - 1);
  print_uint16("yytoknum", yytoknum, YYNTOKENS - 1);
  print_uint16("yyr1", yyr1, YYNRULES);
  print_uint8("yyr2", yyr2, YYNRULES);
  print_uint16("yydefact", yydefact, YYNSTATES - 1);
  print_int16("yydefgoto", yydefgoto, YYNNTS - 1);
  print_int16("yypact", yypact, YYNSTATES - 1);
  print_int16("yypgoto", yypgoto, YYNNTS - 1);
  print_int16("yytable", yytable, YYLAST);
  print_int16("yycheck", yycheck, YYLAST);
  print_uint16("yystos", yystos, YYNSTATES - 1);

  printf("}\n");

  return 0;
}

#define TABLEPRINTER(fname, ctype, pftype, jtype, limit) \
int print_ ## fname(char *name, ctype table[], int max) { \
  int i; \
   \
  /*printf("  public static final " #jtype "[] %s = {", name);*/ \
  printf("  public static class %s {\n", name); \
  printf("    public static final " #jtype "[] table = {"); \
  for (i = 0; i <= max; i++) { \
    if (i > 0) printf(","); \
    if ((i % limit) == 0) printf("\n    "); \
    else printf(" "); \
    printf(pftype, table[i]); \
  } \
  printf("\n    };\n"); \
  printf("  }\n\n"); \
}

TABLEPRINTER(uint8, yytype_uint8, "%d", int, 10)

TABLEPRINTER(uint16, yytype_uint16, "%d", int, 10)

TABLEPRINTER(int16, yytype_int16, "%d", int, 10)

TABLEPRINTER(char, char *const, "\"%s\"", String, 1)

