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
blockSet        : OCBR hintInst* ((runInst | varInst | ifInst | breakInst) (SEM)?)* CCBR #multipleInst  // 多行语句
                | (runInst | varInst | ifInst | breakInst) (SEM)?                        #singleInst    // 单行语句
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

anyObject       : extBlock | lambdaDef | primitiveValue | objectValue | listValue | funcCall | routeMapping | routeConver | expr;

/* 路由 */
routeMapping    : ROU OCBR (IDENTIFIER | STRING) CCBR routeSubscript? (DOT routeNameSet)?   #paramRoute    // 程序传参
                | ROU routeSubscript+ (DOT routeNameSet)?                                   #subExprRoute  // 表达式（访问符 -> 先下标，在元素）
                | ROU DOT? routeNameSet                                                     #nameExprRoute // 表达式（访问符 -> 子元素）
                | ((ROU? routeNameSet) | (ROU routeNameSet?))                               #exprRoute     // 表达式
                ;

/* 转换 */
routeConver     : routeMapping CONVER (objectValue | listValue)                             #exprFmtRoute  // 转换结果
                ;

/* 一般路由的规则 */
routeNameSet    : routeName (DOT routeName)*
                ;
/* 路由名 */
routeName       : IDENTIFIER routeSubscript*;

/* 下标 */
routeSubscript  : LSBT ( STRING | INTEGER_NUM | expr ) RSBT;

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

/* 表达式 */
expr            : (primitiveValue | funcCall | routeMapping)                    #atomExpr       // 可以作为表达式项的有：基本类型 or 发起函数调用 or 路由映射
                | LBT expr RBT                                                  #privilegeExpr  // 优先级
                | prefix=(PLUS | MINUS | NOT) expr                              #unaryExpr      // 一元运算
                | expr bop=(MUL | DIV | DIV2 | MOD) expr                        #dyadicExpr_A   // 二元运算优先级 1st
                | expr bop=(PLUS | MINUS) expr                                  #dyadicExpr_B   // 二元运算优先级 2st
                | expr bop=(AND | OR | XOR | LSHIFT | RSHIFT | RSHIFT2) expr    #dyadicExpr_C   // 二元运算优先级 3st
                | expr bop=(GT | GE | NE | EQ | LE | LT) expr                   #dyadicExpr_D   // 二元运算优先级 4st
                | expr bop=(SC_OR | SC_AND) expr                                #dyadicExpr_E   // 二元运算优先级 5st
                | <assoc=right> expr QUE expr COLON expr                        #ternaryExpr    // 三元运算
                ;

/* 外部语句块块 */
extBlock        : AT IDENTIFIER (LSBT RSBT)?  LBT extParams? RBT '<%' CHAR* '%>';
extParams       : IDENTIFIER (COMMA IDENTIFIER)*;