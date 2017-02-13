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
/**
 * 逻辑器
 * @version : 2013-8-20
 * @author 赵永春(zyc@hasor.net)
 */
public class MatcherDevice<T> implements Matcher<T> {
    private Matcher<T> matcherNode = null;
    protected MatcherDevice(final Matcher<T> matcherNode) {
        this.matcherNode = matcherNode;
    }
    //
    public final boolean matches(final T t) {
        return this.matcherNode.matches(t);
    }
    /**与逻辑。*/
    public MatcherDevice<T> and(final Matcher<T> other) {
        this.matcherNode = new And<T>(this.matcherNode, other);
        return this;
    }
    /**或逻辑。*/
    public MatcherDevice<T> or(final Matcher<T> other) {
        this.matcherNode = new Or<T>(this.matcherNode, other);
        return this;
    }
    /**非逻辑。*/
    public MatcherDevice<T> not() {
        this.matcherNode = new Not<T>(this.matcherNode);
        return this;
    }
    /*-------------------------------------------------------------------------------*/
    private static class And<T> implements Matcher<T> {
        private final Matcher<T> node1;
        private final Matcher<T> node2;
        private And(final Matcher<T> node1, final Matcher<T> node2) {
            this.node1 = Hasor.assertIsNotNull(node1, "delegate1");
            this.node2 = Hasor.assertIsNotNull(node2, "delegate2");
        }
        @Override
        public boolean matches(final T t) {
            return this.node1.matches(t) && this.node2.matches(t);
        }
    }
    private static class Or<T> implements Matcher<T> {
        private final Matcher<T> node1;
        private final Matcher<T> node2;
        private Or(final Matcher<T> node1, final Matcher<T> node2) {
            this.node1 = Hasor.assertIsNotNull(node1, "delegate1");
            this.node2 = Hasor.assertIsNotNull(node2, "delegate2");
        }
        @Override
        public boolean matches(final T t) {
            return this.node1.matches(t) || this.node2.matches(t);
        }
    }
    private static class Not<T> implements Matcher<T> {
        private final Matcher<T> delegate;
        private Not(final Matcher<T> delegate) {
            this.delegate = Hasor.assertIsNotNull(delegate, "delegate");
        }
        @Override
        public boolean matches(final T t) {
            return !this.delegate.matches(t);
        }
        @Override
        public String toString() {
            return "not(" + this.delegate + ")";
        }
    }
}