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
import net.hasor.core.TypeSupplier;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * 通过 ApplicationContextAware 接口来构造 TypeSupplier
 * @version : 2020年02月23日
 * @author 赵永春 (zyc@hasor.net)
 */
public class AbstractTypeSupplierTools extends AbstractEnvironmentAware implements //
        ApplicationContextAware, EnvironmentAware, BeanClassLoaderAware {
    private ApplicationContext applicationContext;
    private ClassLoader        springClassLoader;
    private BuildConfig        buildConfig = new BuildConfig();
    private TypeSupplier       typeSupplier;

    public TypeSupplier getTypeSupplier() {
        return typeSupplier;
    }

    public BuildConfig getBuildConfig() {
        return buildConfig;
    }

    @Override
    public final void setEnvironment(Environment environment) {
        this.buildConfig.envProperties = super.setupEnvironment(environment);
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.typeSupplier = new SpringTypeSupplier(applicationContext);
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public final void setBeanClassLoader(ClassLoader classLoader) {
        this.springClassLoader = classLoader;
    }

    public ClassLoader getSpringClassLoader() {
        return springClassLoader;
    }
}
