/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.test.simple.core._04_scope;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.core.Provider;
import net.hasor.core.Scope;
import net.test.simple.core._03_beans.pojo.PojoBean;
import org.junit.Test;
/**
 * 本示列演示如何使用 Hasor 的Scope隔离Bean。
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class ScopeTest {
    @Test
    public void scopeTest() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>scopeTest<<--");
        //1.创建一个标准的 Hasor 容器。
        AppContext appContext = Hasor.createAppContext(new Module() {
            public void loadModule(ApiBinder apiBinder) throws Throwable {
                MyScope myScope1 = new MyScope();
                //
                apiBinder.bindType(PojoBean.class).nameWith("myBean1").toScope(myScope1);
            }
        });
        //
        PojoBean myBean = null;
        myBean = appContext.findBindingBean("myBean1", PojoBean.class);
        System.out.println("Scope 1 : " + myBean.getName() + myBean);
        myBean = appContext.findBindingBean("myBean1", PojoBean.class);
        System.out.println("Scope 1 : " + myBean.getName() + myBean);
    }
}
/**一个自定义 Scope ，实现了Scope内的单例.*/
class MyScope implements Scope {
    private Map<Object, Provider<?>> scopeMap = new HashMap<Object, Provider<?>>();
    public <T> Provider<T> scope(Object key, final Provider<T> provider) {
        Provider<?> returnData = this.scopeMap.get(key);
        if (provider != null) {
            returnData = new Provider<T>() {
                private T instance;
                public T get() {
                    if (instance == null)
                        this.instance = provider.get();
                    return instance;
                }
            };
            this.scopeMap.put(key, returnData);
        }
        return (Provider<T>) returnData;
    }
}