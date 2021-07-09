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
package net.hasor.dataql.service;
import net.hasor.dataql.Finder;
import net.hasor.dataql.FragmentProcess;
import net.hasor.utils.ResourcesUtils;
import net.hasor.utils.io.input.AutoCloseInputStream;
import net.hasor.utils.ref.LinkedCaseInsensitiveMap;
import net.hasor.utils.supplier.TypeSupplier;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * 资源加载器
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2019-12-11
 */
public class DefaultFinder implements Finder {
    private final Finder                   parent;
    private final ClassLoader              classLoader;
    private final Map<String, Supplier<?>> fragmentMap      = new LinkedCaseInsensitiveMap<>();
    private final Map<String, Supplier<?>> importPrepareMap = new ConcurrentHashMap<>();

    public DefaultFinder() {
        this(Thread.currentThread().getContextClassLoader(), Finder.DEFAULT);
    }

    public DefaultFinder(ClassLoader classLoader, TypeSupplier typeSupplier) {
        this.classLoader = classLoader;
        this.parent = (typeSupplier != null) ? Finder.TYPE_SUPPLIER.apply(typeSupplier) : Finder.DEFAULT;
    }

    public DefaultFinder(ClassLoader classLoader, Finder parent) {
        this.classLoader = classLoader;
        this.parent = (parent != null) ? parent : Finder.DEFAULT;
    }

    /** 负责处理 <code>import @"/net/hasor/demo.ql" as demo;</code>方式中 ‘/net/hasor/demo.ql’ 资源的加载 */
    @Override
    public InputStream findResource(String resourceName) throws IOException {
        if (this.classLoader != null) {
            return new AutoCloseInputStream(ResourcesUtils.getResourceAsStream(this.classLoader, resourceName));
        } else {
            return new AutoCloseInputStream(ResourcesUtils.getResourceAsStream(resourceName));
        }
    }

    @Override
    public Object findBean(Class<?> beanType) {
        String typeName = beanType.getName();
        if (!this.importPrepareMap.containsKey(typeName)) {
            this.importPrepareMap.put(typeName, () -> {
                return this.parent.findBean(beanType);
            });
        }
        return this.importPrepareMap.get(typeName).get();
    }

    @Override
    public FragmentProcess findFragmentProcess(String fragmentType) {
        Supplier<?> supplier = this.fragmentMap.get(fragmentType);
        FragmentProcess process = null;
        if (supplier != null) {
            process = (FragmentProcess) supplier.get();
        }
        if (process == null) {
            return this.parent.findFragmentProcess(fragmentType);
        }
        return process;
    }

    public void addImport(String name, Class<?> implementation) {
        this.importPrepareMap.put(name, () -> findBean(implementation));
    }

    public void addImport(String name, Supplier<?> provider) {
        this.importPrepareMap.put(name, provider);
    }

    public void addFragmentProcess(String name, Class<? extends FragmentProcess> implementation) {
        this.fragmentMap.put(name, () -> findBean(implementation));
    }

    public void addFragmentProcess(String name, Supplier<? extends FragmentProcess> provider) {
        this.fragmentMap.put(name, provider);
    }
}
