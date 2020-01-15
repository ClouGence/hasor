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
 * 用于封装 Hint。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public interface Hints extends HintValue {
    /** 获取选项参数 */
    public String[] getHints();

    /** 获取选项参数 */
    public Object getHint(String optionKey);

    /** 删除选项参数 */
    public void removeHint(String optionKey);

    /** 设置选项参数 */
    public default void setHints(Hints hints) {
        if (hints != null) {
            hints.forEach((optKey, value) -> {
                /**  */if (OperatorUtils.isNumber(value)) {
                    this.setHint(optKey, (Number) value);
                } else if (OperatorUtils.isBoolean(value)) {
                    this.setHint(optKey, (Boolean) value);
                } else if (value != null) {
                    this.setHint(optKey, value.toString());
                }
            });
        }
    }

    /** 设置选项参数 */
    public void setHint(String hintName, String value);

    /** 设置选项参数 */
    public void setHint(String hintName, Number value);

    /** 设置选项参数 */
    public void setHint(String hintName, boolean value);

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
        for (String hintName : getHints()) {
            Object optionValue = getHint(hintName);
            action.accept(hintName, optionValue);
        }
    }

    /**
     * If the specified key is not already associated with a value (or is mapped
     * to {@code null}) associates it with the given value and returns
     * {@code null}, else returns the current value.
     *
     * @param hintName key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @throws UnsupportedOperationException if the {@code put} operation is not supported by this map
     * @since 1.8
     */
    public default void putIfAbsent(String hintName, Object value) {
        if (getHint(hintName) == null) {
            /**  */if (OperatorUtils.isNumber(value)) {
                this.setHint(hintName, (Number) value);
            } else if (OperatorUtils.isBoolean(value)) {
                this.setHint(hintName, (Boolean) value);
            } else if (value != null) {
                this.setHint(hintName, value.toString());
            }
        }
    }

    /**
     * Returns the value to which the specified key is mapped, or
     * {@code defaultValue} if this map contains no mapping for the key.
     *
     * The default implementation makes no guarantees about synchronization
     * or atomicity properties of this method. Any implementation providing
     * atomicity guarantees must override this method and document its
     * concurrency properties.
     *
     * @param hintName the key whose associated value is to be returned
     * @param defaultValue the default mapping of the key
     * @return the value to which the specified key is mapped, or {@code defaultValue} if this map contains no mapping for the key
     * @since 1.8
     */
    public default Object getOrDefault(String hintName, Object defaultValue) {
        Object v = null;
        return (((v = getHint(hintName)) != null)) ? v : defaultValue;
    }

    /**
     * Returns the value to which the specified key is mapped, or
     * {@code defaultValue} if this map contains no mapping for the key.
     *
     * The default implementation makes no guarantees about synchronization
     * or atomicity properties of this method. Any implementation providing
     * atomicity guarantees must override this method and document its
     * concurrency properties.
     *
     * @param hintName the key whose associated value is to be returned
     * @param defaultValue the default mapping of the key
     * @return the value to which the specified key is mapped, or {@code defaultValue} if this map contains no mapping for the key
     * @since 1.8
     */
    public default <V> V getOrMap(String hintName, Function<Object, V> defaultValue) {
        return defaultValue.apply(getHint(hintName));
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
     * @param hintName key with which the specified value is to be associated
     * @param mappingFunction the function to compute a value
     * @throws UnsupportedOperationException if the {@code put} operation is not supported by this map
     * @since 1.8
     */
    public default void computeIfAbsent(String hintName, Function<String, Object> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        if (getHint(hintName) == null) {
            Object newValue;
            if ((newValue = mappingFunction.apply(hintName)) != null) {
                /**  */if (OperatorUtils.isNumber(newValue)) {
                    this.setHint(hintName, (Number) newValue);
                } else if (OperatorUtils.isBoolean(newValue)) {
                    this.setHint(hintName, (Boolean) newValue);
                } else {
                    this.setHint(hintName, newValue.toString());
                }
            }
        }
    }
}