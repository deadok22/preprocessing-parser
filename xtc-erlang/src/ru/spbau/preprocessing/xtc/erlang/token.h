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


/* This header provides tool-independent language tag specification. */
/* Define the LEXER configuration variable to produce a JFlex lexer */
/* specification.  Define the TAG configuration variable to produce an */
/* implementation of the Syntax.LanguageTag interface, used to provide */
/* information to the SuperC system. */


#if defined LEXER && defined TAG
# error May only specify LEXER or TAG output, not both.
#elif ! defined LEXER && ! defined TAG
# error Must specify either LEXER or TAG
#endif

#ifdef LEXER

/**
 * Create a C Language Syntax object.  The "text" parameter must have
 * quotes, since it will be used as a Java string constant.
 */
# define LANGUAGE(token, text)                                        \
  text {                                                              \
    Language<ErlangTag> syntax = new Language<ErlangTag>(ErlangTag.token);           \
                                                                      \
    syntax.setLocation(new Location(fileName, yyline+1, yycolumn+1)); \
                                                                      \
    return syntax;                                                    \
  }

/**
 * Create a C Text Syntax object.
 */
# define TEXT(token, regex, hasName)                                \
  regex {                                                           \
    Text<ErlangTag> syntax = new Text<ErlangTag>(ErlangTag.token, yytext());       \
                                                                    \
    syntax.setLocation(new Location(fileName, yyline+1, yycolumn)); \
                                                                    \
    return syntax;                                                  \
  }

/**
 * Create a preprocessor language token.
 */
# define PREPROCESSOR(token, pptag, text) LANGUAGE(token, text)

#elif defined TAG
# define LANGUAGE(token, text) token(getID(#token), text),
# define TEXT(token, regex, hasName) token(getID(#token), null, hasName),
# define PREPROCESSOR(token, pptag, text) \
  token(getID(#token), text, PreprocessorTag.pptag),
#endif
