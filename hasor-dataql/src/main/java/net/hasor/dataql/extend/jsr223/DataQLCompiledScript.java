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
import net.hasor.dataql.CustomizeScope;
import net.hasor.dataql.Option;
import net.hasor.dataql.Query;
import net.hasor.dataql.QueryResult;
import net.hasor.dataql.compiler.qil.QIL;
import net.hasor.dataql.runtime.InstructRuntimeException;
import net.hasor.dataql.runtime.OptionSet;
import net.hasor.dataql.runtime.QueryHelper;

import javax.script.*;
import java.util.HashMap;
import java.util.Map;

/**
 * JSR223 编译机制的实现。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-10-19
 */
class DataQLCompiledScript extends CompiledScript implements Option {
    private QIL                compilerQIL;
    private OptionSet          optionSet;
    private DataQLScriptEngine engine;

    public DataQLCompiledScript(QIL compilerQIL, DataQLScriptEngine engine) {
        this.compilerQIL = compilerQIL;
        this.optionSet = new OptionSet();
        this.optionSet.setOptionSet(engine);
        this.engine = engine;
    }

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
    // -------------------------------------------------------------------------------------------- Option

    @Override
    public ScriptEngine getEngine() {
        return this.engine;
    }

    @Override
    public QueryResult eval(ScriptContext context) throws ScriptException {
        Query query = QueryHelper.createQuery(this.compilerQIL, this.engine.getFinder());
        Bindings globalBindings = context.getBindings(ScriptContext.GLOBAL_SCOPE);
        if (globalBindings != null) {
            globalBindings.forEach(query::setCompilerVar);
        }
        //
        CustomizeScope customizeScope = this.engine.getCustomizeScopeCreater().create();
        if (customizeScope == null) {
            Bindings engineBindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
            //
            Map<String, Object> dataMap = new HashMap<>();
            if (globalBindings != null) {
                dataMap.putAll(globalBindings);
            }
            if (engineBindings != null) {
                dataMap.putAll(engineBindings);
            }
            customizeScope = symbol -> {
                return dataMap;
            };
        }
        try {
            query.setOptionSet(this);
            return query.execute(customizeScope);
        } catch (InstructRuntimeException e) {
            throw new ScriptException(e);
        }
    }
}