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
package net.hasor.data.ql.udfs.collection;
import net.hasor.data.ql.UDF;
import net.hasor.data.ql.Var;

import java.util.Collection;
import java.util.Map;
/**
 * 取第一个元素。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-06-09
 */
public class First extends AbstractCollectionUDF implements UDF {
    @Override
    public Object call(Map<String, Var> values) {
        Var var = values.get("list");
        Collection<Object> objects = super.toCollection(var.getValue());
        if (objects.isEmpty()) {
            return null;
        }
        //
        return objects.iterator().next();
    }
}