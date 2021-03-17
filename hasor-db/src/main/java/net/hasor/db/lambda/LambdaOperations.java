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
package net.hasor.db.lambda;
/**
 * 提供 lambda 方式生成 SQL。
 * @version : 2020-10-27
 * @author 赵永春 (zyc@hasor.net)
 */
public interface LambdaOperations {
    /** 相当于 insert ... */
    public <T> LambdaInsert<T> lambdaInsert(Class<T> exampleType);

    /** 相当于 update ... */
    public <T> LambdaUpdate<T> lambdaUpdate(Class<T> exampleType);

    /** 相当于 select * form */
    public <T> LambdaQuery<T> lambdaQuery(Class<T> exampleType);

    /** 相当于 delete */
    public <T> LambdaDelete<T> lambdaDelete(Class<T> exampleType);

    /** lambda query */
    public interface LambdaQuery<T> extends LambdaCommon<QueryExecute<T>, T>, QueryExecute<T>, QueryCompare<T, LambdaQuery<T>>, QueryFunc<T, LambdaQuery<T>> {
    }

    /** lambda update */
    public interface LambdaUpdate<T> extends LambdaCommon<UpdateExecute<T>, T>, UpdateExecute<T>, QueryCompare<T, LambdaUpdate<T>> {
    }

    /** lambda Delete */
    public interface LambdaDelete<T> extends LambdaCommon<DeleteExecute<T>, T>, DeleteExecute<T>, QueryCompare<T, LambdaDelete<T>> {
    }

    /** lambda insert */
    public interface LambdaInsert<T> extends LambdaCommon<LambdaInsert<T>, T>, InsertExecute<T> {
    }

    /** lambda insert */
    public interface LambdaCommon<R, T> {
        /** 参考的样本对象 */
        public Class<T> exampleType();

        public R useQualifier();
    }
}
