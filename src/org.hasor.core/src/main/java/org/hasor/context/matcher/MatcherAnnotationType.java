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
package org.hasor.context.matcher;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import com.google.inject.matcher.AbstractMatcher;
/*负责检测匹配。规则：只要类型或方法上标记了@Before。*/
class MatcherAnnotationType extends AbstractMatcher<Object> {
    private Class<? extends Annotation> annotationType = null;
    public MatcherAnnotationType(Class<? extends Annotation> annotationType) {
        this.annotationType = annotationType;
    }
    public boolean matches(Object type) {
        if (type instanceof Class<?>)
            return this.matches((Class<?>) type);
        if (type instanceof Method)
            return this.matches((Method) type);
        return false;
    }
    public boolean matches(Class<?> matcherType) {
        if (matcherType.isAnnotationPresent(this.annotationType) == true)
            return true;
        Method[] m1s = matcherType.getMethods();
        Method[] m2s = matcherType.getDeclaredMethods();
        for (Method m1 : m1s) {
            if (m1.isAnnotationPresent(this.annotationType) == true)
                return true;
        }
        for (Method m2 : m2s) {
            if (m2.isAnnotationPresent(this.annotationType) == true)
                return true;
        }
        return false;
    }
    public boolean matches(Method matcherType) {
        if (matcherType.isAnnotationPresent(this.annotationType) == true)
            return true;
        if (matcherType.getDeclaringClass().isAnnotationPresent(this.annotationType) == true)
            return true;
        return false;
    }
}