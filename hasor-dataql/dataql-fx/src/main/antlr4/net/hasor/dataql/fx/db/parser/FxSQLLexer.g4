// Define a grammar called Hello
lexer grammar FxSQLLexer;
ORI_BEGIN       : '${' -> pushMode(EXT_INSTR);
SAF_BEGIN       : '#{' -> pushMode(EXT_INSTR);
CHAR            : .;

/* 扩展代码块 */
mode EXT_INSTR;
CLOS_TAG        : '}' -> popMode;

/* 字符串 */
STRING          : '"' (~["\r\n] | '""' | TRANS)* '"'
                | '\'' (~['\r\n] | '\'\'' | TRANS)* '\''
                ;
fragment TRANS  : '\\' (['"\\/bfnrt] | UNICODE);
fragment UNICODE: 'u' HEX HEX HEX HEX;
fragment HEX    : [0-9a-fA-F];

OGNL_HAFSTR     : STRING | .;
