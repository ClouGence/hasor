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
import net.hasor.dataql.runtime.operator.OperatorUtils;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 用于封装 Option。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public interface Option extends OptionValue {
    /** 获取选项参数 */
    public String[] getOptionNames();

    /** 获取选项参数 */
    public Object getOption(String optionKey);

    /** 删除选项参数 */
    public void removeOption(String optionKey);

    /** 设置选项参数 */
    public default void setOptionSet(Option optionSet) {
        if (optionSet != null) {
            optionSet.forEach((optKey, value) -> {
                /**  */if (OperatorUtils.isNumber(value)) {
                    this.setOption(optKey, (Number) value);
                } else if (OperatorUtils.isBoolean(value)) {
                    this.setOption(optKey, (Boolean) value);
                } else if (value != null) {
                    this.setOption(optKey, value.toString());
                }
            });
        }
    }

    /** 设置选项参数 */
    public void setOption(String optionKey, String value);

    /** 设置选项参数 */
    public void setOption(String optionKey, Number value);

    /** 设置选项参数 */
    public void setOption(String optionKey, boolean value);

    /**
     * Performs the given action for each entry in this map until all entries
     * have been processed or the action throws an exception.   Unless
     * otherwise specified by the implementing class, actions are performed in
     * the order of entry set iteration (if an iteration order is specified.)
     * Exceptions thrown by the action are relayed to the caller.
     *
     * @param action The action to be performed for each entry
     * @throws NullPointerException if the specified action is null
     * @since 1.8
     */
    public default void forEach(BiConsumer<String, Object> action) {
        Objects.requireNonNull(action);
        for (String optionKey : getOptionNames()) {
            Object optionValue = getOption(optionKey);
            action.accept(optionKey, optionValue);
        }
    }

    /**
     * If the specified key is not already associated with a value (or is mapped
     * to {@code null}) associates it with the given value and returns
     * {@code null}, else returns the current value.
     *
     * @param optKey key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @throws UnsupportedOperationException if the {@code put} operation is not supported by this map
     * @since 1.8
     */
    public default void putIfAbsent(String optKey, Object value) {
        if (getOption(optKey) == null) {
            /**  */if (OperatorUtils.isNumber(value)) {
                this.setOption(optKey, (Number) value);
            } else if (OperatorUtils.isBoolean(value)) {
                this.setOption(optKey, (Boolean) value);
            } else if (value != null) {
                this.setOption(optKey, value.toString());
            }
        }
    }

    /**
     * If the specified key is not already associated with a value (or is mapped
     * to {@code null}), attempts to compute its value using the given mapping
     * function and enters it into this map unless {@code null}.
     *
     * <p>If the function returns {@code null} no mapping is recorded. If
     * the function itself throws an (unchecked) exception, the
     * exception is rethrown, and no mapping is recorded.  The most
     * common usage is to construct a new object serving as an initial
     * mapped value or memoized result.
     *
     * @param optKey key with which the specified value is to be associated
     * @param mappingFunction the function to compute a value
     * @throws UnsupportedOperationException if the {@code put} operation is not supported by this map
     * @since 1.8
     */
    public default void computeIfAbsent(String optKey, Function<String, Object> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        if (getOption(optKey) == null) {
            Object newValue;
            if ((newValue = mappingFunction.apply(optKey)) != null) {
                /**  */if (OperatorUtils.isNumber(newValue)) {
                    this.setOption(optKey, (Number) newValue);
                } else if (OperatorUtils.isBoolean(newValue)) {
                    this.setOption(optKey, (Boolean) newValue);
                } else {
                    this.setOption(optKey, newValue.toString());
                }
            }
        }
    }
}