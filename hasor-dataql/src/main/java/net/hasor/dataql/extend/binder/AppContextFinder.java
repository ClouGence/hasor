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
package net.hasor.dataql.extend.binder;
import net.hasor.core.AppContext;
import net.hasor.dataql.Finder;
import net.hasor.dataql.FragmentProcess;
import net.hasor.dataql.Query;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.ResourcesUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * 通过 AppContext 进行资源加载。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
class AppContextFinder implements Finder {
    private AppContext appContext;

    public AppContextFinder(AppContext appContext) {
        this.appContext = appContext;
    }

    public InputStream findResource(String resourceName) throws IOException {
        InputStream inputStream = null;
        try {
            ClassLoader classLoader = appContext.getEnvironment().getClassLoader();
            if (classLoader != null) {
                resourceName = ResourcesUtils.formatResource(resourceName);
                inputStream = classLoader.getResourceAsStream(resourceName);
            } else {
                inputStream = ResourcesUtils.getResourceAsStream(resourceName);
            }
        } catch (Exception e) {
            String finalResourceName = resourceName;
            throw ExceptionUtils.toRuntimeException(e, throwable -> new RuntimeException("import compiler failed -> '" + finalResourceName + "' not found.", throwable));
        }
        return inputStream;
    }

    public Object findBean(String beanName) {
        try {
            ClassLoader classLoader = appContext.getEnvironment().getClassLoader();
            if (classLoader == null) {
                classLoader = Thread.currentThread().getContextClassLoader();
            }
            Class c = classLoader.loadClass(Query.class.getName());
            if (c != Query.class) {
                classLoader = Query.class.getClassLoader();
            }
            Class<?> loadClass = classLoader.loadClass(beanName);
            return findBean(loadClass);
        } catch (ClassNotFoundException e) {
            return appContext.getInstance(beanName);
        }
    }

    public Object findBean(Class<?> beanType) {
        return appContext.getInstance(beanType);
    }

    @Override
    public FragmentProcess findFragmentProcess(String fragmentType) {
        return this.appContext.findBindingBean(fragmentType, FragmentProcess.class);
    }
}