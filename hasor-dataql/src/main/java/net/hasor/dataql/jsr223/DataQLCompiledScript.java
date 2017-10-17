package net.hasor.dataql.jsr223;
import net.hasor.dataql.InvokerProcessException;
import net.hasor.dataql.Option;
import net.hasor.dataql.Query;
import net.hasor.dataql.QueryResult;
import net.hasor.dataql.domain.compiler.QIL;
import net.hasor.dataql.runtime.QueryEngine;

import javax.script.*;
/**
 *
 */
class DataQLCompiledScript extends CompiledScript implements Option {
    private DataQLScriptEngine engine;
    private QueryEngine        queryEngine;
    public DataQLCompiledScript(QIL compilerQIL, DataQLScriptEngine engine) {
        this.engine = engine;
        this.queryEngine = new QueryEngine(engine, compilerQIL);
        this.queryEngine.setClassLoader(engine.getLoader());
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
}