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
package net.test.simple._11_scope;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Provider;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Module;
import net.hasor.core.Scope;
import net.hasor.core.context.StandardAppContext;
import net.test.simple._02_beans.pojo.PojoBean;
import org.junit.Test;
/**
 * 本示列演示如何启动 Hasor 框架。
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class ScopeHasor {
    @Test
    public void testScopeHasor() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>testBeanHasor<<--");
        //1.创建一个标准的 Hasor 容器。
        AppContext appContext = new StandardAppContext();
        appContext.addModule(new Module() {
            public void init(ApiBinder apiBinder) throws Throwable {
                //1.自定义作用域，单例
                apiBinder.defineBean("myBean1").bindType(PojoBean.class).toScope(new MyScope(true));
                //2.自定义作用域，非单例
                apiBinder.defineBean("myBean2").bindType(PojoBean.class).toScope(new MyScope(false));
            }
            public void start(AppContext appContext) throws Throwable {
                // TODO Auto-generated method stub
            }
        });
        appContext.start();//启动 Hasor 容器，启动过程会初始化所有模块和插件。
        //
        //
        //
        PojoBean myBean = null;
        myBean = appContext.getBean("myBean1");
        System.out.println(myBean.getName() + myBean);
        myBean = appContext.getBean("myBean1");
        System.out.println(myBean.getName() + myBean);
        //
        myBean = appContext.getBean("myBean2");
        System.out.println(myBean.getName() + myBean);
        myBean = appContext.getBean("myBean2");
        System.out.println(myBean.getName() + myBean);
    }
}
class MyScope implements Scope {
    private Map<Object, Provider<?>> scopeMap  = new HashMap<Object, Provider<?>>();
    private boolean                  singleton = false;
    public MyScope(boolean singleton) {
        this.singleton = singleton;
    }
    public <T> Provider<T> scope(Object key, final Provider<T> provider) {
        Provider<?> returnData = this.scopeMap.get(key);
        if (provider != null) {
            returnData = new Provider<T>() {
                private T instance;
                public T get() {
                    if (singleton == false)
                        return provider.get();
                    //
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