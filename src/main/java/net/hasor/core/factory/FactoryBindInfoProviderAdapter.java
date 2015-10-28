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
package net.hasor.core.factory;
import java.lang.reflect.Method;
import java.util.List;
import net.hasor.core.ApiBinder.Matcher;
import net.hasor.core.info.AopBindInfoAdapter;
import net.hasor.core.info.DefaultBindInfoProviderAdapter;
import org.more.classcode.aop.AopClassConfig;
import org.more.classcode.aop.AopMatcher;
/**
 * 
 * @version : 2015年6月26日
 * @author 赵永春(zyc@hasor.net)
 */
public class FactoryBindInfoProviderAdapter<T> extends DefaultBindInfoProviderAdapter<T> {
    private ClassLoader masterLosder = null;
    public FactoryBindInfoProviderAdapter(Class<T> bindingType, ClassLoader masterLosder) {
        super(bindingType);
        this.masterLosder = masterLosder;
    }
    private AopClassConfig engine = null;s
    /**获取用于创建Bean的 Engine。*/
    public AopClassConfig buildEngine(List<AopBindInfoAdapter> aopList) {
        if (this.engine == null) {
            Class<?> superType = this.getSourceType();
            superType = (superType == null) ? this.getBindType() : superType;
            this.engine = new AopClassConfig(superType, this.masterLosder);
            for (AopBindInfoAdapter aop : aopList) {
                if (aop.getMatcherClass().matches(superType) == false) {
                    continue;
                }
                AopMatcher aopMatcher = new HasorAopMatcher(aop.getMatcherMethod());
                this.engine.addAopInterceptor(aopMatcher, aop);
            }
        }
        return this.engine;
    }
    //
    private static class HasorAopMatcher implements AopMatcher {
        private Matcher<Method> matcherMethod = null;
        public HasorAopMatcher(Matcher<Method> matcherMethod) {
            this.matcherMethod = matcherMethod;
        }
        public boolean matcher(Method target) {
            return this.matcherMethod.matches(target);
        }
    }
}