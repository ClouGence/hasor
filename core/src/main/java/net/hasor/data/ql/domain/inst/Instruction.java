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
        String codeName = null;
        try {
            //
            Field[] fields = InstOpcodes.class.getFields();
            for (Field field : fields) {
                byte aByte = field.getByte(null);
                if (aByte == this.instCode) {
                    codeName = field.getName();
                    break;
                }
            }
            //
        } catch (IllegalAccessException e) {
            codeName = "error : " + e.getMessage();
        }
        return codeName;
    }
}