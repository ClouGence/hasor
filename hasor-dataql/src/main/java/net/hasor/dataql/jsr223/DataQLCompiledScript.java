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
import net.hasor.dataql.*;
import net.hasor.dataql.domain.compiler.QIL;
import net.hasor.dataql.runtime.QueryEngineImpl;

import javax.script.*;
/**
 * JSR223 编译机制的实现。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-10-19
 */
class DataQLCompiledScript extends CompiledScript implements QueryEngine {
    private DataQLScriptEngine engine;
    private QueryEngineImpl    queryEngine;
    public DataQLCompiledScript(QIL compilerQIL, DataQLScriptEngine engine) {
        this.engine = engine;
        this.queryEngine = new QueryEngineImpl(engine, compilerQIL);
        this.queryEngine.setOptionSet(engine);
        this.queryEngine.setClassLoader(engine.getLoader());
        //
    }
    @Override
    public ScriptEngine getEngine() {
        return this.engine;
    }
    //
    @Override
    public QueryResult eval(ScriptContext context) throws ScriptException {
        //
        Query query = this.queryEngine.newQuery();
        Bindings globalBindings = this.engine.getBindings(ScriptContext.GLOBAL_SCOPE);
        if (globalBindings != null) {
            query.addParameterMap(globalBindings);
        }
        Bindings engineBindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        if (engineBindings != null) {
            query.addParameterMap(engineBindings);
        }
        //
        try {
            return query.execute();
        } catch (InvokerProcessException e) {
            throw new ScriptException(e);
        }
    }
    //
    //
    // -------------------------------------------------------------------------------------------- Option
    @Override
    public String[] getOptionNames() {
        return this.queryEngine.getOptionNames();
    }
    @Override
    public Object getOption(String optionKey) {
        return this.queryEngine.getOption(optionKey);
    }
    @Override
    public void removeOption(String optionKey) {
        this.queryEngine.removeOption(optionKey);
    }
    @Override
    public void setOptionSet(Option optionSet) {
        this.queryEngine.setOptionSet(optionSet);
    }
    @Override
    public void setOption(String optionKey, String value) {
        this.queryEngine.setOption(optionKey, value);
    }
    @Override
    public void setOption(String optionKey, Number value) {
        this.queryEngine.setOption(optionKey, value);
    }
    @Override
    public void setOption(String optionKey, boolean value) {
        this.queryEngine.setOption(optionKey, value);
    }
    //
    // -------------------------------------------------------------------------------------------- QueryEngine
    @Override
    public QIL getQil() {
        return this.queryEngine.getQil();
    }
    @Override
    public ClassLoader getClassLoader() {
        return this.queryEngine.getClassLoader();
    }
    @Override
    public UdfManager getUdfManager() {
        return this.queryEngine.getUdfManager();
    }
    @Override
    public void setClassLoader(ClassLoader classLoader) {
        this.queryEngine.setClassLoader(classLoader);
    }
    @Override
    public Query newQuery() {
        return this.queryEngine.newQuery();
    }
    @Override
    public void refreshUDF() {
        this.queryEngine.refreshUDF();
    }
}