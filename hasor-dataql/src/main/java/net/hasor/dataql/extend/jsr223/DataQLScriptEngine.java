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
import net.hasor.dataql.Finder;
import net.hasor.dataql.Option;
import net.hasor.dataql.compiler.qil.QIL;
import net.hasor.dataql.runtime.OptionSet;
import net.hasor.dataql.runtime.QueryHelper;
import net.hasor.utils.io.IOUtils;

import javax.script.*;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Objects;

/**
 * JSR223 引擎机制的实现。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-10-19
 */
public class DataQLScriptEngine extends AbstractScriptEngine implements ScriptEngine, Compilable, Option {
    private OptionSet                 optionSet             = new OptionSet();
    private DataQLScriptEngineFactory engineFactory;
    private CustomizeScopeCreater     customizeScopeCreater = () -> null;
    private Finder                    finder                = Finder.DEFAULT;

    DataQLScriptEngine(DataQLScriptEngineFactory engineFactory) {
        this.engineFactory = engineFactory;
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

    public Finder getFinder() {
        return finder;
    }

    public void setFinder(Finder finder) {
        this.finder = Objects.requireNonNull(finder, "finder is null.");
    }

    public CustomizeScopeCreater getCustomizeScopeCreater() {
        return customizeScopeCreater;
    }

    public void setCustomizeScopeCreater(CustomizeScopeCreater customizeScopeCreater) {
        this.customizeScopeCreater = Objects.requireNonNull(customizeScopeCreater, "customizeScopeCreater is null.");
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
            QIL compilerQIL = QueryHelper.queryCompiler(queryString, getFinder());
            return new DataQLCompiledScript(compilerQIL, this);
        } catch (IOException e) {
            throw new ScriptException(e);
        }
    }

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

    @Override
    public Object eval(String queryString, ScriptContext context) throws ScriptException {
        return compile(queryString).eval(context);
    }

    @Override
    public Bindings createBindings() {
        return new SimpleBindings();
    }
}