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
package net.hasor.utils;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

/**
 * 异常工具类
 * @version : 2014年9月25日
 * @author 赵永春 (zyc@hasor.net)
 */
public class ExceptionUtils {
    public static RuntimeException toRuntimeException(Throwable proxy) {
        return toRuntimeException(proxy, throwable -> {
            return new RuntimeException(throwable.getClass().getName() + " - " + throwable.getMessage(), throwable);
        });
    }

    /**将异常包装为 {@link RuntimeException}*/
    public static RuntimeException toRuntimeException(Throwable proxy, Function<Throwable, RuntimeException> conver) {
        if (proxy instanceof InvocationTargetException && ((InvocationTargetException) proxy).getTargetException() != null) {
            proxy = ((InvocationTargetException) proxy).getTargetException();
        }
        if (proxy instanceof RuntimeException) {
            return (RuntimeException) proxy;
        }
        return conver.apply(proxy);
    }

    //
    public static Throwable toRuntimeException(Throwable proxy, Class<?>[] exceptionTypes) throws Throwable {
        if (exceptionTypes != null) {
            for (Class<?> e : exceptionTypes) {
                if (e.isInstance(exceptionTypes)) {
                    return proxy;
                }
            }
        }
        return new RuntimeException(proxy.getClass().getName() + " - " + proxy.getMessage(), proxy);
    }
}