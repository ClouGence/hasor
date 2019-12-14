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
package net.hasor.dataql;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.ResourcesUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * 资源加载器
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2019-12-11
 */
public interface Finder {
    public static final Finder DEFAULT = new Finder() {
    };

    public default InputStream findResource(String resourceName) throws IOException {
        // .加载资源
        InputStream inputStream = null;
        try {
            inputStream = ResourcesUtils.getResourceAsStream(resourceName);
        } catch (Exception e) {
            throw ExceptionUtils.toRuntimeException(e, throwable -> new RuntimeException("import compiler failed -> '" + resourceName + "' not found.", throwable));
        }
        return inputStream;
    }

    public default Object findBean(String beanName) {
        // .确定ClassLoader
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Class c = classLoader.loadClass(Query.class.getName());
            if (c != Query.class) {
                classLoader = Query.class.getClassLoader();
            }
            Class<?> loadClass = classLoader.loadClass(beanName);
            return findBean(loadClass);
        } catch (ClassNotFoundException e) {
            throw ExceptionUtils.toRuntimeException(e, throwable -> new RuntimeException("load Bean failed -> '" + beanName, throwable));
        }
    }

    public default Object findBean(Class<?> beanType) {
        try {
            return beanType.newInstance();
        } catch (Exception e) {
            throw ExceptionUtils.toRuntimeException(e, throwable -> new RuntimeException("load Bean failed -> '" + beanType.getName(), throwable));
        }
    }

    public default FragmentProcess findFragmentProcess(String fragmentType) {
        throw new RuntimeException(fragmentType + " fragment undefine.");
    }
}