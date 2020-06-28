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
import net.hasor.core.Settings;
import net.hasor.core.XmlNode;
import net.hasor.utils.ClassUtils;
import net.hasor.utils.StringUtils;
import net.hasor.web.Invoker;
import net.hasor.web.InvokerCreator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version : 2017-01-10
 * @author 赵永春 (zyc@hasor.net)
 */
class RootInvokerCreater implements InvokerCreator {
    protected Map<Class<?>, InvokerCreator> createrMap = new HashMap<>();
    protected Map<Class<?>, Class<?>>       extMapping = new HashMap<>();

    public RootInvokerCreater(AppContext appContext) throws Exception {
        Settings settings = appContext.getEnvironment().getSettings();
        ClassLoader classLoader = appContext.getClassLoader();
        //
        // .寻找InvokerCreater扩展
        Map<Class<?>, Class<?>> extBinderMap = new HashMap<>();
        XmlNode[] nodeArray = settings.getXmlNodeArray("hasor.invokerCreatorSet.invokerCreator");
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
                List<Class<?>> interfaces = ClassUtils.getAllInterfaces(binderType);
                for (Class<?> faces : interfaces) {
                    extBinderMap.put(faces, binderImpl);
                }
            }
        }
        // .创建扩展(extMapping来建立映射，避免重复创建InvokerCreater)
        for (Map.Entry<Class<?>, Class<?>> ent : extBinderMap.entrySet()) {
            if (this.extMapping.containsKey(ent.getKey())) {
                continue;
            }
            Class<?> createrType = ent.getValue();
            this.extMapping.put(ent.getKey(), createrType);
            if (this.createrMap.containsKey(createrType)) {
                continue;
            }
            InvokerCreator creater = (InvokerCreator) createrType.newInstance();
            this.createrMap.put(createrType, creater);
        }
    }

    //
    @Override
    public Invoker createExt(Invoker dataContext) {
        //
        Map<Class<?>, Object> extMap = new HashMap<>();
        for (Map.Entry<Class<?>, InvokerCreator> ent : this.createrMap.entrySet()) {
            Class<?> extType = ent.getKey();
            InvokerCreator creater = ent.getValue();
            Object extObject = (creater != null) ? creater.createExt(dataContext) : null;
            if (extType != null && extObject != null) {
                extMap.put(extType, extObject);
            }
        }
        //
        Map<Class<?>, Object> supportMap = new HashMap<>();
        supportMap.put(Invoker.class, dataContext);
        for (Map.Entry<Class<?>, Class<?>> ent : this.extMapping.entrySet()) {
            Class<?> key = ent.getKey();
            Class<?> value = ent.getValue();
            Object obj = extMap.get(value);
            if (obj != null) {
                supportMap.put(key, obj);
            }
        }
        //
        ClassLoader classLoader = dataContext.getAppContext().getClassLoader();
        Class<?>[] apiArrays = supportMap.keySet().toArray(new Class<?>[0]);
        return (Invoker) Proxy.newProxyInstance(classLoader, apiArrays, new InvokerCreaterInvocationHandler(supportMap));
    }

    private static class InvokerCreaterInvocationHandler implements InvocationHandler {
        private Map<Class<?>, Object> supportMap;

        public InvokerCreaterInvocationHandler(Map<Class<?>, Object> supportMap) {
            this.supportMap = supportMap;
        }

        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            if (method.getName().equals("toString")) {
                StringBuilder builder = new StringBuilder();
                builder = builder.append("count = ").append(this.supportMap.size()).append(" - [");
                for (Class<?> face : this.supportMap.keySet()) {
                    builder = builder.append(face.getName()).append(",");
                }
                if (builder.charAt(builder.length() - 1) == ',') {
                    builder = builder.deleteCharAt(builder.length() - 1);
                }
                builder.append("]");
                return builder.toString();
            }
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