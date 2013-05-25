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
package org.platform.binder.support;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSessionListener;
import org.platform.binder.ApiBinder.SessionListenerBindingBuilder;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.internal.UniqueAnnotations;
/**
 * 
 * @version : 2013-4-17
 * @author 赵永春 (zyc@byshell.org)
 */
class ListenerBindingBuilder implements Module {
    /*Filter 定义*/
    private final List<ListenerDefinition> listenerDefinitions = new ArrayList<ListenerDefinition>();
    //
    @Override
    public void configure(Binder binder) {
        /*将ListenerDefinition绑定到Guice身上，在正式使用时利用findBindingsByType方法将其找回来。*/
        for (ListenerDefinition define : listenerDefinitions)
            binder.bind(ListenerDefinition.class).annotatedWith(UniqueAnnotations.create()).toProvider(define);
    }
    public SessionListenerBindingBuilder sessionListener() {
        return new SessionListenerBindingBuilderImpl();
    }
    /*-----------------------------------------------------------------------------------------*/
    /** SessionListenerBindingBuilder接口实现 */
    class SessionListenerBindingBuilderImpl implements SessionListenerBindingBuilder {
        @Override
        public void bind(Class<? extends HttpSessionListener> listenerKey) {
            bind(Key.get(listenerKey));
        }
        @Override
        public void bind(Key<? extends HttpSessionListener> listenerKey) {
            this.bind(listenerKey, null);
        }
        @Override
        public void bind(HttpSessionListener sessionListener) {
            Key<HttpSessionListener> listenerKey = Key.get(HttpSessionListener.class, UniqueAnnotations.create());
            bind(listenerKey, sessionListener);
        }
        private void bind(Key<? extends HttpSessionListener> listenerKey, HttpSessionListener listenerInstance) {
            listenerDefinitions.add(new ListenerDefinition(listenerKey, listenerInstance));
        }
    }
}