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
package net.hasor.data.ql.domain.inst;
import net.hasor.core.utils.StringUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
/**
 * QL 指令
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-07-03
 */
public class Instruction implements InstOpcodes {
    private byte         instCode  = 0;
    private List<Object> instParam = null;
    public Instruction(byte instCode, Object[] instParam) {
        this.instCode = instCode;
        this.instParam = Arrays.asList(instParam);
    }
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
        for (int i = 0; i < this.instParam.size(); i++) {
            if (i > 0) {
                codeName.append(", ");
            }
            Object obj = this.instParam.get(i);
            codeName.append(obj);
        }
        //
        return codeName.toString();
    }
}