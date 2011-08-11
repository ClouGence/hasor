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
package org.more.util;
import java.util.Iterator;
/**
 * 该工具类的功能是将两个迭代器合并为一个迭代器。
 * @version 2010-1-9
 * @author 赵永春 (zyc@byshell.org)
 */
public class MergeIterator<T> implements Iterator<T> {
    private Iterator<T> oneIterators;
    private Iterator<T> twoIterators;
    private Iterator<T> temp_Iterators;
    public MergeIterator(Iterator<T> first, Iterator<T> second) {
        oneIterators = first;
        twoIterators = second;
        temp_Iterators = first;
    }
    public boolean hasNext() {
        return (oneIterators.hasNext() || twoIterators.hasNext()) ? true : false;
    }
    public T next() {
        if (temp_Iterators.hasNext() == false)
            temp_Iterators = twoIterators;
        return temp_Iterators.next();
    }
    public void remove() {
        throw new UnsupportedOperationException("MergeIterator不支持该操作。");
    }
}