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
import net.hasor.dataql.domain.compiler.InstOpcodes;

import java.math.BigDecimal;
import java.math.BigInteger;
/**
 * 二元数值运算，负责处理数值的："+"、"-"、"*"、"/"、"\"、"%"
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class EvaluationDOP extends DyadicOperatorProcess {
    @Override
    public Object doDyadicProcess(int opcode, String operator, Object fstObject, Object secObject, Option option) throws InvokerProcessException {
        if (!(fstObject instanceof Number) || !(secObject instanceof Number)) {
            throw throwError(operator, fstObject, secObject);
        }
        // .数值计算的选项参数
        PrecisionEnum precisionEnum = PrecisionEnum.find((Number) option.getOption(Option.NUMBER_PRECISION));   // 计算精度
        RoundingEnum roundingMode = RoundingEnum.find((String) option.getOption(Option.NUMBER_ROUNDING));       // 舍入模式
        Number maxDecimalNum = (Number) option.getOption(Option.MAX_DECIMAL_DIGITS);                               // 小数位数(默认20位)
        if (maxDecimalNum == null) {
            maxDecimalNum = 20;
        }
        int maxDecimal = maxDecimalNum.intValue();// 要保留的小数
        //
        // .数值计算
        Number result = null;
        if (PrecisionEnum.Auto == precisionEnum) {
            Number bigFstObject = autoToBig((Number) fstObject);
            Number bigSecObject = autoToBig((Number) secObject);
            result = doProcessBig(operator, bigFstObject, bigSecObject, maxDecimal, roundingMode);
        }
        if (PrecisionEnum.Bit8 == precisionEnum) {
            byte realFstObject = ((Number) fstObject).byteValue();
            byte realSecObject = ((Number) secObject).byteValue();
            result = doProcessBit32(operator, realFstObject, realSecObject);
        }
        if (PrecisionEnum.Bit16 == precisionEnum) {
            short realFstObject = ((Number) fstObject).shortValue();
            short realSecObject = ((Number) secObject).shortValue();
            result = doProcessBit32(operator, realFstObject, realSecObject);
        }
        if (PrecisionEnum.Bit32 == precisionEnum) {
            int realFstObject = ((Number) fstObject).intValue();
            int realSecObject = ((Number) secObject).intValue();
            result = doProcessBit32(operator, realFstObject, realSecObject);
        }
        if (PrecisionEnum.Bit64 == precisionEnum) {
            long realFstObject = ((Number) fstObject).longValue();
            long realSecObject = ((Number) secObject).longValue();
            result = doProcessBit64(operator, realFstObject, realSecObject);
        }
        // .处理小数保留位数(默认保留20位)
        if (result != null) {
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
        //
        String fstDataType = fstObject.getClass().getName();
        String secDataType = secObject.getClass().getName();
        throw new InvokerProcessException(opcode, fstDataType + " and " + secDataType + " , Cannot be used as '" + operator + "'.");
    }
    //
    private static InvokerProcessException throwError(String operator, Object realFstObject, Object realSecObject) {
        String fstDataType = realFstObject == null ? "null" : realFstObject.getClass().getName();
        String secDataType = realSecObject == null ? "null" : realSecObject.getClass().getName();
        return new InvokerProcessException(InstOpcodes.DO, fstDataType + " and " + secDataType + " , Cannot be used as '" + operator + "'.");
    }
    //
    private static Number autoToBig(Number number) {
        if (number instanceof BigInteger || number instanceof BigDecimal) {
            return number;
        } else {
            if (OperatorUtils.isFloatNumber(number) || OperatorUtils.isDoubleNumber(number)) {
                return new BigDecimal(number.toString());
            } else {
                return new BigInteger(number.toString());
            }
        }
    }
    //
    private static Number doProcessBit32(String operator, int realFstObject, int realSecObject) throws InvokerProcessException {
        // .加法
        if ("+".equals(operator)) {
            return realFstObject + realSecObject;
        }
        // .减法
        if ("-".equals(operator)) {
            return realFstObject - realSecObject;
        }
        // .乘法
        if ("*".equals(operator)) {
            return realFstObject * realSecObject;
        }
        // .除法
        if ("/".equals(operator)) {
            return (float) realFstObject / (float) realSecObject;
        }
        // .整除
        if ("\\".equals(operator)) {
            return realFstObject / realSecObject;
        }
        // .求余
        if ("%".equals(operator)) {
            return realFstObject % realSecObject;
        }
        throw throwError(operator, realFstObject, realSecObject);
    }
    //
    private Number doProcessBit64(String operator, long realFstObject, long realSecObject) throws InvokerProcessException {
        // .加法
        if ("+".equals(operator)) {
            return realFstObject + realSecObject;
        }
        // .减法
        if ("-".equals(operator)) {
            return realFstObject - realSecObject;
        }
        // .乘法
        if ("*".equals(operator)) {
            return realFstObject * realSecObject;
        }
        // .除法
        if ("/".equals(operator)) {
            return (double) realFstObject / (double) realSecObject;
        }
        // .整除
        if ("\\".equals(operator)) {
            return realFstObject / realSecObject;
        }
        // .求余
        if ("%".equals(operator)) {
            return realFstObject % realSecObject;
        }
        throw throwError(operator, realFstObject, realSecObject);
    }
    //
    private Number doProcessBig(String operator, Number bigFstObject, Number bigSecObject, int maxDecimal, RoundingEnum roundingMode) throws InvokerProcessException {
        boolean useDecimal = false;
        if (bigFstObject instanceof BigDecimal || bigSecObject instanceof BigDecimal) {
            if (bigFstObject instanceof BigInteger) {
                bigFstObject = new BigDecimal((BigInteger) bigFstObject);
            }
            if (bigSecObject instanceof BigInteger) {
                bigSecObject = new BigDecimal((BigInteger) bigSecObject);
            }
            useDecimal = true;
        }
        //
        // .加法
        if ("+".equals(operator)) {
            if (useDecimal) {
                return ((BigDecimal) bigFstObject).add((BigDecimal) bigSecObject);
            } else {
                return ((BigInteger) bigFstObject).add((BigInteger) bigSecObject);
            }
        }
        // .减法
        if ("-".equals(operator)) {
            if (useDecimal) {
                return ((BigDecimal) bigFstObject).subtract((BigDecimal) bigSecObject);
            } else {
                return ((BigInteger) bigFstObject).subtract((BigInteger) bigSecObject);
            }
        }
        // .乘法
        if ("*".equals(operator)) {
            if (useDecimal) {
                return ((BigDecimal) bigFstObject).multiply((BigDecimal) bigSecObject);
            } else {
                return ((BigInteger) bigFstObject).multiply((BigInteger) bigSecObject);
            }
        }
        // .除法
        if ("/".equals(operator)) {
            if (useDecimal) {
                return ((BigDecimal) bigFstObject)//
                        .divide((BigDecimal) bigSecObject, roundingMode.getModeNum());
            } else {
                bigFstObject = new BigDecimal((BigInteger) bigFstObject).setScale(maxDecimal, roundingMode.getModeNum());
                bigSecObject = new BigDecimal((BigInteger) bigSecObject).setScale(maxDecimal, roundingMode.getModeNum());
                return ((BigDecimal) bigFstObject)//
                        .divide((BigDecimal) bigSecObject, roundingMode.getModeNum());
            }
        }
        // .整除
        if ("\\".equals(operator)) {
            if (useDecimal) {
                return ((BigDecimal) bigFstObject).divideToIntegralValue((BigDecimal) bigSecObject);
            } else {
                return ((BigInteger) bigFstObject).divide((BigInteger) bigSecObject);
            }
        }
        // .求余
        if ("%".equals(operator)) {
            if (useDecimal) {
                return ((BigDecimal) bigFstObject).remainder((BigDecimal) bigSecObject);
            } else {
                return ((BigInteger) bigFstObject).mod((BigInteger) bigSecObject);
            }
        }
        throw throwError(operator, bigFstObject, bigSecObject);
    }
}