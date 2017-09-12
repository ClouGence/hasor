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
package net.hasor.dataql.runtime.operator;
import net.hasor.dataql.InvokerProcessException;
import net.hasor.dataql.Option;
import net.hasor.dataql.runtime.utils.NumberUtils;
import net.hasor.dataql.runtime.utils.OperatorUtils;
/**
 * 二元比较运算，负责处理：">", ">=", "<", "<=", "==", "!=", "&&", "||", "&", "|", "^", "<<", ">>", ">>>"
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class CompareDOP extends DyadicOperatorProcess {
    private static final Integer BOOL_FASLE = 0;
    private static final Integer BOOL_TRUE  = 1;
    @Override
    public Object doDyadicProcess(int opcode, String operator, Object fstObject, Object secObject, Option option) throws InvokerProcessException {
        //
        // .Boolean 和 Number 混杂模式下，先统一成为 number 在做判断
        if (OperatorUtils.isBoolean(fstObject) && OperatorUtils.isBoolean(secObject)) {
            fstObject = Boolean.TRUE.equals(fstObject) ? BOOL_TRUE : BOOL_FASLE;
            secObject = Boolean.TRUE.equals(secObject) ? BOOL_TRUE : BOOL_FASLE;
        }
        if (OperatorUtils.isBoolean(fstObject) && OperatorUtils.isNumber(secObject)) {
            fstObject = Boolean.TRUE.equals(fstObject) ? BOOL_TRUE : BOOL_FASLE;
            secObject = NumberUtils.eq((Number) secObject, 0) ? BOOL_FASLE : BOOL_TRUE;
        }
        if (OperatorUtils.isNumber(fstObject) && OperatorUtils.isBoolean(secObject)) {
            fstObject = NumberUtils.eq((Number) fstObject, 0) ? BOOL_FASLE : BOOL_TRUE;
            secObject = Boolean.TRUE.equals(secObject) ? BOOL_TRUE : BOOL_FASLE;
        }
        //
        // .大于
        if (">".equals(operator)) {
            return NumberUtils.gt((Number) fstObject, (Number) secObject);
        }
        // .大于等于
        if (">=".equals(operator)) {
            return NumberUtils.gteq((Number) fstObject, (Number) secObject);
        }
        // .小于
        if ("<".equals(operator)) {
            return NumberUtils.lt((Number) fstObject, (Number) secObject);
        }
        // .小于等于
        if ("<=".equals(operator)) {
            return NumberUtils.lteq((Number) fstObject, (Number) secObject);
        }
        // .等于
        if ("==".equals(operator)) {
            return NumberUtils.eq((Number) fstObject, (Number) secObject);
        }
        // .不等于
        if ("!=".equals(operator)) {
            return !NumberUtils.eq((Number) fstObject, (Number) secObject);
        }
        //
        // .与
        if ("&".equals(operator)) {
            return !NumberUtils.and((Number) fstObject, (Number) secObject);
        }
        // .或
        if ("|".equals(operator)) {
            return !NumberUtils.or((Number) fstObject, (Number) secObject);
        }
        // .异或
        if ("^".equals(operator)) {
            return !NumberUtils.xor((Number) fstObject, (Number) secObject);
        }
        // .左位移
        if ("<<".equals(operator)) {
            return !NumberUtils.leftShift((Number) fstObject, (Number) secObject);
        }
        // .带符号右位移
        if (">>".equals(operator)) {
            return !NumberUtils.rightShift((Number) fstObject, (Number) secObject);
        }
        // .无符号右位移
        if (">>>".equals(operator)) {
            return !NumberUtils.rightShiftWithUnsigned((Number) fstObject, (Number) secObject);
        }
        // .逻辑比较运算
        if ("&&".equals(operator) || "||".equals(operator)) {
            boolean fstBool, secBool;
            if (OperatorUtils.isNumber(fstObject)) {
                fstBool = !NumberUtils.eq((Number) fstObject, 0);
            } else {
                fstBool = Boolean.TRUE.equals(fstObject);
            }
            if (OperatorUtils.isNumber(secObject)) {
                secBool = !NumberUtils.eq((Number) secObject, 0);
            } else {
                secBool = Boolean.TRUE.equals(secObject);
            }
            //
            if ("&&".equals(operator)) {
                return fstBool && secBool;
            }
            if ("||".equals(operator)) {
                return fstBool || secBool;
            }
        }
        throw throwError(operator, fstObject, secObject, "this operator nonsupport.");
    }
}