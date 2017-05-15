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
package net.hasor.core.container;
import net.hasor.core.Matcher;
import net.hasor.core.classcode.aop.AopMatcher;

import java.lang.reflect.Method;
/**
 * 负责根据Class或BindInfo创建Bean。
 * @version : 2015年6月26日
 * @author 赵永春(zyc@hasor.net)
 */
class ClassAopMatcher implements AopMatcher {
    private Matcher<Method> matcherMethod = null;
    public ClassAopMatcher(Matcher<Method> matcherMethod) {
        this.matcherMethod = matcherMethod;
    }
    public boolean matcher(Method target) {
        return this.matcherMethod.matches(target);
    }
}