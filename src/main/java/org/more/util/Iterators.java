/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package org.more.util;
import java.util.Enumeration;
import java.util.Iterator;
/**
 * 
 * @version : 2013-4-12
 * @author 赵永春 (zyc@hasor.net)
 */
public final class Iterators {
    /**用于迭代器类型转换*/
    public static interface Converter<T, O> {
        public O converter(T target);
    }
    /**迭代器类型转换*/
    public static <T, O> Iterator<O> converIterator(final Iterator<T> oriIterator, final Converter<T, O> converter) {
        return new Iterator<O>() {
            @Override
            public void remove() {
                oriIterator.remove();
            }
            @Override
            public O next() {
                return converter.converter(oriIterator.next());
            }
            @Override
            public boolean hasNext() {
                return oriIterator.hasNext();
            }
        };
    }
    /**转换为 Enumeration*/
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
}