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
package org.test.aop;
import org.aopalliance.intercept.MethodInterceptor;
import org.hasor.context.ApiBinder;
import org.hasor.context.ModuleSettings;
import org.hasor.context.anno.Module;
import org.hasor.context.anno.support.AnnoSupportModule;
import org.hasor.context.module.AbstractHasorModule;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;
@Module
public class AopModule extends AbstractHasorModule {
    public void configuration(ModuleSettings info) {
        info.afterMe(AnnoSupportModule.class);
    }
    public void init(ApiBinder apiBinder) {
        //ÅÅ³ýËùÓÐÀ¹½ØÆ÷
        Matcher m = Matchers.not(Matchers.subclassesOf(MethodInterceptor.class));
        apiBinder.getGuiceBinder().bindInterceptor(m, Matchers.any(), new AopInterceptor_3());
        //apiBinder.getGuiceBinder().bindInterceptor(Matchers.annotatedWith(Bean.class), Matchers.any(), new AopInterceptor_3());
    }
}