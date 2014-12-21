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
package net.hasor.rsf.utils;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 * 
 * @version : 2014年11月17日
 * @author 赵永春(zyc@hasor.net)
 */
public class MethodCacheUtils {
    private static Map<String, Method> methodMap = new ConcurrentHashMap<String, Method>();
    public static Method getServiceMethod(Class<?> serviceType, String methodName, Class<?>[] parameterTypes) {
        StringBuffer key = new StringBuffer(methodName);
        for (Class<?> pt : parameterTypes) {
            key.append(pt.getName() + ";");
        }
        String mKey = key.toString();
        if (methodMap.containsKey(mKey) == false) {
            try {
                Method m = serviceType.getMethod(methodName, parameterTypes);
                methodMap.put(mKey, m);
            } catch (Exception e) {
                methodMap.put(mKey, null);
            }
        }
        return methodMap.get(mKey);
    }
}