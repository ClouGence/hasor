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
package net.hasor.spring.boot;
import net.hasor.core.AppContext;
import net.hasor.core.DimModule;
import net.hasor.core.Hasor;
import net.hasor.core.exts.aop.Matchers;
import net.hasor.spring.beans.AbstractTypeSupplierToos;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Properties;

@Configuration
public class HasorBasicConfiguration extends AbstractTypeSupplierToos implements InitializingBean, ApplicationContextAware, EnvironmentAware {
    private AppContext         appContext;
    private ApplicationContext applicationContext;
    private Properties         properties;

    @Override
    public final void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        super.setApplicationContext(applicationContext);
        this.applicationContext = applicationContext;
    }

    @Override
    public final void setEnvironment(Environment environment) {
        this.properties = super.setupEnvironment(environment);
    }

    @Override
    public final void afterPropertiesSet() {
        this.appContext = this.createAppContext(null, this.applicationContext);
    }

    protected ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    protected Properties getEnvironmentProperties() {
        return properties;
    }

    protected AppContext createAppContext(Object envObject, ApplicationContext applicationContext) {
        Hasor buildHasor = Hasor.create(envObject);
        Properties environmentProperties = getEnvironmentProperties();
        if (environmentProperties != null) {
            environmentProperties.forEach((k, v) -> {
                buildHasor.addVariable(k.toString(), v.toString());
            });
        }
        return buildHasor.build(apiBinder -> {
            apiBinder.loadModule(apiBinder.findClass(DimModule.class), Matchers.anyClass(), getTypeSupplier());
        });
    }

    @Bean
    public AppContext appContext() {
        return this.appContext;
    }
}
