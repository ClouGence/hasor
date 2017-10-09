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
package net.hasor.plugins.spring;
import net.hasor.core.Provider;
import org.springframework.context.ApplicationContext;
/**
 * 包装来自 Spring 的 Bean。
 *
 * @version : 2016年2月15日
 * @author 赵永春 (zyc@hasor.net)
 */
public class SpringBean<T> implements Provider<T> {
    private String                       beanID;
    private Provider<ApplicationContext> applicationContext;
    //
    public SpringBean(String beanID, Provider<ApplicationContext> applicationContext) {
        this.beanID = beanID;
        this.applicationContext = applicationContext;
    }
    @Override
    public T get() {
        return (T) applicationContext.get().getBean(this.beanID);
    }
}