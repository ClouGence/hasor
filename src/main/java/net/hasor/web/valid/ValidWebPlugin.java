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
package net.hasor.web.valid;
import net.hasor.web.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
/**
 * 表单验证器插件
 * @version : 2016-08-03
 * @author 赵永春 (zyc@hasor.net)
 */
public class ValidWebPlugin extends WebModule implements WebPlugin, MappingSetup {
    private Map<Method, ValidDefinition> validMapping = null;
    //
    @Override
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        apiBinder.addPlugin(this);
        apiBinder.addSetup(this);
        this.validMapping = new HashMap<Method, ValidDefinition>();
    }
    @Override
    public void setup(MappingData mappingData) {
        Method[] methods = mappingData.getMethods();
        for (Method m : methods) {
            this.validMapping.put(m, new ValidDefinition(m));
        }
    }
    @Override
    public void beforeFilter(Invoker invoker, InvokerData define) {
        //
        ValidDefinition valid = this.validMapping.get(define.targetMethod());
        if (valid == null || !valid.isEnable() || !(invoker instanceof ValidInvoker)) {
            return;
        }
        //
        ValidInvoker errors = (ValidInvoker) invoker;
        Object[] resolveParams = define.getParameters();
        valid.doValid(invoker.getAppContext(), errors, resolveParams);
    }
    @Override
    public void afterFilter(Invoker invoker, InvokerData define) {
        //
    }
}