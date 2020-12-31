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
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiConsumer;

/**
 * 负责处理 Environment 的数据发现
 * @version : 2020年02月23日
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractEnvironmentAware {
    protected Environment environment;

    public Properties setupEnvironment(Environment environment) {
        this.environment = environment;
        Properties envProperties = new Properties();
        Iterator<PropertySource<?>> propertySourceIterator = ((StandardEnvironment) environment).getPropertySources().iterator();
        while (propertySourceIterator.hasNext()) {
            PropertySource<?> propertySource = propertySourceIterator.next();
            if ("systemProperties".equalsIgnoreCase(propertySource.getName())) {
                continue;// this propertySource in Hasor has same one
            }
            if ("systemEnvironment".equalsIgnoreCase(propertySource.getName())) {
                continue;// this propertySource in Hasor has same one
            }
            Object source = propertySource.getSource();
            if (source instanceof Map) {
                ((Map<?, ?>) source).forEach((BiConsumer<Object, Object>) (key, value) -> {
                    if (key == null || value == null) {
                        return;
                    }
                    envProperties.put(key, value);
                });
            }
        }
        return envProperties;
    }
}
