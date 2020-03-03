/*
 * Copyright 2002-2005 the original author or authors.
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
package net.hasor.spring;
import net.hasor.core.ApiBinder;
import net.hasor.core.Module;
import net.hasor.core.Provider;
import net.hasor.spring.beans.SpringTypeSupplier;
import org.springframework.context.ApplicationContext;

import java.util.function.Supplier;

/**
 * Spring插件
 * @version : 2020年2月29日
 * @author 赵永春 (zyc@hasor.net)
 */
public interface SpringModule extends Module {
    /** 获取 SpringTypeSupplier */
    public default SpringTypeSupplier springTypeSupplier(ApiBinder apiBinder) {
        Supplier<ApplicationContext> provider = apiBinder.getProvider(ApplicationContext.class);
        return new SpringTypeSupplier(provider);
    }

    /** 使用 Spring getBean(Class) 方式获取Bean。  */
    public default <T> Supplier<T> getSupplierOfType(ApiBinder apiBinder, Class<T> targetType) {
        Supplier<ApplicationContext> provider = apiBinder.getProvider(ApplicationContext.class);
        return (Provider<T>) () -> provider.get().getBean(targetType);
    }

    /** 使用 Spring getBean(String) 方式获取Bean。  */
    public default <T> Supplier<T> getSupplierOfName(ApiBinder apiBinder, String beanName) {
        Supplier<ApplicationContext> provider = apiBinder.getProvider(ApplicationContext.class);
        return (Provider<T>) () -> (T) provider.get().getBean(beanName);
    }
}
