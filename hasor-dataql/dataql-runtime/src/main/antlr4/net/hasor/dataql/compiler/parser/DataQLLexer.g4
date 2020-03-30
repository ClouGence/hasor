// Define a grammar called Hello
lexer grammar DataQLLexer;

/* skip spaces */
WS      : [ \t\n\r\f]+          -> skip ; // skip spaces, (空格\水平制表符\换行\回车\换页)
COMMENT1: '//' (~[\n\r])* EOL?  -> skip ;
COMMENT2: '/*' .*? '*/';
EOL     : [\n\r\f];

//
AT      : '@@';
OPEN_TAG: '<%' -> pushMode(EXT_INSTR);

/* key words */
IF      : 'if';
ELSE    : 'else';
RETURN  : 'return';
THROW   : 'throw';
EXIT    : 'exit';
VAR     : 'var';
RUN     : 'run';
HINT    : 'hint';
IMPORT  : 'import';
TRUE    : 'true';
FALSE   : 'false';
NULL    : 'null';
AS      : 'as';

/* arithmetic operators 算数运算 */
PLUS    : '+';      // 加法
MINUS   : '-';      // 减法
MUL     : '*';      // 乘法
DIV     : '/';      // 除法
DIV2    : '\\';     // 整除
MOD     : '%';      // 取摸
LBT     : '(';      // 优先级
RBT     : ')';      // 优先级
AND     : '&';      // 按位于运算
OR      : '|';      // 按位或运算
NOT     : '!';      // 按位取反
XOR     : '^';      // 异或
LSHIFT  : '<<';     // 左位移
RSHIFT  : '>>';     // 有符号右位移
RSHIFT2 : '>>>';    // 无符号右位移

/* logic operators 逻辑运算 */
GT      : '>';      // 大于
GE      : '>=';     // 大于等于
LT      : '<';      // 小于
LE      : '<=';     // 小于等于
EQ      : '==';     // 等于
NE      : '!=';     // 不等于
SC_OR   : '||';     // 逻辑或
SC_AND  : '&&';     // 逻辑与

/* assist words 连接符在某些特定场景下使用 */
COMMA   : ',';      // 参数\分割项
COLON   : ':';      // Object 类型中使用
ASS     : '=';      // 赋值
DOT     : '.';      //
LSBT    : '[';      // 数组 or 下标
RSBT    : ']';      // 数组 or 下标
OCBR    : '{';      // 表示为一个对象
CCBR    : '}';      // 表示为一个对象
ROU     : [@#$];    // 路由限定符
QUE     : '?';      // ?
SEM     : ';';      // ;
CONVER  : '=>';     // =>
LAMBDA  : '->';     // ->

/* 字符串 */
STRING          : '"' (~["\r\n] | '""' | TRANS)* '"'
                | '\'' (~['\r\n] | '\'\'' | TRANS)* '\''
                ;
fragment TRANS  : '\\' (['"\\/bfnrt] | UNICODE);
fragment UNICODE: 'u' HEX HEX HEX HEX;
fragment HEX    : [0-9a-fA-F];

/* 数字 */
HEX_NUM         : '0' [xX] [0-9a-fA-F]+;                // 十六进制：0x12345
OCT_NUM         : '0' [oO] [0-7]+;                      // 八 进 制：0o1234567
BIT_NUM         : '0' [bB] [01]+;                       // 二 进 制：0b01010101100
INTEGER_NUM     : '-'? [0-9]+;                          // 十进制数：-0000234 or 123
DECIMAL_NUM     : '-'? (([0-9]* '.' [0-9]+) | [1-9]+)   // 浮点数
                  ([eE] [+-]? [1-9][0-9]*)?;            // 科学计数法

/* 标识符 */
IDENTIFIER      : ([_a-zA-Z] [_0-9a-zA-Z]*) | ID_EXT;
fragment ID_EXT : '`' (~[`\r\n] | '``' | TRANS)* '`';

/* 扩展代码块 */
mode EXT_INSTR;
CLOS_TAG    : '%>' -> popMode;
CHAR        : .;
