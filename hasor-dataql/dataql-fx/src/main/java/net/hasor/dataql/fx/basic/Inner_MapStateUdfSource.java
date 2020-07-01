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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * 带有状态的集合。函数库引入 <code>import 'net.hasor.dataql.fx.basic.CollectionUdfSource' as collect; var arr = collect.newList()</code>
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2019-12-12
 */
class Inner_MapStateUdfSource implements UdfSourceAssembly {
    private final Map<String, Object> objectMap;
    private final Map<String, Udf>    self;

    public Inner_MapStateUdfSource(Map<String, Object> initData) {
        if (initData != null) {
            objectMap = initData;
        } else {
            objectMap = new LinkedHashMap<>();
        }
        //
        Class<?> targetType = getClass();
        Predicate<Method> predicate = getPredicate(targetType);
        Inner_MapStateUdfSource target = this;
        this.self = new TypeUdfMap(targetType, () -> target, predicate);
    }

    /** 把参数数据加到开头 */
    public Map<String, Udf> put(String key, Object dataValue) {
        this.objectMap.put(key, dataValue);
        return this.self;
    }

    /** 把参数数据加到末尾 */
    public Map<String, Udf> putAll(Map<String, Object> dataMap) {
        if (dataMap != null) {
            this.objectMap.putAll(dataMap);
        }
        return this.self;
    }

    /** 数组大小 */
    public int size() {
        return this.objectMap.size();
    }

    /** 有状态集合的数据
     * @return*/
    public Map<String, Object> data() {
        return this.objectMap;
    }
}