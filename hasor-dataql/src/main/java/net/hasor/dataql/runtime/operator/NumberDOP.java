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

import java.math.BigDecimal;
/**
 * 二元数值运算，负责处理数值的："+"、"-"、"*"、"/"、"\"、"%"
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class NumberDOP extends DyadicOperatorProcess {
    @Override
    public Object doDyadicProcess(int opcode, String operator, Object fstObject, Object secObject, Option option) throws InvokerProcessException {
        if (!(fstObject instanceof Number) || !(secObject instanceof Number)) {
            throw throwError(operator, fstObject, secObject, "requirements must be numerical.");
        }
        // .数值计算的选项参数
        RoundingEnum roundingMode = RoundingEnum.find((String) option.getOption(Option.NUMBER_ROUNDING));       // 舍入模式
        Number maxDecimalNum = (Number) option.getOption(Option.MAX_DECIMAL_DIGITS);                            // 小数位数(默认20位)
        if (maxDecimalNum == null) {
            maxDecimalNum = 20;
        }
        int maxDecimal = maxDecimalNum.intValue();// 要保留的小数
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
            throw throwError(operator, fstObject, secObject, "this operator nonsupport.");
        }
        if (result == null) {
            throw throwError(operator, fstObject, secObject, "evaluation result is empty.");
        }
        //
        // .处理小数保留位数(默认保留20位)
        //
        // float
        //      1bit（符号位） 8bits（指数位） 23bits（尾数位）
        //      2^23 = 8388608，一共七位，因此小数精度为 6~7位。
        if (OperatorUtils.isFloatNumber(result) && maxDecimal < 7) {
            BigDecimal resultDecimal = new BigDecimal(result.toString());
            resultDecimal = resultDecimal.setScale(maxDecimal, roundingMode.getModeNum());
            return resultDecimal.floatValue();
        }
        // double
        //      1bit（符号位） 11bits（指数位） 52bits（尾数位）
        //      2^52 = 4503599627370496，一共16位，因此小数精度为 15~16位。
        if (OperatorUtils.isDoubleNumber(result) && maxDecimal < 16) {
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