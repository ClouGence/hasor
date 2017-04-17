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
package net.hasor.graphql.ctx;
import net.hasor.core.Provider;
import net.hasor.graphql.UDF;

import java.lang.reflect.Method;
import java.util.Map;
/**
 *
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class GraphUDF implements UDF {
    private Provider<?>              targetService; // 目标服务
    private Method                   targetMethod;  // 目标方法
    //
    private Map<String, Integer>     paramIndex;    // 参数索引
    private Map<String, Class<?>>    paramType;     // 参数类型
    private Map<String, Provider<?>> paramDefault;  // 参数默认值
    //
    @Override
    public Object call(Map<String, Object> values) {
        return null;
    }
}