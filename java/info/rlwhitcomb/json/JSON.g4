/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Roger L. Whitcomb.
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
 *	Grammar for JSON to be used with complete JSON API.
 *
 *  History:
 *      04-Jan-2022 (rlwhitcomb)
 *	    First version (excerpted and revised from Calc.g4)
 *	17-Feb-2022 (rlwhitcomb)
 *	    #196: Tweak during implementation.
 */

grammar JSON;

json
   : entity EOF
   ;

entity
   : obj
   | arr
   | value
   ;

obj
   : '{' pairList '}'
   | '{' '}'
   ;

pairList
   : pair ( ',' pair ) *
   ;

pair
   : id ':' entity
   | STRING ':' entity
   ;

arr
   : '[' entityList ']'
   | '[' ']'
   ;

entityList
   : entity ( ',' entity ) *
   ;

value
   : STRING                      # stringValue
   | NUMBER                      # numberValue
   | NULL_CONST                  # nullValue
   | BOOL_CONST                  # booleanValue
   | BIN_CONST                   # binaryValue
   | OCT_CONST                   # octalValue
   | HEX_CONST                   # hexValue
   ;

id
   : ID
   ;

/* Lexer rules start here */

NULL_CONST
   : 'null'
   ;

BOOL_CONST
   : 'true'
   | 'false'
   ;

ID : NAME_START_CHAR NAME_CHAR *
   ;


STRING
   : '"'      (ESC1 | SAFECODEPOINT1)* '"'
   | '\''     (ESC2 | SAFECODEPOINT2)* '\''
   ;

fragment ESC1
   : '\\' (["\\/bfnrt] | UNICODE)
   ;

fragment ESC2
   : '\\' (['\\/bfnrt] | UNICODE)
   ;

fragment UNICODE
   : 'u' HEX HEX HEX HEX
   | 'u' '{' HEX + '}'
   | 'o' OCT OCT OCT
   | 'o' '{' OCT + '}'
   | 'B' BIN BIN BIN BIN BIN BIN BIN BIN
   | 'B' '{' BIN + '}'
   ;

fragment HEX
   : [0-9a-fA-F]
   ;

fragment OCT
   : [0-7]
   ;

fragment BIN
   : [01]
   ;

fragment SAFECODEPOINT1
   : ~ ["\\\u0000-\u001F]
   ;

fragment SAFECODEPOINT2
   : ~ ['\\\u0000-\u001F]
   ;

fragment NAME_START_CHAR
   : 'A'..'Z'
   | 'a'..'z'
   | '_'
   | '\u00C0'..'\u00D6'
   | '\u00D8'..'\u00F6'
   | '\u00F8'..'\u02FF'
   | '\u0370'..'\u037D'
   | '\u037F'..'\u1FFF'
   | '\u200C'..'\u200D'
   | '\u2071'..'\u2073'
   | '\u207A'..'\u207F'
   | '\u208A'..'\u218F'
   | '\u2C00'..'\u2FEF'
   | '\u3001'..'\uD7FF'
   | '\uF900'..'\uFDCF'
   | '\uFDF0'..'\uFF0F'
   | '\uFF1A'..'\uFFFD'
   ;

fragment NAME_CHAR
   : NAME_START_CHAR
   | '0'..'9'
   | '\u00B7'
   | '\u0300'..'\u036F'
   | '\u203F'..'\u2040'
   ;

NUMBER
   : INT ('.' [0-9] +)? EXP?
   ;

BIN_CONST
   : '0' B [01]+
   ;

OCT_CONST
   : '0' [0-7]+
   ;

HEX_CONST
   : '0' X [0-9a-fA-F] +
   ;

// no leading zeros
fragment INT
   : '0'
   | [1-9] [0-9]*
   ;

// \- since - means "range" inside [...]
fragment EXP
   : [Ee] [+\-]? INT
   ;

fragment A : [aA] ;
fragment B : [bB] ;
fragment C : [cC] ;
fragment D : [dD] ;
fragment E : [eE] ;
fragment F : [fF] ;
fragment G : [gG] ;
fragment H : [hH] ;
fragment I : [iI] ;
fragment J : [jJ] ;
fragment K : [kK] ;
fragment L : [lL] ;
fragment M : [mM] ;
fragment N : [nN] ;
fragment O : [oO] ;
fragment P : [pP] ;
fragment Q : [qQ] ;
fragment R : [rR] ;
fragment S : [sS] ;
fragment T : [tT] ;
fragment U : [uU] ;
fragment V : [vV] ;
fragment W : [wW] ;
fragment X : [xX] ;
fragment Y : [yY] ;
fragment Z : [zZ] ;

fragment NL
   : '\r'? '\n'
   ;


COMMENT
   : '/*' .*? '*/' -> skip
   ;

LINE_COMMENT
   : ( '#' | '//' ) .*? NL -> skip
   ;

LINE_ESCAPE
   : '\\' NL -> skip
   ;


WS
   : ( [ \t] + | NL ) -> skip
   ;

