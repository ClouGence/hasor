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
import net.hasor.web.InvokeChain;
import net.hasor.web.InvokerFilter;
/**
 * 验证执行器,线程安全
 * @version : 2016-08-03
 * @author 赵永春 (zyc@hasor.net)
 */
public class ValidInvokerFilter implements InvokerFilter<ValidErrors> {
    @Override
    public void doInvoke(ValidErrors invoker, InvokeChain chain) throws Throwable {
        //
        //.findMethod
        String httpMethod = invoker.getHttpRequest().getMethod();
        httpMethod = httpMethod.toUpperCase();
        //
        // .findVali
        String validName = "";
        ValidDefinition valid = invoker.getAppContext().findBindingBean(validName, ValidDefinition.class);
        //
        // .doValid
        valid.doValid(invoker.getAppContext(), invoker, chain.getParamObjects());
        chain.doNext();
    }
}s