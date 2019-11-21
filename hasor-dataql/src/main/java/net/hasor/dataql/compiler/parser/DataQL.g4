// Define a grammar called Hello
grammar DataQL;

/* skip spaces */
WS      : [ \t\n\r\f]+          -> skip ; // skip spaces, (空格\水平制表符\换行\回车\换页)
COMMENT1: '//' (~[\n\r])* EOL?  -> skip ;
COMMENT2: '/*' .*? '*/'         -> skip ;
EOL     : [\n\r\f];

/* key words */
IF      : 'if';
ELSE    : 'else';
RETURN  : 'return';
THROW   : 'throw';
EXIT    : 'exit';
VAR     : 'var';
OPTION  : 'option';
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
IDENTIFIER      : [_a-zA-Z] [_0-9a-zA-Z]*;

/* ----------------------------------------------------------------------------------- 语句 & 命令 */
/* 入口 */
rootInstSet     : optionInst* importInst* blockSet+ EOF ;

/* 选项指令 */
optionInst      : OPTION IDENTIFIER ASS primitiveValue ';'?;

/* import指令 */
importInst      : IMPORT ROU? STRING AS IDENTIFIER ';'?;

/* 语句块 */
blockSet        : OCBR ((varInst | ifInst | breakInst) (';')?)+ CCBR #multipleInst   // 多行语句
                | (varInst | ifInst | breakInst) (';')?              #singleInst     // 单行语句
                ;

/* if 语句 */
ifInst          : IF LBT expr RBT blockSet
                 (ELSE IF LBT expr RBT blockSet)*
                 (ELSE blockSet)?;
/* 退出 语句 */
breakInst       : (RETURN | THROW | EXIT) (INTEGER_NUM COMMA)? anyObject;

/* lambda函数声明 */
lambdaDef       : LBT (IDENTIFIER (COMMA IDENTIFIER)*)? RBT '->' blockSet ;

/* var 语句 var aaa = ... */
varInst         : VAR IDENTIFIER ASS anyObject;

/* ----------------------------------------------------------------------------------- 路由 */

anyObject       : lambdaDef | primitiveValue | objectValue | listValue | funcCall | routeMapping | expr;

/* 路由 */
routeMapping    : ROU ((OCBR (IDENTIFIER | STRING) CCBR) | routeNameSet)    #specialRoute  // 特殊路由
                | routeNameSet                                              #normalRoute   // 一般路由
                | routeMapping '=>' (objectValue | listValue)               #convertRoute  // 路由并转换结果
                ;

/* 一般路由的规则 */
routeNameSet    : routeName (DOT routeName)*
                ;
/* 路由名 */
routeName       : IDENTIFIER routeSubscript*;

/* 下标 */
routeSubscript  : LSBT ( STRING | INTEGER_NUM ) RSBT;


/* 函数调用(不含转换,可用于表达式) */
funcCall        : routeMapping LBT ( anyObject (COMMA anyObject)* )? RBT funcCallResult?;

/** 函数调用返回值处理 */
funcCallResult  : (routeSubscript+)? DOT routeNameSet funcCallResult?       #funcCallResult_route   // 对结果在进行路由，并处理结果
                | '=>' (objectValue | listValue)                            #funcCallResult_convert // 结果转换
                | LBT ( anyObject (COMMA anyObject)* )? RBT funcCallResult? #funcCallResult_call    // 调用函数返回的函数，并处理结果
                ;

/* ----------------------------------------------------------------------------------- 带有结构的 */

/* 对象结构 */
objectValue     : OCBR objectKeyValue? ( COMMA objectKeyValue)* CCBR;
objectKeyValue  : STRING ( COLON anyObject)?;

/* 列表结构 */
listValue       : LSBT anyObject? (COMMA anyObject)* RSBT;

/* 基本类型 */
primitiveValue  : STRING                                                    #stringValue    // 字符串
                | NULL                                                      #nullValue      // 空值
                | (TRUE | FALSE)                                            #booleanValue   // boolean 类型
                | (DECIMAL_NUM | INTEGER_NUM | HEX_NUM | OCT_NUM | BIT_NUM) #numberValue    // 数字类型
                ;

/* ----------------------------------------------------------------------------------- 表达式 */

expr            : (PLUS | MINUS | NOT) expr                     #unaryExpr      // 一元运算
                | LBT expr RBT ( dyadicExpr | ternaryExpr )?    #privilegeExpr  // 优先级(后面可以有多元运算)
                | atomExpr ( dyadicExpr | ternaryExpr )?        #multipleExpr   // 基本算项 or 多元运算
                ;

/* 二元运算 */
dyadicExpr      : (PLUS | MINUS | MUL | DIV | DIV2 | MOD | LBT | RBT | AND | OR | NOT | XOR | LSHIFT | RSHIFT | RSHIFT2 | GT | GE | LT | LE | EQ | NE | SC_OR | SC_AND) expr;

/* 三元运算 */
ternaryExpr     : '?' expr COLON expr;

/* 可以作为表达式项的有：基本类型 or 发起函数调用 or 路由映射 */
atomExpr        : primitiveValue | funcCall | routeMapping;
