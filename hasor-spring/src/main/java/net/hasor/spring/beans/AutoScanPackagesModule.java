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
package net.hasor.spring.beans;
import net.hasor.core.ApiBinder;
import net.hasor.core.DimModule;
import net.hasor.core.Module;
import net.hasor.core.TypeSupplier;
import net.hasor.core.exts.aop.Matchers;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Set;

/**
 * 负责处理 h:loadModule 标签上 autoScan 和 scanPackages 两个属性
 * @version : 2020年02月23日
 * @author 赵永春 (zyc@hasor.net)
 */
public class AutoScanPackagesModule extends AbstractTypeSupplierTools implements Module, ApplicationContextAware {
    protected static Logger   logger             = LoggerFactory.getLogger(AutoScanPackagesModule.class);
    private          String[] loadModulePackages = null;

    public AutoScanPackagesModule(String[] packages) {
        this.loadModulePackages = packages;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        super.setApplicationContext(applicationContext);
    }

    @Override
    public void loadModule(ApiBinder apiBinder) {
        if (loadModulePackages == null) {
            this.loadModulePackages = apiBinder.getEnvironment().getSpanPackage();
        }
        //
        TypeSupplier typeSupplier = this.getTypeSupplier().beforeOther(new TypeSupplier() {
            @Override
            public <T> T get(Class<? extends T> targetType) {
                try {
                    return targetType.newInstance();
                } catch (Exception e) {
                    throw ExceptionUtils.toRuntimeException(e);
                }
            }
        });
        //
        logger.info("loadModule autoScan='true' scanPackages=" + StringUtils.join(this.loadModulePackages, ","));
        Set<Class<?>> classSet = apiBinder.findClass(DimModule.class, loadModulePackages);
        apiBinder.loadModule(classSet, Matchers.anyClass(), typeSupplier);
    }
}
