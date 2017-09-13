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
import net.hasor.utils.StringUtils;

import java.math.RoundingMode;
/**
 * 数值计算最大精度
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public enum RoundingEnum {
    /**向远离零的方向舍入。舍弃非零部分，并将非零舍弃部分相邻的一位数字加一。*/
    UP(RoundingMode.UP),//
    /**向接近零的方向舍入。舍弃非零部分，同时不会非零舍弃部分相邻的一位数字加一，采取截取行为。*/
    DOWN(RoundingMode.DOWN),//
    /**
     * 向正无穷的方向舍入。如果为正数，舍入结果同ROUND_UP一致；如果为负数，舍入结果同ROUND_DOWN一致。
     * 注意：此模式不会减少数值大小。*/
    CEILING(RoundingMode.CEILING),//
    /**
     * 向负无穷的方向舍入。如果为正数，舍入结果同ROUND_DOWN一致；如果为负数，舍入结果同ROUND_UP一致。
     * 注意：此模式不会增加数值大小。*/
    FLOOR(RoundingMode.FLOOR),//
    /**
     * 向“最接近”的数字舍入，如果与两个相邻数字的距离相等，则为向上舍入的舍入模式。如果舍弃部分>= 0.5，则舍入行为与ROUND_UP相同；否则舍入行为与ROUND_DOWN相同。
     * 这种模式也就是我们常说的我们的“四舍五入”。*/
    HALF_UP(RoundingMode.HALF_UP),//
    /**
     * 向“最接近”的数字舍入，如果与两个相邻数字的距离相等，则为向下舍入的舍入模式。如果舍弃部分> 0.5，则舍入行为与ROUND_UP相同；否则舍入行为与ROUND_DOWN相同。
     * 这种模式也就是我们常说的我们的“五舍六入”。*/
    HALF_DOWN(RoundingMode.HALF_DOWN),//
    /**向“最接近”的数字舍入，如果与两个相邻数字的距离相等，则相邻的偶数舍入。如果舍弃部分左边的数字奇数，则舍入行为与 ROUND_HALF_UP 相同；如果为偶数，则舍入行为与 ROUND_HALF_DOWN 相同。
     * 注意：在重复进行一系列计算时，此舍入模式可以将累加错误减到最小。
     * 此舍入模式也称为“银行家舍入法”，主要在美国使用。
     * 四舍六入，五分两种情况，如果前一位为奇数，则入位，否则舍去。*/
    HALF_EVEN(RoundingMode.HALF_EVEN),//
    /**断言请求的操作具有精确的结果，因此不需要舍入。如果对获得精确结果的操作指定此舍入模式，则抛出ArithmeticException。*/
    UNNECESSARY(RoundingMode.UNNECESSARY);
    //
    private RoundingMode modeNum;
    RoundingEnum(RoundingMode modeNum) {
        this.modeNum = modeNum;
    }
    public RoundingMode getModeNum() {
        return this.modeNum;
    }
    public static RoundingEnum find(String modeType) {
        if (StringUtils.isBlank(modeType))
            return HALF_UP;
        for (RoundingEnum pre : RoundingEnum.values()) {
            if (pre.name().equalsIgnoreCase(modeType))
                return pre;
        }
        return HALF_UP;
    }
}