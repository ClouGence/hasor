/*
 * Copyright 2002-2005 the original author or authors.
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
package net.hasor.db.jdbc.lambda;
import net.hasor.utils.reflect.SFunction;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 提供 lambda 方式生成 SQL。
 * @version : 2020-10-27
 * @author 赵永春 (zyc@hasor.net)
 */
public interface LambdaOperations {
    /** 相当于 select * form */
    public default <T> LambdaQuery<T> lambda(Class<T> exampleType) {
        return lambda(exampleType, Collections.emptyList());
    }

    /** 相当于 select xxx,xxx,xxx form */
    public <T> LambdaQuery<T> lambda(Class<T> exampleType, List<SFunction<T>> columns);

    /** 封装 */
    public interface LambdaQuery<T> extends Compare<T, LambdaQuery<T>>, Func<T, LambdaQuery<T>>,//
            BoundSql, QueryExecute<T> {
    }

    /** 嵌套 */
    public interface NestedQuery<T> extends Compare<T, NestedQuery<T>> {
    }

    public interface BoundSql {
        public String getSqlString();

        public Map<String, Object> getArgs();
    }
}