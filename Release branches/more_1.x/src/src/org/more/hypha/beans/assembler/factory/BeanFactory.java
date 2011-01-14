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
package org.more.hypha.beans.assembler.factory;
import java.util.Hashtable;
import java.util.Map;
import org.more.hypha.ApplicationContext;
import org.more.hypha.beans.AbstractBeanDefine;
/**
 * 该类负责处理
 * @version 2011-1-14
 * @author 赵永春 (zyc@byshell.org)
 */
public class BeanFactory {
    private ApplicationContext    context       = null;
    private BeanEngine            engine        = null;
    //
    //
    private Map<String, Class<?>> beanTypeCatch = new Hashtable<String, Class<?>>();
    private Map<String, Object>   beanType      = new Hashtable<String, Object>();
    /** */
    public BeanFactory(BeanEngine engine) {
        this.engine = engine;
    }
    public Object getBean(AbstractBeanDefine define, ApplicationContext applicationContext, Object[] objects) {
        return objects;
    };
    public Class<?> getBeanType(AbstractBeanDefine define, ApplicationContext applicationContext) {
        return null;
    };
}