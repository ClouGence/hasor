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
package net.hasor.core.context;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.core.Hasor;
import net.hasor.core.Scope;
import net.hasor.core.spi.CollectScopeChainSpi;
import net.hasor.test.core.basic.pojo.PojoBean;
import net.hasor.utils.ArrayUtils;
import org.junit.Test;

import javax.inject.Singleton;
import java.util.function.Supplier;

public class DefaultSingletonTest {
    @Test
    public void builderTest1() throws Throwable {
        AppContext appContext = Hasor.create().build(apiBinder -> {
            // 为每一个 apiBinder 声明的对象都设置单例
            apiBinder.bindSpiListener(CollectScopeChainSpi.class, new CollectScopeChainSpi() {
                public Supplier<Scope>[] collectScope(BindInfo<?> bindInfo, AppContext appContext, Supplier<Scope>[] suppliers) {
                    return ArrayUtils.add(suppliers, appContext.findScope(Singleton.class));
                }

                public Supplier<Scope>[] collectScope(Class<?> targetType, AppContext appContext, Supplier<Scope>[] suppliers) {
                    return ArrayUtils.add(suppliers, appContext.findScope(Singleton.class));
                }
            });
        });
        //
        PojoBean pojoBean1 = appContext.getInstance(PojoBean.class);
        PojoBean pojoBean2 = appContext.getInstance(PojoBean.class);
        assert pojoBean1 == pojoBean2;
        //
        appContext = Hasor.create().build();
        pojoBean1 = appContext.getInstance(PojoBean.class);
        pojoBean2 = appContext.getInstance(PojoBean.class);
        assert pojoBean1 != pojoBean2;
    }
}

class MyCollectScopeListener implements CollectScopeChainSpi {
    public Supplier<Scope>[] collectScope(BindInfo<?> bindInfo, AppContext appContext, Supplier<Scope>[] suppliers) {
        return ArrayUtils.add(suppliers, appContext.findScope(Singleton.class));
    }

    public Supplier<Scope>[] collectScope(Class<?> targetType, AppContext appContext, Supplier<Scope>[] suppliers) {
        return ArrayUtils.add(suppliers, appContext.findScope(Singleton.class));
    }
}