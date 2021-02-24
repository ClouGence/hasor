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

import java.math.BigDecimal;

import static net.hasor.dataql.Hints.MIN_DECIMAL_WIDTH;
import static net.hasor.dataql.Hints.MIN_INTEGER_WIDTH;

/**
 * 二元数值运算，负责处理数值的："+"、"-"、"*"、"/"、"\"、"%"
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class NumberDOP extends AbstractDOP {
    @Override
    public Object doDyadicProcess(RuntimeLocation location, String operator, Object fstObject, Object secObject, Hints option) throws QueryRuntimeException {
        if (!(fstObject instanceof Number) || !(secObject instanceof Number)) {
            throw throwError(location, operator, fstObject, secObject, "requirements must be numerical.");
        }
        // .数值计算的选项参数
        RoundingEnum roundingMode = RoundingEnum.find((String) option.getHint(Hints.NUMBER_ROUNDING));   // 舍入模式
        int maxDecimal = option.getOrMap(Hints.MAX_DECIMAL_DIGITS, val -> {                              // 小数位数(默认20位)
            if (val == null) {
                return 20;
            }
            if (val instanceof Number) {
                return ((Number) val).intValue();
            }
            return Integer.parseInt(val.toString());
        });
        //
        // .调整最小精度宽度
        String decimalWidth = (String) option.getHint(MIN_DECIMAL_WIDTH);
        String integerWidth = (String) option.getHint(MIN_INTEGER_WIDTH);
        fstObject = OperatorUtils.fixNumberWidth((Number) fstObject, decimalWidth, integerWidth);
        secObject = OperatorUtils.fixNumberWidth((Number) secObject, decimalWidth, integerWidth);
        //
        // .数值计算
        Number result = null;
        switch (operator.charAt(0)) {
            case '+':
                result = OperatorUtils.add((Number) fstObject, (Number) secObject);
                break;
            case '-':
                result = OperatorUtils.subtract((Number) fstObject, (Number) secObject);
                break;
            case '*':
                result = OperatorUtils.multiply((Number) fstObject, (Number) secObject);
                break;
            case '/':
                result = OperatorUtils.divide((Number) fstObject, (Number) secObject, maxDecimal, roundingMode);
                break;
            case '\\':
                result = OperatorUtils.aliquot((Number) fstObject, (Number) secObject);
                break;
            case '%':
                result = OperatorUtils.mod((Number) fstObject, (Number) secObject);
                break;
            default:
                throw throwError(location, operator, fstObject, secObject, "this operator nonsupport.");
        }
        if (result == null) {
            throw throwError(location, operator, fstObject, secObject, "evaluation result is empty.");
        }
        //
        // .处理小数保留位数(默认保留20位)
        //
        // float
        //      1bit（符号位） 8bits（指数位） 23bits（尾数位）
        //      2^23 = 8388608，一共七位，因此小数精度为 6~7位。
        if (OperatorUtils.isFloatNumber(result) && maxDecimal < 7) {
            float number = (float) result;
            if (number == Float.POSITIVE_INFINITY || Float.isNaN(number) || number == Float.NEGATIVE_INFINITY) {
                return result.toString();
            }
            BigDecimal resultDecimal = new BigDecimal(result.toString());
            resultDecimal = resultDecimal.setScale(maxDecimal, roundingMode.getModeNum());
            return resultDecimal.floatValue();
        }
        // double
        //      1bit（符号位） 11bits（指数位） 52bits（尾数位）
        //      2^52 = 4503599627370496，一共16位，因此小数精度为 15~16位。
        if (OperatorUtils.isDoubleNumber(result) && maxDecimal < 16) {
            double number = (double) result;
            if (number == Double.POSITIVE_INFINITY || Double.isNaN(number) || number == Double.NEGATIVE_INFINITY) {
                return result.toString();
            }
            BigDecimal resultDecimal = new BigDecimal(result.toString());
            resultDecimal = resultDecimal.setScale(maxDecimal, roundingMode.getModeNum());
            return resultDecimal.doubleValue();
        }
        // BigDecimal 大数，通过 setScale 处理保留位数。
        if (result instanceof BigDecimal) {
            return ((BigDecimal) result)//
                    .setScale(maxDecimal, roundingMode.getModeNum())// 设置小数位数
                    .stripTrailingZeros();// 抹除末尾0
        }
        //
        return result;
    }
}
