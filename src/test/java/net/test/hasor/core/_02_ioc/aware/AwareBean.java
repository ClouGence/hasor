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
package net.test.hasor.core._02_ioc.aware;
import net.hasor.core.AppContext;
import net.hasor.core.AppContextAware;
import net.hasor.core.BindInfo;
import net.hasor.core.BindInfoAware;
/**
 *
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class AwareBean implements AppContextAware, BindInfoAware {
    private AppContext  appContext;
    private BindInfo<?> bindInfo;
    public boolean called = false;
    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
    }
    public void setBindInfo(BindInfo<?> bindInfo) {
        this.bindInfo = bindInfo;
    }
    public void foo() {
        called = true;
        Object sayBody = this.appContext.findBindingBean("say", String.class);
        System.out.println("bindID=" + bindInfo.getBindID() + " -> " + sayBody);
    }
}