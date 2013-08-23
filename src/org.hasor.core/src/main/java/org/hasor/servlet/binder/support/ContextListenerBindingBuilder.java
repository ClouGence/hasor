/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package org.hasor.servlet.binder.support;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContextListener;
import org.hasor.servlet.WebApiBinder.ServletContextListenerBindingBuilder;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.internal.UniqueAnnotations;
/**
 * 
 * @version : 2013-4-17
 * @author 赵永春 (zyc@hasor.net)
 */
class ContextListenerBindingBuilder implements Module {
    /*Filter 定义*/
    private final List<ContextListenerDefinition> listenerDefinitions = new ArrayList<ContextListenerDefinition>();
    //
    public void configure(Binder binder) {
        /*将ListenerDefinition绑定到Guice身上，在正式使用时利用findBindingsByType方法将其找回来。*/
        for (ContextListenerDefinition define : listenerDefinitions)
            binder.bind(ContextListenerDefinition.class).annotatedWith(UniqueAnnotations.create()).toProvider(define);
    }
    public ServletContextListenerBindingBuilder contextListener() {
        return new ServletContextListenerBindingBuilderImpl();
    }
    /*-----------------------------------------------------------------------------------------*/
    /** SessionListenerBindingBuilder接口实现 */
    class ServletContextListenerBindingBuilderImpl implements ServletContextListenerBindingBuilder {
        public void bind(Class<? extends ServletContextListener> listenerKey) {
            bind(Key.get(listenerKey));
        }
        public void bind(Key<? extends ServletContextListener> listenerKey) {
            this.bind(listenerKey, null);
        }
        public void bind(ServletContextListener sessionListener) {
            Key<ServletContextListener> listenerKey = Key.get(ServletContextListener.class, UniqueAnnotations.create());
            bind(listenerKey, sessionListener);
        }
        private void bind(Key<? extends ServletContextListener> listenerKey, ServletContextListener listenerInstance) {
            listenerDefinitions.add(new ContextListenerDefinition(listenerKey, listenerInstance));
        }
    }
}