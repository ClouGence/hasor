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
package net.hasor.dataql.domain.inst;
import net.hasor.core.utils.StringUtils;

import java.lang.reflect.Field;
/**
 * QL 指令
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-07-03
 */
public class Instruction implements InstOpcodes {
    private byte     instCode  = 0;
    private Object[] instParam = null;
    public Instruction(byte instCode, Object[] instParam) {
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
    //
    //
    @Override
    public String toString() {
        StringBuilder codeName = new StringBuilder();
        try {
            //
            Field[] fields = InstOpcodes.class.getFields();
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
}