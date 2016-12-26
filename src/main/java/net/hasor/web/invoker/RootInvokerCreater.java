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
package net.hasor.web.invoker;
import net.hasor.core.AppContext;
import net.hasor.core.EventListener;
import net.hasor.core.Settings;
import net.hasor.core.XmlNode;
import net.hasor.web.DataContext;
import net.hasor.web.InvokerCreater;
import org.more.util.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * @version : 2013-6-5
 * @author 赵永春 (zyc@hasor.net)
 */
class RootInvokerCreater implements InvokerCreater, EventListener<AppContext> {
    private AtomicBoolean                 inited     = new AtomicBoolean(false);
    private Map<Class<?>, InvokerCreater> createrMap = new HashMap<Class<?>, InvokerCreater>();
    //
    @Override
    public void onEvent(String event, AppContext appContext) throws Throwable {
        if (!this.inited.compareAndSet(false, true)) {
            return;/*避免被初始化多次*/
        }
        //
        Settings settings = appContext.getEnvironment().getSettings();
        ClassLoader classLoader = appContext.getClassLoader();
        //
        // .寻找InvokerCreater扩展
        Map<Class<?>, Class<?>> extBinderMap = new HashMap<Class<?>, Class<?>>();
        XmlNode[] nodeArray = settings.getXmlNodeArray("hasor.invokerCreaterSet.invokerCreater");
        if (nodeArray != null && nodeArray.length > 0) {
            for (XmlNode atNode : nodeArray) {
                if (atNode == null) {
                    continue;
                }
                String binderTypeStr = atNode.getAttribute("type");
                String binderImplStr = atNode.getText();
                if (StringUtils.isBlank(binderTypeStr) || StringUtils.isBlank(binderImplStr)) {
                    continue;
                }
                //
                Class<?> binderType = classLoader.loadClass(binderTypeStr);
                Class<?> binderImpl = classLoader.loadClass(binderImplStr);
                if (!binderType.isInterface()) {
                    continue;
                }
                //
                extBinderMap.put(binderType, binderImpl);
            }
        }
        // .创建扩展
        for (Map.Entry<Class<?>, Class<?>> ent : extBinderMap.entrySet()) {
            InvokerCreater creater = (InvokerCreater) ent.getValue().newInstance();
            this.createrMap.put(ent.getKey(), creater);
        }
    }
    //
    @Override
    public Object create(DataContext dataContext) {
        Map<Class<?>, Object> supportMap = new HashMap<Class<?>, Object>();
        for (Map.Entry<Class<?>, InvokerCreater> ent : this.createrMap.entrySet()) {
            Class<?> extType = ent.getKey();
            InvokerCreater creater = ent.getValue();
            Object extObject = (creater != null) ? creater.create(dataContext) : null;
            if (extType != null && extObject != null) {
                supportMap.put(extType, extObject);
            }
        }
        //
        ClassLoader classLoader = dataContext.getAppContext().getClassLoader();
        supportMap.put(DataContext.class, dataContext);
        Class<?>[] apiArrays = supportMap.keySet().toArray(new Class<?>[supportMap.size()]);
        return Proxy.newProxyInstance(classLoader, apiArrays, new InvokerCreaterInvocationHandler(supportMap));
    }
    private static class InvokerCreaterInvocationHandler implements InvocationHandler {
        private Map<Class<?>, Object> supportMap;
        public InvokerCreaterInvocationHandler(Map<Class<?>, Object> supportMap) {
            this.supportMap = supportMap;
        }
        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            //
            Class<?> declaringClass = method.getDeclaringClass();
            Object target = this.supportMap.get(declaringClass);
            if (target == null) {
                throw new UnsupportedOperationException("this method is not support -> " + method);
            }
            //
            try {
                return method.invoke(target, args);
            } catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }
    }
}