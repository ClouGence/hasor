/*
 * Copyright 2008-2009 the original ’‘”¿¥∫(zyc@hasor.net).
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
package net.hasor.core.binder.matcher;
import net.hasor.core.ApiBinder.Matcher;
import net.hasor.core.Hasor;
/**
 * ¬ﬂº≠∆˜
 * @version : 2013-8-20
 * @author ’‘”¿¥∫(zyc@hasor.net)
 */
public class MatcherDevice<T> implements Matcher<T> {
    private Matcher<T> matcherNode = null;
    protected MatcherDevice(Matcher<T> matcherNode) {
        this.matcherNode = matcherNode;
    }
    //
    public final boolean matches(T t) {
        return matcherNode.matches(t);
    }
    /**”Î*/
    public MatcherDevice<T> and(Matcher<T> other) {
        this.matcherNode = new And<T>(this.matcherNode, other);
        return this;
    }
    /**ªÚ*/
    public MatcherDevice<T> or(Matcher<T> other) {
        this.matcherNode = new Or<T>(this.matcherNode, other);
        return this;
    }
    /**∑«*/
    public MatcherDevice<T> not() {
        this.matcherNode = new Not<T>(this.matcherNode);
        return this;
    }
    /*-------------------------------------------------------------------------------*/
    private static class And<T> implements Matcher<T> {
        private final Matcher<T> node1;
        private final Matcher<T> node2;
        private And(Matcher<T> node1, Matcher<T> node2) {
            this.node1 = Hasor.assertIsNotNull(node1, "delegate1");
            this.node2 = Hasor.assertIsNotNull(node2, "delegate2");
        }
        public boolean matches(T t) {
            return this.node1.matches(t) && this.node2.matches(t);
        }
    }
    private static class Or<T> implements Matcher<T> {
        private final Matcher<T> node1;
        private final Matcher<T> node2;
        private Or(Matcher<T> node1, Matcher<T> node2) {
            this.node1 = Hasor.assertIsNotNull(node1, "delegate1");
            this.node2 = Hasor.assertIsNotNull(node2, "delegate2");
        }
        public boolean matches(T t) {
            return this.node1.matches(t) || this.node2.matches(t);
        }
    }
    private static class Not<T> implements Matcher<T> {
        private final Matcher<T> delegate;
        private Not(Matcher<T> delegate) {
            this.delegate = Hasor.assertIsNotNull(delegate, "delegate");
        }
        public boolean matches(T t) {
            return !delegate.matches(t);
        }
        public String toString() {
            return "not(" + delegate + ")";
        }
    }
}