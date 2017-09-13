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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
/**
 *
 * @version : 2013-4-12
 * @author 赵永春 (zyc@hasor.net)
 */
public class Iterators {
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
    /**合并两个迭代器*/
    public static <T> Enumeration<T> mergeEnumeration(final Enumeration<T> enum1, final Enumeration<T> enum2) {
        final Enumeration<T> i1 = enum1 != null ? enum1 : Iterators.asEnumeration(new ArrayList<T>(0).iterator());
        final Enumeration<T> i2 = enum2 != null ? enum2 : Iterators.asEnumeration(new ArrayList<T>(0).iterator());
        return new Enumeration<T>() {
            private Enumeration<T> it = i1;
            @Override
            public boolean hasMoreElements() {
                return i1.hasMoreElements() || i2.hasMoreElements() ? true : false;
            }
            @Override
            public T nextElement() {
                if (this.it.hasMoreElements() == false) {
                    this.it = i2;
                }
                return this.it.nextElement();
            }
        };
    }
    /**合并两个迭代器*/
    public static <T> Iterator<T> mergeIterator(final Iterator<T> iterator1, final Iterator<T> iterator2) {
        final Iterator<T> i1 = iterator1 != null ? iterator1 : new ArrayList<T>(0).iterator();
        final Iterator<T> i2 = iterator2 != null ? iterator2 : new ArrayList<T>(0).iterator();
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