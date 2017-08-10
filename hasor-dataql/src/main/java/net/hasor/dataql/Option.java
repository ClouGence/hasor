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
package net.hasor.dataql;
/**
 * 用于封装 Option。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public interface Option {
    /** 在执行 put 时，如果不能 put 是否引发异常（默认为 true：安全的，不引发异常） */
    public static final String SAFE_PUT           = "SAFE_PUT";
    /**
     * 在进行数值计算时采用的精度，超出范围的数值计算将会产生精度丢失。
     * 开发者可以通过设置成 0 来启用自动精度，自动精度计算下数值计算将通过 BigDecimal、BigInteger 计算（默认为 32 int）
     *
     * 注意：精度并不代表产生的计算结果精度，而是计算前的数值精度。
     * DO，指令中使用该参数，受影响的运算符为："+", "-", "*", "/", "%", "\"
     * */
    public static final String NUMBER_PRECISION   = "NUMBER_PRECISION";
    /**
     * 最大保留的小数位数，默认为：20。超出该范围将会根据 NUMBER_ROUNDING 选项指定的舍入模式进行舍入，默认是四舍五入。
     */
    public static final String MAX_DECIMAL_DIGITS = "MAX_DECIMAL_DIGITS";
    /**小数的舍入模式，参考 RoundingEnum 定义的舍入模式(一共八种)，默认为：四舍五入。详细配置参考：RoundingEnum 枚举。*/
    public static final String NUMBER_ROUNDING    = "NUMBER_ROUNDING";
    //

    /** 获取选项参数 */
    public String[] getOptionNames();

    /** 获取选项参数 */
    public Object getOption(String optionKey);

    /** 删除选项参数 */
    public void removeOption(String optionKey);

    /** 设置选项参数 */
    public void setOption(String optionKey, String value);

    /** 设置选项参数 */
    public void setOption(String optionKey, Number value);

    /** 设置选项参数 */
    public void setOption(String optionKey, boolean value);
}