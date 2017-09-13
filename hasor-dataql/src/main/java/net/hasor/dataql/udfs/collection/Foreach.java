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
package net.hasor.dataql.udfs.collection;
import net.hasor.dataql.UDF;
/**
 * 循环遍历函数
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class Foreach extends AbstractCollectionUDF implements UDF {
    @Override
    public Object call(Object[] values) {
        if (values == null || values.length < 1) {
            return null;
        }
        //
        return super.toCollection(values[0]);
    }
}