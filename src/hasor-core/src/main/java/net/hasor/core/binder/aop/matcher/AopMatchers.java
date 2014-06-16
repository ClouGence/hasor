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
package net.hasor.core.binder.aop.matcher;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import net.hasor.core.ApiBinder.Matcher;
import net.hasor.core.Hasor;
/**
 * 
 * @version : 2013-8-20
 * @author 赵永春(zyc@hasor.net)
 */
public class AopMatchers {
    private AopMatchers() {}
    //
    public static <T> MatcherDevice<T> createDevice(Matcher<T> matcher) {
        return new MatcherDevice<T>(matcher);
    }
    /** 匹配任意类型*/
    public static Matcher<Class<?>> anyClass() {
        return new Matcher<Class<?>>() {
            public boolean matches(Class<?> t) {
                return true;
            }
        };
    }
    /** 匹配任意方法*/
    public static Matcher<Method> anyMethod() {
        return new Matcher<Method>() {
            public boolean matches(Method t) {
                return true;
            }
        };
    }
    /** 在类型中匹配注解 */
    public static Matcher<Class<?>> annotatedWithClass(Class<? extends Annotation> annotationType) {
        return new ClassMatcherAnnotationType(annotationType);
    }
    /** 在方法中匹配注解 */
    public static Matcher<Method> annotatedWithMethod(Class<? extends Annotation> annotationType) {
        return new MethodMatcherAnnotationType(annotationType);
    }
    /** 返回一个匹配器，匹配给定类型的子类（或实现了的接口） */
    public static Matcher<Class<?>> subClassesOf(Class<?> superclass) {
        return new SubclassesOf(superclass);
    }
    /**将表达式解析为<code>Matcher&lt;Class&gt;</code>。*/
    public static Matcher<Class<?>> expressionClass(String matcherExpression) {
        throw new UnsupportedOperationException();//TODO　暂不支持
    }
    /**将表达式解析为<code>Matcher&lt;Method&gt;</code>。*/
    public static Matcher<Method> expressionMethod(String matcherExpression) {
        throw new UnsupportedOperationException();//TODO　暂不支持
    }//
     //
     //
    /**匹配子类*/
    private static class SubclassesOf implements Matcher<Class<?>> {
        private final Class<?> superclass;
        public SubclassesOf(Class<?> superclass) {
            this.superclass = Hasor.assertIsNotNull(superclass, "superclass");
        }
        public boolean matches(Class<?> subclass) {
            return superclass.isAssignableFrom(subclass);
        }
        public boolean equals(Object other) {
            return other instanceof SubclassesOf && ((SubclassesOf) other).superclass.equals(superclass);
        }
        public int hashCode() {
            return 37 * superclass.hashCode();
        }
        public String toString() {
            return "subclassesOf(" + superclass.getSimpleName() + ".class)";
        }
    }
    /**负责检测匹配。规则：只要类型上标记了某个注解。*/
    private static class ClassMatcherAnnotationType implements Matcher<Class<?>> {
        private Class<? extends Annotation> annotationType = null;
        public ClassMatcherAnnotationType(Class<? extends Annotation> annotationType) {
            this.annotationType = annotationType;
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
    }
    /**负责检测匹配。规则：只要方法上标记了某个注解。*/
    private static class MethodMatcherAnnotationType implements Matcher<Method> {
        private Class<? extends Annotation> annotationType = null;
        public MethodMatcherAnnotationType(Class<? extends Annotation> annotationType) {
            this.annotationType = annotationType;
        }
        public boolean matches(Method matcherType) {
            if (matcherType.isAnnotationPresent(this.annotationType) == true)
                return true;
            if (matcherType.getDeclaringClass().isAnnotationPresent(this.annotationType) == true)
                return true;
            return false;
        }
    }
}