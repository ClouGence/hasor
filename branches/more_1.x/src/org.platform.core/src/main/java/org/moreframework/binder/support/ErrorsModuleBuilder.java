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
package org.moreframework.binder.support;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.moreframework.binder.ErrorHook;
import org.moreframework.binder.ApiBinder.ErrorBindingBuilder;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.internal.UniqueAnnotations;
/**
 * 用于处理ServletBindingBuilder接口对象的创建
 * @version : 2013-4-12
 * @author 赵永春 (zyc@byshell.org)
 */
class ErrorsModuleBuilder implements Module {
    /*Error 定义*/
    private final List<ErrorDefinition> errorDefinitions = new ArrayList<ErrorDefinition>();
    //
    public ErrorBindingBuilder errorTypes(List<Class<? extends Throwable>> errorTypes) {
        return new Type_ServletErrorBindingBuilder(errorTypes);
    }
    @Override
    public void configure(Binder binder) {
        /*将ServletErrorDefinition绑定到Guice身上，在正式使用时利用findBindingsByType方法将其找回来。*/
        for (ErrorDefinition define : errorDefinitions)
            binder.bind(ErrorDefinition.class).annotatedWith(UniqueAnnotations.create()).toProvider(define);
    }
    /*-----------------------------------------------------------------------------------------*/
    static abstract class AbstractServletErrorBindingBuilder implements ErrorBindingBuilder {
        @Override
        public void bind(Class<? extends ErrorHook> errorKey) {
            bind(Key.get(errorKey));
        }
        @Override
        public void bind(Key<? extends ErrorHook> errorKey) {
            bind(errorKey, new HashMap<String, String>());
        }
        @Override
        public void bind(ErrorHook errorHook) {
            bind(errorHook, new HashMap<String, String>());
        }
        @Override
        public void bind(Class<? extends ErrorHook> errorKey, Map<String, String> initParams) {
            bind(Key.get(errorKey), initParams);
        }
        @Override
        public void bind(Key<? extends ErrorHook> errorKey, Map<String, String> initParams) {
            bind(errorKey, initParams, null);
        }
        @Override
        public void bind(ErrorHook errorHook, Map<String, String> initParams) {
            Key<ErrorHook> servletKey = Key.get(ErrorHook.class, UniqueAnnotations.create());
            bind(servletKey, initParams, errorHook);
        }
        protected abstract void bind(Key<? extends ErrorHook> errorHookKey, Map<String, String> initParams, ErrorHook errorHook);
    }
    class Type_ServletErrorBindingBuilder extends AbstractServletErrorBindingBuilder {
        private final List<Class<? extends Throwable>> errorTypes;
        public Type_ServletErrorBindingBuilder(List<Class<? extends Throwable>> errorTypes) {
            this.errorTypes = errorTypes;
        }
        @Override
        protected void bind(Key<? extends ErrorHook> errorHookKey, Map<String, String> initParams, ErrorHook errorHook) {
            for (Class<? extends Throwable> errorType : errorTypes)
                errorDefinitions.add(new ErrorDefinition(errorType, errorHookKey, initParams, errorHook));
        }
    }
    /*--*/
}