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
import net.hasor.core.AppContext;
import net.hasor.core.TypeSupplier;
import net.hasor.utils.ClassUtils;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.ResourcesUtils;
import net.hasor.utils.supplier.SingleProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 资源加载器
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2019-12-11
 */
public interface Finder {
    /** 默认实现 */
    public static final Finder                                 DEFAULT              = new Finder() {
    };
    /** 通过 TypeSupplier 委托 findBean 的类型创建 */
    public static final Function<TypeSupplier, Finder>         TYPE_SUPPLIER        = typeSupplier -> {
        return new Finder() {
            public Object findBean(Class<?> beanType) {
                if (typeSupplier.test(beanType)) {
                    return typeSupplier.get(beanType);
                } else {
                    return ClassUtils.newInstance(beanType);
                }
            }
        };
    };
    /** 与 ofAppContext 类似不同的是允许 延迟生产 AppContext */
    public static final Function<Supplier<AppContext>, Finder> APP_CONTEXT_SUPPLIER = appContextSupplier -> {
        Supplier<AppContext> single = new SingleProvider<>(appContextSupplier);
        return new Finder() {
            public Object findBean(Class<?> beanType) {
                return single.get().getInstance(beanType);
            }
        };
    };
    /** 通过 AppContext 委托 findBean 的类型创建 */
    public static final Function<AppContext, Finder>           APP_CONTEXT          = appContext -> {
        return APP_CONTEXT_SUPPLIER.apply(() -> appContext);
    };

    /** 负责处理 <code>import @"/net/hasor/demo.ql" as demo;</code>方式中 ‘/net/hasor/demo.ql’ 资源的加载 */
    public default InputStream findResource(String resourceName) throws IOException {
        // .加载资源
        InputStream inputStream = null;
        try {
            inputStream = ResourcesUtils.getResourceAsStream(resourceName);
        } catch (Exception e) {
            throw ExceptionUtils.toRuntime(e, throwable -> new RuntimeException("import compiler failed -> '" + resourceName + "' not found.", throwable));
        }
        return inputStream;
    }

    /** 负责处理 <code>import 'net.hasor.dataql.sdk.CollectionUdfSource' as collect;</code>方式的资源的加载。 */
    public default Object findBean(Class<?> beanType) {
        return ClassUtils.newInstance(beanType);
    }

    public default FragmentProcess findFragmentProcess(String fragmentType) {
        throw new RuntimeException(fragmentType + " fragment undefine.");
    }
}
