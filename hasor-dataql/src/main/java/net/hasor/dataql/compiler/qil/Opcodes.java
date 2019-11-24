/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.dataql.compiler.qil;
/**
 * QL 指令集，共计 33 条指令
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-07-03
 */
public interface Opcodes {
    // -------------------------------------------------------------------------- 构造指令
    public static final byte LDC_D  = 11;   // 将数字压入栈（例：LDC_D 12345）
    public static final byte LDC_B  = 12;   // 将布尔数据压入栈（例：INSN_B true）
    public static final byte LDC_S  = 13;   // 将字符串数据压入栈（例：LDC_S "ssssss"）
    public static final byte LDC_N  = 14;   // 将null压入栈（例：INSN_N）
    public static final byte NEW_O  = 15;   // 构造一个键值对对象并压入栈
    public static final byte NEW_A  = 16;   // 构造一个集合对象并压入栈
    // -------------------------------------------------------------------------- 存储指令
    public static final byte STORE  = 21;   // 栈顶数据存储到堆（例：STORE，2）
    public static final byte LOAD   = 22;   // 从指定深度的堆中加载n号元素到栈（例：LOAD 1 ,1 ）
    public static final byte GET    = 23;   // 获取栈顶对象元素的属性（例：GET,"xxxx"）
    public static final byte PUT    = 24;   // 将栈顶对象元素放入对象元素中（例：GET,"xxxx"）
    public static final byte PULL   = 25;   // 栈顶元素是一个集合类型，获取集合的指定索引元素。（例：PULL 123）
    public static final byte PUSH   = 26;   // 将栈顶元素压入集合（例：PUSH）
    // -------------------------------------------------------------------------- 结束指令
    public static final byte EXIT   = 31;   // 结束所有指令序列的执行并返回数据和状态
    public static final byte RETURN = 32;   // 结束当前指令序列的执行，并返回数据和状态给上一个指令序列。如果没有上一个指令序列那么结束整个查询
    public static final byte THROW  = 33;   // 结束所有指令序列的执行，并抛出异常
    // -------------------------------------------------------------------------- 运算指令
    public static final byte UO     = 41;   // 一元运算
    public static final byte DO     = 42;   // 二元运算，堆栈【第一个操作数，第二个操作数】  第一操作数 * 第二操作数
    // -------------------------------------------------------------------------- 控制指令
    public static final byte IF     = 51;   // if 条件判断，如果条件判断失败那么 GOTO 到指定位置，否则继续往下执行
    public static final byte GOTO   = 52;   // 执行跳转
    public static final byte OPT    = 53;   // 环境配置，影响执行引擎的参数选项。
    public static final byte CAST_I = 54;   // 将栈顶元素转换为迭代器，作为迭代器有三个特殊操作：data(数据)、next(移动到下一个，如果成功返回true)
    public static final byte E_PUSH = 55;   // 取出当前栈顶数据，并压入环境栈
    public static final byte E_POP  = 56;   // 丢弃环境栈顶的元素
    public static final byte E_LOAD = 57;   // 加载环境栈顶的数据到数据栈
    public static final byte LOAD_C = 58;   // 加载自定义路由
    public static final byte POP    = 59;   // 丢弃栈顶数据
    // -------------------------------------------------------------------------- 函数指令
    public static final byte CALL   = 61;   // 发起服务调用（例：CALL,2）
    public static final byte M_DEF  = 62;   // 函数定义，将栈顶元素转换为 UDF
    public static final byte M_REF  = 63;   // 引用另一处的指令序列地址，并将其作为 UDF 形态存放到栈顶
    public static final byte M_STAR = 64;   // 和M_REF指令配对，用于修整 lambda 调用的入参修整
    public static final byte M_TYP  = 65;   // 加载一个类型对象到栈顶，该类型是一个有效的 UDF。这相当于引用 java 类型UDF 函数
    public static final byte LOCAL  = 66;   // 将入参存入堆，也用于标记变量名称
    // -------------------------------------------------------------------------- 辅助指令
    public static final byte LABEL  = 71;   // 协助GOTO定位用，无实际作用
    public static final byte LINE   = 72;   // 行号，无实际作用
}