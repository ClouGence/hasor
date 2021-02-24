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
package net.hasor.dataql.runtime.operator.ops;
import net.hasor.dataql.Hints;
import net.hasor.dataql.parser.location.RuntimeLocation;
import net.hasor.dataql.runtime.QueryRuntimeException;
import net.hasor.dataql.runtime.operator.OperatorUtils;

/**
 * 二元比较运算，负责处理：, "&", "|", "^", "<<", ">>", ">>>"
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class BinaryDOP extends AbstractDOP {
    private static final Integer BOOL_FALSE = 0;
    private static final Integer BOOL_TRUE  = 1;

    @Override
    public Object doDyadicProcess(RuntimeLocation location, String operator, Object fstObject, Object secObject, Hints option) throws QueryRuntimeException {
        //
        // .Boolean 和 Number 混杂模式下，先统一成为 number 在做判断
        if (OperatorUtils.isBoolean(fstObject) && OperatorUtils.isBoolean(secObject)) {
            fstObject = Boolean.TRUE.equals(fstObject) ? BOOL_TRUE : BOOL_FALSE;
            secObject = Boolean.TRUE.equals(secObject) ? BOOL_TRUE : BOOL_FALSE;
        }
        if (OperatorUtils.isBoolean(fstObject) && OperatorUtils.isNumber(secObject)) {
            fstObject = Boolean.TRUE.equals(fstObject) ? BOOL_TRUE : BOOL_FALSE;
            secObject = OperatorUtils.eq((Number) secObject, 0) ? BOOL_FALSE : BOOL_TRUE;
        }
        if (OperatorUtils.isNumber(fstObject) && OperatorUtils.isBoolean(secObject)) {
            fstObject = OperatorUtils.eq((Number) fstObject, 0) ? BOOL_FALSE : BOOL_TRUE;
            secObject = Boolean.TRUE.equals(secObject) ? BOOL_TRUE : BOOL_FALSE;
        }
        // .与
        if ("&".equals(operator)) {
            return OperatorUtils.and((Number) fstObject, (Number) secObject);
        }
        // .或
        if ("|".equals(operator)) {
            return OperatorUtils.or((Number) fstObject, (Number) secObject);
        }
        // .异或
        if ("^".equals(operator)) {
            return OperatorUtils.xor((Number) fstObject, (Number) secObject);
        }
        // .左位移
        if ("<<".equals(operator)) {
            return OperatorUtils.shiftLeft((Number) fstObject, (Number) secObject);
        }
        // .带符号右位移
        if (">>".equals(operator)) {
            return OperatorUtils.shiftRight((Number) fstObject, (Number) secObject);
        }
        // .无符号右位移
        if (">>>".equals(operator)) {
            return OperatorUtils.shiftRightWithUnsigned((Number) fstObject, (Number) secObject);
        }
        throw throwError(location, operator, fstObject, secObject, "this operator nonsupport.");
    }
}
