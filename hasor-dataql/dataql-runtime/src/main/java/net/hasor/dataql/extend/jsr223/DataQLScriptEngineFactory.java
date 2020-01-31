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
package net.hasor.dataql.extend.jsr223;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * JSR223
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-10-19
 */
public class DataQLScriptEngineFactory implements ScriptEngineFactory {
    private static final String VERSION       = "2.0";
    private static final String SHORT_NAME    = "dataql";
    private static final String LANGUAGE_NAME = "DataQL";

    @Override
    public String getEngineName() {
        return "DataQL Engine";
    }

    @Override
    public String getEngineVersion() {
        return VERSION;
    }

    @Override
    public String getLanguageName() {
        return LANGUAGE_NAME;
    }

    @Override
    public String getLanguageVersion() {
        return VERSION;
    }

    @Override
    public List<String> getExtensions() {
        return EXTENSIONS;
    }

    @Override
    public List<String> getMimeTypes() {
        return MIME_TYPES;
    }

    @Override
    public List<String> getNames() {
        return NAMES;
    }

    private static final List<String> NAMES;
    private static final List<String> EXTENSIONS;
    private static final List<String> MIME_TYPES;

    static {
        List<String> n = new ArrayList<>(2);
        n.add(SHORT_NAME);
        n.add(LANGUAGE_NAME);
        NAMES = Collections.unmodifiableList(n);
        n = new ArrayList<>(1);
        n.add("dql");
        EXTENSIONS = Collections.unmodifiableList(n);
        n = new ArrayList<>(1);
        n.add("application/x-dataql");
        MIME_TYPES = Collections.unmodifiableList(n);
    }

    public String getMethodCallSyntax(String obj, String method, String... args) {
        throw new UnsupportedOperationException();
    }

    public String getOutputStatement(String toDisplay) {
        throw new UnsupportedOperationException();
    }

    public String getProgram(String... statements) {
        throw new UnsupportedOperationException();
    }

    public Object getParameter(String key) {
        if (ScriptEngine.NAME.equals(key)) {
            return SHORT_NAME;
        } else if (ScriptEngine.ENGINE.equals(key)) {
            return getEngineName();
        } else if (ScriptEngine.ENGINE_VERSION.equals(key)) {
            return VERSION;
        } else if (ScriptEngine.LANGUAGE.equals(key)) {
            return LANGUAGE_NAME;
        } else if (ScriptEngine.LANGUAGE_VERSION.equals(key)) {
            return getEngineVersion();
        } else {
            throw new IllegalArgumentException("Invalid key");
        }
    }

    @Override
    public ScriptEngine getScriptEngine() {
        return new DataQLScriptEngine(this);
    }
}