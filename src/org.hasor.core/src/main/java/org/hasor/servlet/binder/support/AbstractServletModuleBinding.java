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
package org.hasor.servlet.binder.support;
import java.util.Map;
/**
 * Abstract implementation for all servlet module bindings
 * @version : 2013-4-12
 * @author ’‘”¿¥∫ (zyc@hasor.net)
 */
class AbstractServletModuleBinding {
    private final Map<String, String> initParams;
    private final String              pattern;
    private final UriPatternMatcher   patternMatcher;
    public AbstractServletModuleBinding(Map<String, String> initParams, String pattern, UriPatternMatcher patternMatcher) {
        this.initParams = initParams;
        this.pattern = pattern;
        this.patternMatcher = patternMatcher;
    }
    /** Returns any context params supplied when creating the binding. */
    public Map<String, String> getInitParams() {
        return this.initParams;
    }
    /** Returns the pattern used to match against the binding. */
    public String getPattern() {
        return pattern;
    }
    /** Returns the pattern type that this binding was created with. */
    public UriPatternType getUriPatternType() {
        return patternMatcher.getPatternType();
    }
    /** Returns true if the given URI will match this binding. */
    public boolean matchesUri(String uri) {
        return patternMatcher.matches(uri);
    }
}