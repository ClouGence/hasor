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
package net.hasor.dataql.domain.compiler;
/**
 * QL 指令集，共计 30 条指令
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-07-03
 */
public interface Opcodes {
    //
    // -------------------------------------------------------------- 构造指令
    public static final byte NO     = 11; // new Object
    public static final byte NA     = 12; // new Array
    //
    // -------------------------------------------------------------- 数据指令
    public static final byte LDC_D  = 21; // 数字（例：INSN_D 1234）
    public static final byte LDC_B  = 22; // 布尔（例：INSN_B true）
    public static final byte LDC_S  = 23; // 字符串
    public static final byte LDC_N  = 24; // Null（例：INSN_N）
    //
    // -------------------------------------------------------------- 存储指令
    public static final byte LOAD   = 31; // 从堆中加载数据到栈（例：LOAD，1）
    public static final byte STORE  = 32; // 将栈中的数据存储到堆（例：STORE，2）
    //
    // -------------------------------------------------------------- 结果指令
    public static final byte ASM    = 41; // 结果作为对象（例：ASM，"type"）
    public static final byte ASO    = 42; // 结果作为原始对象（例：ASO）
    public static final byte ASA    = 43; // 结果作为数组（例：ASA，"type"）
    public static final byte ASE    = 44; // 结果结束（例：ASA）
    //
    // -------------------------------------------------------------- 操作指令
    public static final byte PUT    = 51; // 加到对象结果集中（例：PUT,"xxxx"）
    public static final byte PUSH   = 52; // 加到 Array 结果集中（例：PUSH）
    public static final byte ROU    = 53; // 寻值（例：ROU,"xxxxx"）
    public static final byte UO     = 54; // 一元运算
    public static final byte DO     = 55; // 二元运算
    //
    // -------------------------------------------------------------- 调用指令
    public static final byte CALL   = 61; // 发起服务调用（例：CALL,"xxxxx",2）
    public static final byte LCALL  = 62; // 调用内置函数
    //
    // -------------------------------------------------------------- 函数指令
    public static final byte METHOD = 71; // 函数定义
    public static final byte M_REF  = 72; // 函数引用
    //
    // -------------------------------------------------------------- 控制指令
    public static final byte IF     = 81; // if（条件判断成功，执行下一条指令。否则执行 GOTO跳转。）
    public static final byte GOTO   = 82; // 执行跳转
    public static final byte END    = 83; // 结束指令序列并返回值（消耗：1个元素，产出：0个元素）
    public static final byte EXIT   = 84; // 结束所有指令序列的执行并返回结果（消耗：2个元素，产出：0个元素）
    public static final byte ERR    = 85; // 结束指令序列并抛出异常（消耗：2个元素，产出：0个元素）
    //
    // -------------------------------------------------------------- 辅助指令
    public static final byte OPT    = 1; // 选项参数（消耗：2个元素，产出：0个元素）
    public static final byte LINE   = 2; // 行号
    public static final byte LABEL  = 3; // 协助GOTO定位用，无实际作用
    public static final byte LOCAL  = 4; // 本地变量表名称
}