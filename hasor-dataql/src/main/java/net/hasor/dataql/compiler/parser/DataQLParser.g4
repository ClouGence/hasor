// Define a grammar called Hello
parser grammar DataQLParser;
options { tokenVocab = DataQLLexer; }
/* ----------------------------------------------------------------------------------- 语句 & 命令 */

/* 入口 */
rootInstSet     : hintInst* importInst* blockSet+ EOF;

/* 选项指令 */
hintInst        : HINT IDENTIFIER ASS primitiveValue SEM?;

/* import指令 */
importInst      : IMPORT ROU? STRING AS IDENTIFIER SEM?;

/* 语句块 */
blockSet        : OCBR ((runInst | varInst | ifInst | breakInst) (SEM)?)+ CCBR #multipleInst   // 多行语句
                | (runInst | varInst | ifInst | breakInst) (SEM)?              #singleInst     // 单行语句
                ;

/* if 语句 */
ifInst          : IF LBT expr RBT blockSet
                 (ELSE IF LBT expr RBT blockSet)*
                 (ELSE blockSet)?;
/* 退出 语句 */
breakInst       : (RETURN | THROW | EXIT) (INTEGER_NUM COMMA)? anyObject;

/* lambda函数声明 */
lambdaDef       : LBT (IDENTIFIER (COMMA IDENTIFIER)*)? RBT LAMBDA blockSet;

/* var 语句 var aaa = ... */
varInst         : VAR IDENTIFIER ASS anyObject;

/* run 语句 run ... */
runInst         : RUN anyObject;

/* ----------------------------------------------------------------------------------- 路由 */

anyObject       : extBlock | lambdaDef | primitiveValue | objectValue | listValue | funcCall | routeMapping | expr;

/* 路由 */
routeMapping    : ROU OCBR (IDENTIFIER | STRING) CCBR routeSubscript? (DOT routeNameSet)?   #specialRoute  // 特殊路由
                | ((ROU? routeNameSet) | (ROU (DOT routeNameSet)?) )                        #normalRoute   // 一般路由
                | routeMapping CONVER (objectValue | listValue)                             #convertRoute  // 路由并转换结果
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
funcCallResult  : (routeSubscript+)? DOT routeNameSet funcCallResult?       #funcCallResult_route1  // 对结果在进行路由，并处理结果
                | routeSubscript+ (DOT routeNameSet)? funcCallResult?       #funcCallResult_route2  // 对结果在进行路由，并处理结果
                | CONVER (objectValue | listValue)                          #funcCallResult_convert // 结果转换
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
ternaryExpr     : QUE expr COLON expr;

/* 可以作为表达式项的有：基本类型 or 发起函数调用 or 路由映射 */
atomExpr        : primitiveValue | funcCall | routeMapping;

/* 外部语句块块 */
extBlock        : AT IDENTIFIER LBT extParams? RBT '<%' EXT_ANY '%>';
extParams       : IDENTIFIER (COMMA IDENTIFIER)*;
