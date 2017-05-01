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
package net.hasor.graphql;
import net.hasor.core.ApiBinder;
import net.hasor.core.BindInfo;
import net.hasor.core.Provider;
/**
 * QL 扩展，用于注册 GraphUDF。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public interface GraphApiBinder extends ApiBinder {
    /**添加 GraphUDF。*/
    public GraphApiBinder addUDF(String name, Class<? extends GraphUDF> udfType);

    /**添加 GraphUDF。*/
    public GraphApiBinder addUDF(String name, GraphUDF graphUdfObject);

    /**添加 GraphUDF。*/
    public GraphApiBinder addUDF(String name, Provider<? extends GraphUDF> udfProvider);

    /**添加 GraphUDF。*/
    public GraphApiBinder addUDF(String name, BindInfo<? extends GraphUDF> udfInfo);
}