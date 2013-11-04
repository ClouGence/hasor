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
package net.hasor.plugins.aop.matchers;
import net.hasor.Hasor;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;
/**
 * ¬ﬂº≠∆˜
 * @version : 2013-8-20
 * @author ’‘”¿¥∫(zyc@hasor.net)
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class MatcherDevice implements Matcher {
    private Matcher matcherNode = null;
    public final boolean matches(Object t) {
        return matcherNode.matches(t);
    }
    /**”Î*/
    public MatcherDevice and(Matcher other) {
        this.matcherNode = this.matcherNode.and(other);
        return this;
    }
    /**ªÚ*/
    public MatcherDevice or(Matcher other) {
        this.matcherNode = this.matcherNode.or(other);
        return this;
    }
    /**∑«*/
    public MatcherDevice not() {
        this.matcherNode = new Not(this.matcherNode);
        return this;
    }
    /*-------------------------------------------------------------------------------*/
    private static class Not extends AbstractMatcher {
        final Matcher delegate;
        private Not(Matcher delegate) {
            this.delegate = Hasor.assertIsNotNull(delegate, "delegate");
        }
        public boolean matches(Object t) {
            return !delegate.matches(t);
        }
        public String toString() {
            return "not(" + delegate + ")";
        }
    }
}