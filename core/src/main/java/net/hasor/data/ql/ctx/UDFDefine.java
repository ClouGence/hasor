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
package net.hasor.data.ql.ctx;
import net.hasor.core.AppContext;
import net.hasor.core.AppContextAware;
import net.hasor.core.BindInfo;
import net.hasor.data.ql.UDF;
import net.hasor.data.ql.Var;

import java.util.Map;
/**
 * DataUDF 函数定义
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class UDFDefine implements UDF, AppContextAware {
    private String                  name;
    private BindInfo<? extends UDF> udfInfo;
    private UDF                     target;
    //
    public UDFDefine(String name, BindInfo<? extends UDF> udfInfo) {
        this.name = name;
        this.udfInfo = udfInfo;
    }
    @Override
    public void setAppContext(AppContext appContext) {
        this.target = appContext.getInstance(this.udfInfo);
    }
    public String getName() {
        return this.name;
    }
    @Override
    public Object call(final Map<String, Var> values) {
        return this.target.call(values);
    }
}