/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package org.more.util;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleScriptContext;
/**
 * 脚本执行工具
 * @version : 2011-7-22
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class ScriptUtils {
    /**默认脚本引擎*/
    public static final String DefaultScriptEngine = "javascript";
    /**创建默认脚本引擎。*/
    public static ScriptEngine createScriptEngine() {
        return createScriptEngine(DefaultScriptEngine);
    };
    /**创建脚本引擎。*/
    public static ScriptEngine createScriptEngine(String engine) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engineObject = manager.getEngineByName(engine);
        if (engineObject == null)
            throw new UnsupportedOperationException("不支持的脚本引擎[" + engine + "]");
        return engineObject;
    };
    /** 指定调用Action返回之后的脚本处理请求。 */
    public static Object runScript(InputStream script, Map<String, ?> params) throws Exception {
        return runScript(new InputStreamReader(script), params);
    };
    /** 指定调用Action返回之后的脚本处理请求。 */
    public static Object runScript(CharSequence sequence, Map<String, ?> params) throws Exception {
        StringBuffer sb = new StringBuffer(sequence);
        String str = sb.toString();
        return runScript(new StringReader(str), params);
    };
    /** 指定调用Action返回之后的脚本处理请求。 */
    public static Object runScript(Reader reader, Map<String, ?> params) throws Exception {
        return runScript(createScriptEngine(), reader, params);
    };
    /** 指定调用Action返回之后的脚本处理请求。 */
    public static Object runScript(ScriptEngine scriptEngine, InputStream script, Map<String, ?> params) throws Exception {
        return runScript(scriptEngine, new InputStreamReader(script), params);
    };
    /** 指定调用Action返回之后的脚本处理请求。 */
    public static Object runScript(ScriptEngine scriptEngine, CharSequence sequence, Map<String, ?> params) throws Exception {
        StringBuffer sb = new StringBuffer(sequence);
        String str = sb.toString();
        return runScript(scriptEngine, new StringReader(str), params);
    };
    /** 指定调用Action返回之后的脚本处理请求。 */
    public static Object runScript(ScriptEngine scriptEngine, Reader reader, Map<String, ?> params) throws Exception {
        if (scriptEngine == null)
            throw new NullPointerException("脚本引擎不能为空。");
        if (reader == null)
            throw new NullPointerException("脚本reader参数为空。");
        //执行脚本方法callBack，并且获取返回值返回
        SimpleScriptContext c = new SimpleScriptContext();
        for (String key : params.keySet())
            c.setAttribute(key, params.get(key), ScriptContext.GLOBAL_SCOPE);
        return scriptEngine.eval(reader, c);
    };
    /**获取可以执行调用的{@link Invocable}对象。*/
    public static Invocable getInvocable(InputStream script, Map<String, ?> params) throws Exception {
        return getInvocable(new InputStreamReader(script), params);
    }
    /**获取可以执行调用的{@link Invocable}对象。*/
    public static Invocable getInvocable(CharSequence sequence, Map<String, ?> params) throws Exception {
        StringBuffer sb = new StringBuffer(sequence);
        String str = sb.toString();
        return getInvocable(new StringReader(str), params);
    }
    /**获取可以执行调用的{@link Invocable}对象。*/
    public static Invocable getInvocable(Reader reader, Map<String, ?> params) throws Exception {
        return getInvocable(createScriptEngine(), reader, params);
    }
    /**获取可以执行调用的{@link Invocable}对象。*/
    public static Invocable getInvocable(ScriptEngine scriptEngine, InputStream script, Map<String, ?> params) throws Exception {
        return getInvocable(scriptEngine, new InputStreamReader(script), params);
    }
    /**获取可以执行调用的{@link Invocable}对象。*/
    public static Invocable getInvocable(ScriptEngine scriptEngine, CharSequence sequence, Map<String, ?> params) throws Exception {
        StringBuffer sb = new StringBuffer(sequence);
        String str = sb.toString();
        return getInvocable(scriptEngine, new StringReader(str), params);
    }
    /**获取可以执行调用的{@link Invocable}对象。*/
    public static Invocable getInvocable(ScriptEngine scriptEngine, Reader reader, Map<String, ?> params) throws Exception {
        if (scriptEngine == null)
            throw new NullPointerException("脚本引擎不能为空。");
        if (reader == null)
            throw new NullPointerException("脚本reader参数为空。");
        //执行脚本方法callBack，并且获取返回值返回
        SimpleScriptContext c = new SimpleScriptContext();
        for (String key : params.keySet())
            c.setAttribute(key, params.get(key), ScriptContext.GLOBAL_SCOPE);
        return (Invocable) scriptEngine;
    }
};