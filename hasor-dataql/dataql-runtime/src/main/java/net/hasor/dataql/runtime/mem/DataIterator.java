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
package net.hasor.dataql.runtime.mem;
import net.hasor.dataql.domain.DomainHelper;

import java.util.Iterator;

/**
 * 数据迭代器
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2019-11-22
 */
public class DataIterator {
    private Iterator iterator = null;
    private Object   oriData  = null;
    private Object   data     = null;

    public DataIterator(Object oriData, Iterator iterator) {
        this.oriData = oriData;
        this.iterator = iterator;
    }

    public Object getData() {
        return data;
    }

    public boolean isNext() {
        if (this.iterator.hasNext()) {
            this.data = DomainHelper.convertTo(iterator.next());
            return true;
        }
        return false;
    }

    public Object getOriData() {
        return this.oriData;
    }
}