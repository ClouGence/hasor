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
package net.hasor.plugins.aop.matchers;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import net.hasor.core.Hasor;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;
/**
 * 
 * @version : 2013-8-20
 * @author 赵永春(zyc@hasor.net)
 */
public class AopMatchers {
    private AopMatchers() {}
    //
    /** 匹配任意类型*/
    public static Matcher<Class<?>> anyClass() {
        return new AbstractMatcher<Class<?>>() {
            public boolean matches(Class<?> t) {
                return true;
            }
        };
    }
    /** 匹配任意方法*/
    public static Matcher<Method> anyMethod() {
        return new AbstractMatcher<Method>() {
            public boolean matches(Method t) {
                return true;
            }
        };
    }
    /** 在（类型、方法）中匹配注解 */
    public static Matcher<Object> annotatedWith(Class<? extends Annotation> annotationType) {
        return new MatcherAnnotationType(annotationType);
    }
    /** 返回一个匹配器，匹配给定类型的子类（或实现了的接口） */
    public static Matcher<Class<?>> subClassesOf(Class<?> superclass) {
        return new SubclassesOf(superclass);
    }
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //    /**
    //    * Returns a matcher which matches elements (methods, classes, etc.)
    //    * with a given annotation.
    //    */
    //    public static Matcher<AnnotatedElement> annotatedWith(final Annotation annotation) {
    //        return Matchers.annotatedWith(annotation);
    //    }
    //    /** Returns a matcher which matches objects equal to the given object. */
    //    public static Matcher<Object> only(Object value) {
    //        return Matchers.only(value);
    //    }
    //    /**
    //    * Returns a matcher which matches only the given object.
    //    */
    //    public static Matcher<Object> identicalTo(final Object value) {
    //        return Matchers.identicalTo(value);
    //    }
    //    /**
    //    * Returns a matcher which matches classes in the given package. Packages are specific to their
    //    * classloader, so classes with the same package name may not have the same package at runtime.
    //    */
    //    public static Matcher<Class> inPackage(final Package targetPackage) {
    //        return Matchers.inPackage(targetPackage);
    //    }
    //    /**
    //    * Returns a matcher which matches classes in the given package and its subpackages. Unlike
    //    * {@link #inPackage(Package) inPackage()}, this matches classes from any classloader.
    //    * @since 2.0
    //    */
    //    public static Matcher<Class> inSubpackage(final String targetPackageName) {
    //        return Matchers.inSubpackage(targetPackageName);
    //    }
    //    /** Returns a matcher which matches methods with matching return types. */
    //    public static Matcher<Method> returns(final Matcher<? super Class<?>> returnType) {
    //        return Matchers.returns(returnType);
    //    }
    private static class SubclassesOf extends AbstractMatcher<Class<?>> implements Serializable {
        private static final long serialVersionUID = 0;
        private final Class<?>    superclass;
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
}