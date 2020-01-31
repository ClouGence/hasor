package net.hasor.dataql.extend;
import net.hasor.dataql.QueryResult;
import net.hasor.dataql.Udf;
import net.hasor.dataql.domain.DataModel;
import net.hasor.dataql.domain.ListModel;
import net.hasor.dataql.domain.ValueModel;
import net.hasor.dataql.extend.jsr223.DataQLScriptEngine;
import org.junit.Test;

import javax.script.*;
import java.util.HashMap;

public class Jsr223Test {
    @Test
    public void jar223_1() throws ScriptException {
        ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("dataql");
        Object eval = scriptEngine.eval("var a= 10 ; return a");
        //
        assert eval instanceof QueryResult;
        DataModel dataModel = ((QueryResult) eval).getData();
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).asInt() == 10;
    }

    @Test
    public void jar223_2() throws ScriptException {
        ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("dataql");
        //
        SimpleScriptContext params = new SimpleScriptContext();
        params.setBindings(scriptEngine.createBindings(), ScriptContext.GLOBAL_SCOPE);
        params.setBindings(scriptEngine.createBindings(), ScriptContext.ENGINE_SCOPE);
        params.setAttribute("uid", "uid form env", ScriptContext.ENGINE_SCOPE);
        params.setAttribute("sid", "sid form env", ScriptContext.GLOBAL_SCOPE);
        //
        Object eval = scriptEngine.eval("return [${uid},${sid}]", params);
        assert eval instanceof QueryResult;
        DataModel dataModel = ((QueryResult) eval).getData();
        assert dataModel.isListModel();
        assert ((ListModel) dataModel).asValueModel(0).asString().equals("uid form env");
        assert ((ListModel) dataModel).asValueModel(1).asString().equals("sid form env");
    }

    @Test
    public void jar223_3() throws ScriptException {
        HashMap<String, Object> tempData = new HashMap<String, Object>() {{
            put("uid", "uid form tempData");
            put("sid", "sid form tempData");
        }};
        //
        DataQLScriptEngine scriptEngine = (DataQLScriptEngine) new ScriptEngineManager().getEngineByName("dataql");
        scriptEngine.setCustomizeScopeCreater(() -> symbol -> tempData);
        //
        SimpleScriptContext params = new SimpleScriptContext();
        params.setBindings(scriptEngine.createBindings(), ScriptContext.GLOBAL_SCOPE);
        params.setBindings(scriptEngine.createBindings(), ScriptContext.ENGINE_SCOPE);
        params.setAttribute("uid", "uid form env", ScriptContext.ENGINE_SCOPE); // 因为设置了 CustomizeScopeCreater，所以这里无效
        params.setAttribute("sid", "sid form env", ScriptContext.GLOBAL_SCOPE); // 因为设置了 CustomizeScopeCreater，所以这里无效
        //
        Object eval = scriptEngine.eval("return [${uid},${sid}]", params);
        assert eval instanceof QueryResult;
        DataModel dataModel = ((QueryResult) eval).getData();
        assert dataModel.isListModel();
        assert ((ListModel) dataModel).asValueModel(0).asString().equals("uid form tempData");
        assert ((ListModel) dataModel).asValueModel(1).asString().equals("sid form tempData");
    }

    @Test
    public void jar223_4() throws ScriptException {
        Udf testUdf = (readOnly, params) -> readOnly.getHint("abc");
        //
        DataQLScriptEngine scriptEngine = (DataQLScriptEngine) new ScriptEngineManager().getEngineByName("dataql");
        SimpleScriptContext params = new SimpleScriptContext();
        params.setBindings(scriptEngine.createBindings(), ScriptContext.GLOBAL_SCOPE);// GLOBAL is CompilerVar
        params.setAttribute("foo", testUdf, ScriptContext.GLOBAL_SCOPE);
        //
        scriptEngine.setHint("abc", 10);
        Object eval = scriptEngine.eval("return foo()", params);
        assert eval instanceof QueryResult;
        DataModel dataModel = ((QueryResult) eval).getData();
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).asInt() == 10;
    }
}
