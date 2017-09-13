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
package net.hasor.core.classcode.matcher;
import net.hasor.core.Hasor;
import net.hasor.core.Matcher;
import net.hasor.utils.ClassUtils;
import net.hasor.utils.MatchUtils;
import net.hasor.utils.MatchUtils.MatchTypeEnum;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
/**
 *
 * @version : 2013-8-20
 * @author 赵永春(zyc@hasor.net)
 */
public class AopMatchers {
    private AopMatchers() {
    }
    //
    public static <T> MatcherDevice<T> createDevice(final Matcher<T> matcher) {
        return new MatcherDevice<T>(matcher);
    }
    /** 匹配任意类型*/
    public static Matcher<Class<?>> anyClass() {
        return new Matcher<Class<?>>() {
            public boolean matches(final Class<?> t) {
                return true;
            }
        };
    }
    /** 匹配任意方法*/
    public static Matcher<Method> anyMethod() {
        return new Matcher<Method>() {
            public boolean matches(final Method t) {
                return true;
            }
        };
    }
    /** 在类型中匹配注解 */
    public static Matcher<Class<?>> annotatedWithClass(final Class<? extends Annotation> annotationType) {
        return new ClassAnnotationOf(annotationType);
    }
    /** 在方法中匹配注解 */
    public static Matcher<Method> annotatedWithMethod(final Class<? extends Annotation> annotationType) {
        return new MethodAnnotationOf(annotationType);
    }
    /** 返回一个匹配器，匹配给定类型的子类（或实现了的接口） */
    public static Matcher<Class<?>> subClassesOf(final Class<?> superclass) {
        return new SubClassesOf(superclass);
    }
    /**将表达式解析为<code>Matcher&lt;Method&gt;</code>。
     * 格式为：<code>&lt;包名&gt;.&lt;类名&gt;</code>，可以使用通配符。*/
    public static Matcher<Class<?>> expressionClass(final String matcherExpression) {
        return new ClassOf(MatchUtils.MatchTypeEnum.Wild, matcherExpression);
    }
    /**
     * 使用表达式配置Aop。
     * <p>例：<pre>格式：&lt;返回值&gt;&nbsp;&lt;类名&gt;.&lt;方法名&gt;(&lt;参数签名列表&gt;)
     *  * *.*()                  匹配：任意无参方法
     *  * *.*(*)                 匹配：任意方法
     *  * *.add*(*)              匹配：任意add开头的方法
     *  * *.add*(*,*)            匹配：任意add开头并且具有两个参数的方法。
     *  * net.test.hasor.*(*)    匹配：包“net.test.hasor”下的任意类，任意方法。
     *  * net.test.hasor.add*(*) 匹配：包“net.test.hasor”下的任意类，任意add开头的方法。
     *  java.lang.String *.*(*)  匹配：任意返回值为String类型的方法。
     * </pre>
     * @param matcherExpression 格式为“<code>&lt;返回值&gt;&nbsp;&lt;类名&gt;.&lt;方法名&gt;(&lt;参数签名列表&gt;)</code>”
     */
    public static Matcher<Method> expressionMethod(final String matcherExpression) {
        return new MethodOf(matcherExpression);
    }
    //
    //
    //
    //
    //
    /**匹配方法*/
    private static class MethodOf implements Matcher<Method> {
        private final String matcherExpression;
        public MethodOf(String matcherExpression) {
            this.matcherExpression = matcherExpression;
        }
        public boolean matches(Method target) {
            String methodStr = ClassUtils.getDescNameWithOutModifiers(target);
            return MatchUtils.wildToRegex(matcherExpression, methodStr, MatchTypeEnum.Wild);
        }
    }
    //
    /**匹配类名*/
    private static class ClassOf implements Matcher<Class<?>> {
        private final MatchTypeEnum matchTypeEnum;
        private final String        matcherExpression;
        public ClassOf(MatchTypeEnum matchTypeEnum, String matcherExpression) {
            this.matchTypeEnum = matchTypeEnum;
            this.matcherExpression = matcherExpression;
        }
        public boolean matches(Class<?> target) {
            return MatchUtils.wildToRegex(matcherExpression, target.getName(), this.matchTypeEnum);
        }
    }
    //
    /**匹配子类*/
    private static class SubClassesOf implements Matcher<Class<?>> {
        private final Class<?> superclass;
        public SubClassesOf(final Class<?> superclass) {
            this.superclass = Hasor.assertIsNotNull(superclass, "superclass");
        }
        public boolean matches(final Class<?> subclass) {
            return this.superclass.isAssignableFrom(subclass);
        }
        public boolean equals(final Object other) {
            return other instanceof SubClassesOf && ((SubClassesOf) other).superclass.equals(this.superclass);
        }
        public int hashCode() {
            return 37 * this.superclass.hashCode();
        }
        public String toString() {
            return "SubClassesOf(" + this.superclass.getSimpleName() + ".class)";
        }
    }
    //
    /**匹配类或类方法上标记的注解。*/
    private static class ClassAnnotationOf implements Matcher<Class<?>> {
        private Class<? extends Annotation> annotationType = null;
        public ClassAnnotationOf(final Class<? extends Annotation> annotationType) {
            this.annotationType = annotationType;
        }
        public boolean matches(final Class<?> matcherType) {
            if (matcherType.isAnnotationPresent(this.annotationType)) {
                return true;
            }
            Method[] m1s = matcherType.getMethods();
            Method[] m2s = matcherType.getDeclaredMethods();
            for (Method m1 : m1s) {
                if (m1.isAnnotationPresent(this.annotationType)) {
                    return true;
                }
            }
            for (Method m2 : m2s) {
                if (m2.isAnnotationPresent(this.annotationType)) {
                    return true;
                }
            }
            return false;
        }
        public boolean equals(final Object other) {
            return other instanceof ClassAnnotationOf && ((ClassAnnotationOf) other).annotationType.equals(this.annotationType);
        }
        public int hashCode() {
            return 37 * this.annotationType.hashCode();
        }
        public String toString() {
            return "ClassAnnotationOf(" + this.annotationType.getSimpleName() + ".class)";
        }
    }
    //
    /**匹配方法上的注解。*/
    private static class MethodAnnotationOf implements Matcher<Method> {
        private Class<? extends Annotation> annotationType = null;
        public MethodAnnotationOf(final Class<? extends Annotation> annotationType) {
            this.annotationType = annotationType;
        }
        public boolean matches(final Method matcherType) {
            if (matcherType.isAnnotationPresent(this.annotationType)) {
                return true;
            }
            if (matcherType.getDeclaringClass().isAnnotationPresent(this.annotationType)) {
                return true;
            }
            return false;
        }
        public boolean equals(final Object other) {
            return other instanceof MethodAnnotationOf && ((MethodAnnotationOf) other).annotationType.equals(this.annotationType);
        }
        public int hashCode() {
            return 37 * this.annotationType.hashCode();
        }
        public String toString() {
            return "MethodAnnotationOf(" + this.annotationType.getSimpleName() + ".class)";
        }
    }
}