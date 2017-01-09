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
package net.hasor.web.definition;
import net.hasor.core.AppContext;
import net.hasor.web.Invoker;
import net.hasor.web.InvokerConfig;
import net.hasor.web.InvokerFilter;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
/**
 * Abstract implementation for all servlet module bindings
 * @version : 2013-4-12
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractDefinition implements InvokerFilter {
    private final long                index;
    private final Map<String, String> initParams;
    private final String              pattern;
    private final UriPatternMatcher   patternMatcher;
    private AppContext appContext = null;
    //
    public AbstractDefinition(long index, String pattern, UriPatternMatcher patternMatcher, Map<String, String> initParams) {
        this.index = index;
        if (initParams != null) {
            this.initParams = new HashMap<String, String>(initParams);
        } else {
            this.initParams = new HashMap<String, String>();
        }
        this.pattern = pattern;
        this.patternMatcher = patternMatcher;
    }
    /***/
    public long getIndex() {
        return this.index;
    }
    /** Returns any context params supplied when creating the binding. */
    public Map<String, String> getInitParams() {
        return this.initParams;
    }
    /** Returns the pattern used to match against the binding. */
    public String getPattern() {
        return this.pattern;
    }
    /** Returns the pattern type that this binding was created with. */
    public UriPatternType getUriPatternType() {
        return this.patternMatcher.getPatternType();
    }
    /** Returns true if the given URI will match this binding. */
    public boolean matchesInvoker(Invoker invoker) {
        String url = invoker.getRequestPath();
        return this.patternMatcher.matches(url);
    }
    //
    @Override
    public String toString() {
        return String.format("pattern=%s ,uriPatternType=%s ,type %s ,initParams=%s ", //
                this.getPattern(), this.getUriPatternType(), this.getClass(), this.getInitParams());
    }
    //
    //
    @Override
    public final void init(InvokerConfig config) throws Throwable {
        this.appContext = config.getAppContext();
        Map<String, String> initParams = new HashMap<String, String>();
        Enumeration<String> names = config.getInitParameterNames();
        while (names.hasMoreElements()) {
            String key = names.nextElement();
            String value = config.getInitParameter(key);
            initParams.put(key, value);
        }
        //
        this.getInitParams().putAll(initParams);
        this.getTarget();
    }
    protected AppContext getAppContext() {
        return appContext;
    }
    protected abstract Object getTarget() throws Throwable;
}