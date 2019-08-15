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
package net.hasor.dataql.jsr223;
import net.hasor.dataql.Option;
import net.hasor.dataql.Query;
import net.hasor.dataql.UdfManager;
import net.hasor.dataql.UdfSource;
import net.hasor.dataql.domain.compiler.QIL;
import net.hasor.dataql.domain.compiler.QueryCompiler;
import net.hasor.dataql.domain.parser.ParseException;
import net.hasor.dataql.runtime.OptionSet;
import net.hasor.dataql.udf.SimpleUdfManager;
import net.hasor.utils.io.IOUtils;

import javax.script.*;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.List;

/**
 * JSR223 引擎机制的实现。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-10-19
 */
public class DataQLScriptEngine extends AbstractScriptEngine implements ScriptEngine, Compilable, UdfManager, Option {
    private final OptionSet                 optionSet = new OptionSet();
    private final DataQLScriptEngineFactory engineFactory;
    private       ClassLoader               loader;
    private final UdfManager                udfManager;

    public DataQLScriptEngine(DataQLScriptEngineFactory engineFactory) {
        this.engineFactory = engineFactory;
        this.loader = getParentLoader();
        this.udfManager = new SimpleUdfManager();
    }

    public ClassLoader getLoader() {
        return loader;
    }

    public void setLoader(ClassLoader loader) {
        this.loader = loader;
    }

    // -------------------------------------------------------------------------------------------- Option
    @Override
    public String[] getOptionNames() {
        return this.optionSet.getOptionNames();
    }

    @Override
    public Object getOption(String optionKey) {
        return this.optionSet.getOption(optionKey);
    }

    @Override
    public void removeOption(String optionKey) {
        this.optionSet.removeOption(optionKey);
    }

    @Override
    public void setOptionSet(Option optionSet) {
        this.optionSet.setOptionSet(optionSet);
    }

    @Override
    public void setOption(String optionKey, String value) {
        this.optionSet.setOption(optionKey, value);
    }

    @Override
    public void setOption(String optionKey, Number value) {
        this.optionSet.setOption(optionKey, value);
    }

    @Override
    public void setOption(String optionKey, boolean value) {
        this.optionSet.setOption(optionKey, value);
    }

    // -------------------------------------------------------------------------------------------- UdfManager
    @Override
    public List<UdfSource> getSourceByName(String sourceName) {
        return this.udfManager.getSourceByName(sourceName);
    }

    @Override
    public List<String> getSourceNames() {
        return this.udfManager.getSourceNames();
    }

    @Override
    public void addSource(UdfSource udfSource) {
        this.udfManager.addSource(udfSource);
    }

    // -------------------------------------------------------------------------------------------- ScriptEngine
    @Override
    public CompiledScript compile(Reader queryString) throws ScriptException {
        try {
            StringWriter outWriter = new StringWriter();
            IOUtils.copy(queryString, outWriter);
            return this.compile(outWriter.toString());
        } catch (IOException e) {
            throw new ScriptException(e);
        }
    }

    @Override
    public CompiledScript compile(String queryString) throws ScriptException {
        try {
            QIL compilerQIL = QueryCompiler.compilerQuery(queryString);
            return new DataQLCompiledScript(compilerQIL, this);
        } catch (ParseException e) {
            throw new ScriptException(e);
        }
    }

    // -------------------------------------------------------------------------------------------- ScriptEngine
    @Override
    public Object eval(Reader queryString, ScriptContext context) throws ScriptException {
        try {
            StringWriter outWriter = new StringWriter();
            IOUtils.copy(queryString, outWriter);
            return this.eval(outWriter.toString(), context);
        } catch (IOException e) {
            throw new ScriptException(e);
        }
    }

    @Override
    public ScriptEngineFactory getFactory() {
        return this.engineFactory;
    }

    private static ClassLoader getParentLoader() {
        // check whether thread context loader can "see" Groovy Script class
        ClassLoader ctxtLoader = Thread.currentThread().getContextClassLoader();
        try {
            Class c = ctxtLoader.loadClass(Query.class.getName());
            if (c == Query.class) {
                return ctxtLoader;
            }
        } catch (ClassNotFoundException cnfe) {
            /* ignore */
        }
        // exception was thrown or we get wrong class
        return Query.class.getClassLoader();
    }

    @Override
    public Object eval(String queryString, ScriptContext context) throws ScriptException {
        return compile(queryString).eval(context);
    }

    @Override
    public Bindings createBindings() {
        return new SimpleBindings();
    }
}