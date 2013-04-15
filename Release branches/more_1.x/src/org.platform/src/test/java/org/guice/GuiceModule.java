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
package org.guice;
import java.util.Map;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.internal.UniqueAnnotations;
import com.google.inject.matcher.AbstractMatcher;
/**
 * 
 * @version : 2013-4-1
 * @author ÕÔÓÀ´º (zyc@byshell.org)
 */
public class GuiceModule implements Module {
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void configure(Binder binder) {
        //        Map<Key, Class<?>> systemServices = new HashMap<Key, Class<?>>();
        //        //1.×¢²á·þÎñ
        //        Set<Class<?>> servicesSet = ClassUtil.getClassSet("org.guice", Service.class);
        //        for (Class<?> service : servicesSet) {
        //            Service serAnno = service.getAnnotation(Service.class);
        //            Class serviceClass = service;
        //            for (String nameItem : serAnno.value()) {
        //                Key key = Key.get(service, Names.named(nameItem));
        //                ScopedBindingBuilder scoped = binder.bind(key).to(serviceClass);
        //                //                if (service.getAnnotation(System.class) != null) {
        //                scoped.in(Service.class);
        //                systemServices.put(key, serviceClass);
        //                //                }
        //            }
        //        }
        //        //2.×¢²á×÷ÓÃÓò 
        //        binder.bindScope(Service.class, new SysScope(systemServices));
        //3.×¢²áAop 
//        binder.bindInterceptor(new MyM(), new MyM(), new MyMethodInterceptor());
        //
        binder.bind(Faces.class).toInstance(new CopyOfFacesImpl(12));
        //
        binder.bind(Faces.class).annotatedWith(UniqueAnnotations.create()).toInstance(new FacesImpl(1));
        binder.bind(Faces.class).annotatedWith(UniqueAnnotations.create()).toInstance(new FacesImpl(2));
        binder.bind(Faces.class).annotatedWith(UniqueAnnotations.create()).toInstance(new FacesImpl(3));
        binder.bind(Faces.class).annotatedWith(UniqueAnnotations.create()).toInstance(new FacesImpl(4));
    }
}
class SysScope implements Scope {
    private Map<Key, Class<?>> systemServices = null;
    public SysScope(Map<Key, Class<?>> systemServices) {
        this.systemServices = systemServices;
    }
    @Override
    public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
        Class<?> cls = systemServices.get(key);
        return unscoped;
    }
}
class MyM extends AbstractMatcher<Object> {
    @Override
    public boolean matches(Object t) {
        return true;
    }
}
class MyMethodInterceptor implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        return invocation.proceed();
    }
}