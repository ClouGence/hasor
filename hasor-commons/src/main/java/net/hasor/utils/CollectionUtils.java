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
package net.hasor.utils;
import java.util.*;
import java.util.function.Function;

/**
 *
 * @version : 2013-4-12
 * @author 赵永春 (zyc@hasor.net)
 */
public class CollectionUtils {
    // Empty utilities
    //--------------------------------------------------------------------------
    public static boolean isNotEmpty(List<?> tables) {
        return tables != null && !tables.isEmpty();
    }

    public static boolean isEmpty(List<?> tables) {
        return tables == null || tables.isEmpty();
    }
    // split utilities
    //--------------------------------------------------------------------------

    /**
     * 切分list
     * @param sourceList
     * @param groupSize 每组定长
     */
    public static <T> List<List<T>> splitList(List<T> sourceList, int groupSize) {
        int length = sourceList.size();
        // 计算可以分成多少组
        int num = (length + groupSize - 1) / groupSize;
        List<List<T>> newList = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            // 开始位置
            int fromIndex = i * groupSize;
            // 结束位置
            int toIndex = Math.min((i + 1) * groupSize, length);
            newList.add(sourceList.subList(fromIndex, toIndex));
        }
        return newList;
    }
    // Iterator/Enumeration utilities
    //--------------------------------------------------------------------------

    /** 迭代器类型转换 */
    public static <T, O> Iterator<O> convertIterator(final Iterator<T> oriIterator, final Function<T, O> converter) {
        return new Iterator<O>() {
            @Override
            public void remove() {
                oriIterator.remove();
            }

            @Override
            public O next() {
                return converter.apply(oriIterator.next());
            }

            @Override
            public boolean hasNext() {
                return oriIterator.hasNext();
            }
        };
    }

    /** 转换为 Enumeration */
    public static <T> Enumeration<T> asEnumeration(final Iterator<T> iterator) {
        return new Enumeration<T>() {
            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public T nextElement() {
                return iterator.next();
            }
        };
    }

    /** 合并两个迭代器 */
    public static <T> Enumeration<T> mergeEnumeration(final Enumeration<T> enum1, final Enumeration<T> enum2) {
        final Enumeration<T> i1 = enum1 != null ? enum1 : CollectionUtils.asEnumeration(Collections.emptyIterator());
        final Enumeration<T> i2 = enum2 != null ? enum2 : CollectionUtils.asEnumeration(Collections.emptyIterator());
        return new Enumeration<T>() {
            private Enumeration<T> it = i1;

            @Override
            public boolean hasMoreElements() {
                return i1.hasMoreElements() || i2.hasMoreElements();
            }

            @Override
            public T nextElement() {
                if (!this.it.hasMoreElements()) {
                    this.it = i2;
                }
                return this.it.nextElement();
            }
        };
    }

    /** 合并两个迭代器 */
    public static <T> Iterator<T> mergeIterator(final Iterator<T> iterator1, final Iterator<T> iterator2) {
        final Iterator<T> i1 = iterator1 != null ? iterator1 : Collections.emptyIterator();
        final Iterator<T> i2 = iterator2 != null ? iterator2 : Collections.emptyIterator();
        return new Iterator<T>() {
            private Iterator<T> it = i1;

            @Override
            public boolean hasNext() {
                return i1.hasNext() || i2.hasNext();
            }

            @Override
            public T next() {
                if (!this.it.hasNext()) {
                    this.it = i2;
                }
                return this.it.next();
            }

            @Override
            public void remove() {
                this.it.remove();
            }
        };
    }
}