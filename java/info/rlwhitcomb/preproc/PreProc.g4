/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Roger L. Whitcomb.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 *	Grammar for a preprocessor similar to the C/C++ macro processor
 *
 *  History:
 *      20-Jan-2021 (rlwhitcomb)
 *          First version (not quite complete yet).
 */

grammar PreProc;

/* Parser rules */

directive
        : DIR stmt NL ;

stmt
        : DEFINE ID expr                  # defineStmt
        | UNDEF ID                        # undefStmt
        | IFDEF ID                        # ifdefStmt
        | IFNDEF ID                       # ifndefStmt
        | IFNUM expr                      # ifnumStmt
        | IFSTR expr                      # ifstrStmt
        | IFISTR expr                     # ifistrStmt
        | IF expr                         # ifStmt
        | ELSE                            # elseStmt
        | ( ELIF | ELSEIF ) expr          # elseifStmt
        | ENDIF                           # endifStmt
        | INCLUDE ( STRING | BSTRING )    # includeStmt
        | ERROR TEXT                      # errorStmt
        ;
        
expr
        : ID                              # idExpr
        | STRING                          # stringExpr
        | '(' expr ')'                    # parenExpr
        | expr ( '*' | '/' | '%' ) expr   # multExpr
        | expr ( '+' | '-' ) expr         # addExpr
        |<assoc=right> '!' expr           # notExpr
        | DEFINED '(' ID ')'              # definedExpr
        | expr ( LT | LE | GT | GE ) expr # compareExpr
        | expr ( EQ | NE ) expr           # equalExpr
        | expr AND expr                   # andExpr
        | expr OR expr                    # orExpr
        ;


/* Lexer rules */

DEFINED : 'defined' | 'DEFINED' ;

DEFINE  : 'define'  | 'DEFINE'  ;

UNDEF   : 'undef'   | 'UNDEF'   ;

IFDEF   : 'ifdef'   | 'IFDEF'   ;

IFNDEF  : 'ifndef'  | 'IFNDEF'  ;

IFNUM   : 'ifnum'   | 'IFNUM'   ;

IFSTR   : 'ifstr'   | 'IFSTR'   ;

IFISTR  : 'ifistr'  | 'IFISTR'  ; 

IF      : 'if'      | 'IF'      ;

ELSE    : 'else'    | 'ELSE'    ;

ELIF    : 'elif'    | 'ELIF'    ;

ELSEIF  : 'elseif'  | 'ELSEIF'  ;

ENDIF   : 'endif'   | 'ENDIF'   ;

INCLUDE : 'include' | 'INCLUDE' ;

ERROR   : 'error'   | 'ERROR'   ;


EQ     : '==' | 'eq'  | 'EQ'  ;

NE     : '!=' | 'ne'  | 'NE'  ;

LE     : '<=' | 'le'  | 'LE'  ;

LT     : '<'  | 'lt'  | 'LT'  ;

GE     : '>=' | 'ge'  | 'GE'  ;

GT     : '>'  | 'gt'  | 'GT'  ;

AND    : '&&' | 'and' | 'AND' ;

OR     : '||' | 'or'  | 'OR'  ;

DIR    : '$' ;


STRING
       : '"'  (ESC  | SAFECODEPOINT)*  '"'
       | '\'' (ESC2 | SAFECODEPOINT2)* '\''
       ;

BSTRING
       : '<' ~ [>\u0000-\u001F] +  '>'
       | '[' ~ [\]\u0000-\u001F] + ']'
       | '{' ~ [}\u0000-\u001F] +  '}'
       ;

PREDEF : '__' [a-zA-Z_] + '__' ;

ID     : [a-zA-Z_] [a-zA-Z_0-9$] * ;

TEXT   : ~ [\u0000-\u001F] + ;


fragment ESC
       : '\\' (["\\/bfnrt] | UNICODE)
       ;

fragment ESC2
       : '\\' (['\\/bfnrt] | UNICODE)
       ;

fragment UNICODE
       : 'u' HEX HEX HEX HEX
       ;

fragment HEX
       : [0-9a-fA-F]
       ;

fragment SAFECODEPOINT
       : ~ ["\\\u0000-\u001F]
       ;

fragment SAFECODEPOINT2
       : ~ ['\\\u0000-\u001F]
       ;


WS     : [ \t] + -> skip ;

NL     : '\r' ? '\n' ;

