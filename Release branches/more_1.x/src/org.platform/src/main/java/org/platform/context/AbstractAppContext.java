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
package org.platform.context;
import java.util.HashMap;
import java.util.Map;
import org.platform.Assert;
import org.platform.binder.BeanInfo;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
/**
 * {@link AppContext}接口的实现类。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractAppContext extends AppContext {
    private Injector              guice       = null;
    private Map<String, BeanInfo> beanInfoMap = null;
    protected AbstractAppContext(Injector guice) {
        this.guice = guice;
        Assert.isNotNull(guice);
    }
    @Override
    public Injector getGuice() {
        return this.guice;
    }
    @Override
    public <T> Class<T> getBeanType(String name) {
        if (this.beanInfoMap == null)
            this.collectBeanInfos(this.getGuice());
        BeanInfo info = this.beanInfoMap.get(name);
        if (info != null)
            return (Class<T>) info.getBeanType();
        return null;
    }
    @Override
    public String[] getBeanNames() {
        if (this.beanInfoMap == null)
            this.collectBeanInfos(this.getGuice());
        return this.beanInfoMap.values().toArray(new String[this.beanInfoMap.size()]);
    }
    @Override
    public BeanInfo getBeanInfo(String name) {
        if (this.beanInfoMap == null)
            this.collectBeanInfos(this.getGuice());
        return this.beanInfoMap.get(name);
    }
    private void collectBeanInfos(Injector injector) {
        this.beanInfoMap = new HashMap<String, BeanInfo>();
        TypeLiteral<BeanInfo> INFO_DEFS = TypeLiteral.get(BeanInfo.class);
        for (Binding<BeanInfo> entry : injector.findBindingsByType(INFO_DEFS)) {
            BeanInfo beanInfo = entry.getProvider().get();
            this.beanInfoMap.put(beanInfo.getName(), beanInfo);
        }
    }
}