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
package net.hasor.dataql.sdk;
import net.hasor.dataql.Finder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * 带有状态的集合。函数库引入 <code>import 'net.hasor.dataql.sdk.CollectionUdfSource' as collect; var arr = collect.new</code>
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2019-12-12
 */
class InnerCollectionStateUdfSource implements UdfSourceAssembly {
    private List<Object> objectArrayList;

    public InnerCollectionStateUdfSource(List<Object> initData) {
        if (initData != null) {
            objectArrayList = initData;
        } else {
            objectArrayList = new ArrayList<>();
        }
    }

    @Override
    public Supplier<?> getSupplier(Class<?> targetType, Finder finder) {
        return () -> this;
    }

    /** 把参数数据加到开头 */
    public List<Object> addFirst(Object dataArrays) {
        if (dataArrays != null) {
            this.objectArrayList = CollectionUdfSource.merge(() -> {
                return new Object[] { dataArrays, objectArrayList };
            });
        }
        return this.objectArrayList;
    }

    /** 把参数数据加到末尾 */
    public List<Object> addLast(Object dataArrays) {
        if (dataArrays != null) {
            this.objectArrayList = CollectionUdfSource.merge(() -> {
                return new Object[] { objectArrayList, dataArrays };
            });
        }
        return this.objectArrayList;
    }

    /** 有状态集合的数据 */
    public List<Object> data() {
        return this.objectArrayList;
    }
}