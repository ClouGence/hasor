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
package net.hasor.dataql.binder;
import net.hasor.core.AppContext;
import net.hasor.core.AppContextAware;
import net.hasor.core.BindInfo;
import net.hasor.dataql.Option;
import net.hasor.dataql.UDF;
/**
 * UDF 函数定义
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
class DefineUDF implements UDF, AppContextAware {
    private String                  parentName;
    private String                  name;
    private BindInfo<? extends UDF> udfInfo;
    private UDF                     target;
    //
    public DefineUDF(String parentName, String name, BindInfo<? extends UDF> udfInfo) {
        this.parentName = parentName;
        this.name = name;
        this.udfInfo = udfInfo;
    }
    @Override
    public void setAppContext(AppContext appContext) {
        this.target = appContext.getInstance(this.udfInfo);
    }
    public String getParentName() {
        return this.parentName;
    }
    public String getName() {
        return this.name;
    }
    @Override
    public Object call(Object[] values, Option readOnly) throws Throwable {
        return this.target.call(values, readOnly);
    }
}