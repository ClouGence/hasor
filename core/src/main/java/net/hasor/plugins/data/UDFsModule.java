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
package net.hasor.plugins.data;
import net.hasor.core.ApiBinder;
import net.hasor.core.Module;
import net.hasor.plugins.data.collection.First;
import net.hasor.plugins.data.collection.Foreach;
import net.hasor.plugins.data.collection.Last;
import net.hasor.plugins.data.collection.Limit;
/**
 * 提供内置 <code>DataQL</code> UDF 函数。
 * @version : 2017-6-08
 * @author 赵永春 (zyc@byshell.org)
 */
public class UDFsModule implements Module {
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        //
        DataApiBinder dataBinder = apiBinder.tryCast(DataApiBinder.class);
        if (dataBinder == null) {
            return;
        }
        // .内置集合函数
        dataBinder.addUDF("foreach", new Foreach());
        dataBinder.addUDF("first", new First());
        dataBinder.addUDF("last", new Last());
        dataBinder.addUDF("limit", new Limit());
    }
}