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
import net.hasor.utils.StringUtils;

import java.lang.reflect.Field;
/**
 * QL 指令
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-07-03
 */
public class InstructionInfo implements Opcodes, Instruction {
    private byte     instCode     = 0;
    private Object[] instParam    = null;
    private boolean  compilerMark = false;//一个特殊的标，用于处理 ListExpression、ObjectExpression 两个模型编译时是否输出对应的 NA、NO 指令。
    public InstructionInfo(byte instCode, Object[] instParam) {
        this.instCode = instCode;
        this.instParam = instParam == null ? new Object[0] : instParam;
    }
    //
    //
    /**获取指令码。*/
    public byte getInstCode() {
        return this.instCode;
    }
    /**获取 字符串数据*/
    public String getString(int index) {
        return (String) this.instParam[index];
    }
    /**获取 布尔数据*/
    public Boolean getBoolean(int index) {
        return (Boolean) this.instParam[index];
    }
    /**获取 数字数据*/
    public Number getNumber(int index) {
        return (Number) this.instParam[index];
    }
    /**获取 数字数据*/
    public int getInt(int index) {
        return (Integer) this.instParam[index];
    }
    /**获取 字符串数据*/
    public Object[] getArrays() {
        return this.instParam;
    }
    /**
     * 将 Label 替换为本身标记的行号。
     * 如果出现 Label 未插入情况，则返回false。
     * （每个Label 都要通过 InstQueue.inst方法插入到指令序列中，一个被使用的 Label 如果没有插入到序列中的情况被成为，Label未插入）
     * */
    public boolean replaceLabel() {
        for (int i = 0; i < this.instParam.length; i++) {
            if (this.instParam[i] instanceof Label) {
                Label label = (Label) this.instParam[i];
                if (label.getIndex() == null) {
                    return false;
                }
                this.instParam[i] = label.getIndex();
            }
        }
        return true;
    }
    //
    //
    @Override
    public String toString() {
        StringBuilder codeName = new StringBuilder();
        try {
            //
            Field[] fields = Opcodes.class.getFields();
            for (Field field : fields) {
                byte aByte = field.getByte(null);
                if (aByte == this.instCode) {
                    codeName.append(field.getName());
                    break;
                }
            }
            //
        } catch (IllegalAccessException e) {
            codeName.append("error : ");
            codeName.append(e.getMessage());
            return codeName.toString();
        }
        //
        int needSpace = 10 - codeName.length();
        if (needSpace > 0) {
            codeName.append(StringUtils.leftPad("", needSpace, ' '));
        }
        for (int i = 0; i < this.instParam.length; i++) {
            if (i > 0) {
                codeName.append(", ");
            }
            codeName.append(this.instParam[i]);
        }
        //
        return codeName.toString();
    }
    //
    /** 判断是否要被编译输出 */
    public boolean isCompilerMark() {
        return this.compilerMark;
    }
    /** 判断是否要被编译输出 */
    public void setCompilerMark(boolean compilerMark) {
        this.compilerMark = compilerMark;
    }
}