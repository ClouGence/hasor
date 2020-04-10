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
package net.hasor.dataql.fx.basic;
import net.hasor.dataql.Udf;
import net.hasor.dataql.UdfSourceAssembly;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * 带有状态的集合。函数库引入 <code>import 'net.hasor.dataql.fx.basic.CollectionUdfSource' as collect; var arr = collect.newList()</code>
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2019-12-12
 */
class Inner_ListStateUdfSource implements UdfSourceAssembly {
    private List<Object>     objectArrayList;
    private Map<String, Udf> self;

    public Inner_ListStateUdfSource(List<Object> initData) {
        if (initData != null) {
            objectArrayList = initData;
        } else {
            objectArrayList = new ArrayList<>();
        }
        //
        Class<?> targetType = getClass();
        Predicate<Method> predicate = getPredicate(targetType);
        Inner_ListStateUdfSource target = this;
        this.self = new TypeUdfMap(targetType, () -> target, predicate);
    }

    /** 把参数数据加到开头 */
    public Map<String, Udf> addFirst(Object dataArrays) {
        if (dataArrays != null) {
            this.objectArrayList = CollectionUdfSource.merge(() -> {
                return new Object[] { dataArrays, objectArrayList };
            });
        }
        return this.self;
    }

    /** 把参数数据加到末尾 */
    public Map<String, Udf> addLast(Object dataArrays) {
        if (dataArrays != null) {
            this.objectArrayList = CollectionUdfSource.merge(() -> {
                return new Object[] { objectArrayList, dataArrays };
            });
        }
        return this.self;
    }

    /** 有状态集合的数据 */
    public List<Object> data() {
        return this.objectArrayList;
    }
}