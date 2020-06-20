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
import net.hasor.dataql.Hints;
import net.hasor.dataql.compiler.QueryModel;
import net.hasor.dataql.compiler.qil.QIL;
import net.hasor.dataql.runtime.CompilerArguments;
import net.hasor.dataql.runtime.HintsSet;
import net.hasor.dataql.runtime.QueryHelper;

import javax.script.*;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Objects;

/**
 * JSR223 引擎机制的实现。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-10-19
 */
public class DataQLScriptEngine extends AbstractScriptEngine implements ScriptEngine, Compilable, Hints {
    private HintsSet                  optionSet = new HintsSet();
    private DataQLScriptEngineFactory engineFactory;
    private Finder                    finder    = Finder.DEFAULT;

    DataQLScriptEngine(DataQLScriptEngineFactory engineFactory) {
        this.engineFactory = engineFactory;
    }

    // -------------------------------------------------------------------------------------------- Option
    @Override
    public String[] getHints() {
        return this.optionSet.getHints();
    }

    @Override
    public Object getHint(String optionKey) {
        return this.optionSet.getHint(optionKey);
    }

    @Override
    public void removeHint(String optionKey) {
        this.optionSet.removeHint(optionKey);
    }

    @Override
    public void setHint(String hintName, String value) {
        this.optionSet.setHint(hintName, value);
    }

    @Override
    public void setHint(String hintName, Number value) {
        this.optionSet.setHint(hintName, value);
    }

    @Override
    public void setHint(String hintName, boolean value) {
        this.optionSet.setHint(hintName, value);
    }

    public Finder getFinder() {
        return finder;
    }

    public void setFinder(Finder finder) {
        this.finder = Objects.requireNonNull(finder, "finder is null.");
    }
    // -------------------------------------------------------------------------------------------- ScriptEngine

    @Override
    public ScriptEngineFactory getFactory() {
        return this.engineFactory;
    }

    @Override
    public Bindings createBindings() {
        return new SimpleBindings();
    }
    // -------------------------------------------------------------------------------------------- ScriptEngine

    @Override
    public CompiledScript compile(Reader queryString) throws ScriptException {
        try {
            Bindings global = this.getBindings(ScriptContext.GLOBAL_SCOPE);
            if (global == null) {
                global = createBindings();
                this.setBindings(createBindings(), ScriptContext.GLOBAL_SCOPE);
            }
            //
            QueryModel queryModel = QueryHelper.queryParser(queryString);
            CompilerArguments compilerArguments = new CompilerArguments(global.keySet());
            QIL compilerQIL = QueryHelper.queryCompiler(queryModel, compilerArguments, this.getFinder());
            return new DataQLCompiledScript(compilerQIL, this);
        } catch (IOException e) {
            throw new ScriptException(e);
        }
    }

    @Override
    public CompiledScript compile(String queryString) throws ScriptException {
        return this.compile(new StringReader(queryString));
    }

    @Override
    public Object eval(Reader queryString, ScriptContext context) throws ScriptException {
        this.setContext(context);
        return compile(queryString).eval();
    }

    @Override
    public Object eval(String queryString, ScriptContext context) throws ScriptException {
        this.setContext(context);
        return compile(queryString).eval();
    }
}