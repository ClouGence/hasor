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
package net.hasor.core;
import java.util.function.Predicate;

/**
 * 匹配器
 * @version : 2013-7-10
 * @author 赵永春 (zyc@hasor.net)
 */
@Deprecated
@FunctionalInterface
public interface Matcher<T> extends Predicate<T> {
    /**Returns {@code true} if this matches {@code T}, {@code false} otherwise.*/
    public boolean matches(T target);

    @Override
    default boolean test(T target) {
        return this.matches(target);
    }

    default Predicate<T> toPredicate(Matcher<T> provider) {
        return provider;
    }

    default Matcher<T> fromPredicate(Predicate<T> supplier) {
        return supplier::test;
    }
}