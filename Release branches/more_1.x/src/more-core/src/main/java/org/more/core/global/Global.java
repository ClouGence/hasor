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
package org.more.core.global;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.more.core.error.FormatException;
import org.more.core.error.InitializationException;
import org.more.core.error.SupportException;
import org.more.core.json.JsonUtil;
import org.more.core.ognl.Ognl;
import org.more.core.ognl.OgnlException;
import org.more.util.ResourcesUtil;
import org.more.util.StringConvertUtil;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
import org.more.util.attribute.SequenceStack;
import org.more.util.attribute.TransformToMap;
/**
* 全局常量读取器
* @version : 2011-9-3
* @author 赵永春 (zyc@byshell.org)
*/
public abstract class Global implements IAttribute<Object> {
    /**内置对象名*/
    private final static String                _global      = "_global";
    private final static String                EnableEL     = "_global.enableEL";
    private final static String                EnableJson   = "_global.enableJson";
    public final static String[]               Configs      = new String[] { "global_config.properties", "META-INF/global_config.properties", "META-INF/resource/core/global_config.properties" };
    /**添加的所有配置文件都在这里保存，根据不同的注册名来进行分组*/
    private Map<String, SequenceStack<String>> poolMap      = null;
    private SequenceStack<String>              allData      = null;
    private SequenceStack<Object>              rootMap      = null;
    //
    private Object                             context      = null;
    private GlobalObject                       globalObject = null;
    //
    /*------------------------------------------------------------------------*/
    public Global(IAttribute<String> configs) {
        this();
        if (configs != null)
            this.addConfig("", configs);
    }
    public Global() {
        this.poolMap = new LinkedHashMap<String, SequenceStack<String>>();
        this.allData = new SequenceStack<String>();
        //设置el root对象
        this.rootMap = new SequenceStack<Object>();
        this.rootMap.putStack(new AttBase<Object>());
        this.rootMap.putStack(this);
        this.globalObject = new GlobalObject(this);
        this.rootMap.setAttribute(_global, new TransformToMap<Object>(this.globalObject));//内置对象
    };
    /*------------------------------------------------------------------------*/
    /**解析全局配置参数，并且返回其{@link Object}形式对象。*/
    public Object getObject(Enum<?> name) {
        return this.getToType(name, Object.class);
    };
    /**解析全局配置参数，并且返回其{@link Object}形式对象。*/
    public Object getObject(String name) {
        return this.getToType(name, Object.class);
    };
    /**解析全局配置参数，并且返回其{@link Object}形式对象。第二个参数为默认值。*/
    public Object getObject(Enum<?> name, Object defaultValue) {
        return this.getToType(name, Object.class, defaultValue);
    };
    /**解析全局配置参数，并且返回其{@link Object}形式对象。第二个参数为默认值。*/
    public Object getObject(String name, Object defaultValue) {
        return this.getToType(name, Object.class, defaultValue);
    };
    /**解析全局配置参数，并且返回其{@link Character}形式对象。*/
    public Character getChar(Enum<?> name) {
        return this.getToType(name, Character.class);
    };
    /**解析全局配置参数，并且返回其{@link Character}形式对象。*/
    public Character getChar(String name) {
        return this.getToType(name, Character.class);
    };
    /**解析全局配置参数，并且返回其{@link Character}形式对象。第二个参数为默认值。*/
    public Character getChar(Enum<?> name, Character defaultValue) {
        return this.getToType(name, Character.class, defaultValue);
    };
    /**解析全局配置参数，并且返回其{@link Character}形式对象。第二个参数为默认值。*/
    public Character getChar(String name, Character defaultValue) {
        return this.getToType(name, Character.class, defaultValue);
    };
    /**解析全局配置参数，并且返回其{@link String}形式对象。*/
    public String getString(Enum<?> name) {
        return this.getToType(name, String.class);
    };
    /**解析全局配置参数，并且返回其{@link String}形式对象。*/
    public String getString(String name) {
        return this.getToType(name, String.class);
    };
    /**解析全局配置参数，并且返回其{@link String}形式对象。第二个参数为默认值。*/
    public String getString(Enum<?> name, String defaultValue) {
        return this.getToType(name, String.class, defaultValue);
    };
    /**解析全局配置参数，并且返回其{@link String}形式对象。第二个参数为默认值。*/
    public String getString(String name, String defaultValue) {
        return this.getToType(name, String.class, defaultValue);
    };
    /**解析全局配置参数，并且返回其{@link Boolean}形式对象。*/
    public Boolean getBoolean(Enum<?> name) {
        return this.getToType(name, Boolean.class);
    };
    /**解析全局配置参数，并且返回其{@link Boolean}形式对象。*/
    public Boolean getBoolean(String name) {
        return this.getToType(name, Boolean.class);
    };
    /**解析全局配置参数，并且返回其{@link Boolean}形式对象。第二个参数为默认值。*/
    public Boolean getBoolean(Enum<?> name, Boolean defaultValue) {
        return this.getToType(name, Boolean.class, defaultValue);
    };
    /**解析全局配置参数，并且返回其{@link Boolean}形式对象。第二个参数为默认值。*/
    public Boolean getBoolean(String name, Boolean defaultValue) {
        return this.getToType(name, Boolean.class, defaultValue);
    };
    /**解析全局配置参数，并且返回其{@link Short}形式对象。*/
    public Short getShort(Enum<?> name) {
        return this.getToType(name, Short.class);
    };
    /**解析全局配置参数，并且返回其{@link Short}形式对象。*/
    public Short getShort(String name) {
        return this.getToType(name, Short.class);
    };
    /**解析全局配置参数，并且返回其{@link Short}形式对象。第二个参数为默认值。*/
    public Short getShort(Enum<?> name, Short defaultValue) {
        return this.getToType(name, Short.class, defaultValue);
    };
    /**解析全局配置参数，并且返回其{@link Short}形式对象。第二个参数为默认值。*/
    public Short getShort(String name, Short defaultValue) {
        return this.getToType(name, Short.class, defaultValue);
    };
    /**解析全局配置参数，并且返回其{@link Integer}形式对象。*/
    public Integer getInteger(Enum<?> name) {
        return this.getToType(name, Integer.class);
    };
    /**解析全局配置参数，并且返回其{@link Integer}形式对象。*/
    public Integer getInteger(String name) {
        return this.getToType(name, Integer.class);
    };
    /**解析全局配置参数，并且返回其{@link Integer}形式对象。第二个参数为默认值。*/
    public Integer getInteger(Enum<?> name, Integer defaultValue) {
        return this.getToType(name, Integer.class, defaultValue);
    };
    /**解析全局配置参数，并且返回其{@link Integer}形式对象。第二个参数为默认值。*/
    public Integer getInteger(String name, Integer defaultValue) {
        return this.getToType(name, Integer.class, defaultValue);
    };
    /**解析全局配置参数，并且返回其{@link Long}形式对象。*/
    public Long getLong(Enum<?> name) {
        return this.getToType(name, Long.class);
    };
    /**解析全局配置参数，并且返回其{@link Long}形式对象。*/
    public Long getLong(String name) {
        return this.getToType(name, Long.class);
    };
    /**解析全局配置参数，并且返回其{@link Long}形式对象。第二个参数为默认值。*/
    public Long getLong(Enum<?> name, Long defaultValue) {
        return this.getToType(name, Long.class, defaultValue);
    };
    /**解析全局配置参数，并且返回其{@link Long}形式对象。第二个参数为默认值。*/
    public Long getLong(String name, Long defaultValue) {
        return this.getToType(name, Long.class, defaultValue);
    };
    /**解析全局配置参数，并且返回其{@link Float}形式对象。*/
    public Float getFloat(Enum<?> name) {
        return this.getToType(name, Float.class);
    };
    /**解析全局配置参数，并且返回其{@link Float}形式对象。*/
    public Float getFloat(String name) {
        return this.getToType(name, Float.class);
    };
    /**解析全局配置参数，并且返回其{@link Float}形式对象。第二个参数为默认值。*/
    public Float getFloat(Enum<?> name, Float defaultValue) {
        return this.getToType(name, Float.class, defaultValue);
    };
    /**解析全局配置参数，并且返回其{@link Float}形式对象。第二个参数为默认值。*/
    public Float getFloat(String name, Float defaultValue) {
        return this.getToType(name, Float.class, defaultValue);
    };
    /**解析全局配置参数，并且返回其{@link Double}形式对象。*/
    public Double getDouble(Enum<?> name) {
        return this.getToType(name, Double.class);
    };
    /**解析全局配置参数，并且返回其{@link Double}形式对象。*/
    public Double getDouble(String name) {
        return this.getToType(name, Double.class);
    };
    /**解析全局配置参数，并且返回其{@link Double}形式对象。第二个参数为默认值。*/
    public Double getDouble(Enum<?> name, Double defaultValue) {
        return this.getToType(name, Double.class, defaultValue);
    };
    /**解析全局配置参数，并且返回其{@link Double}形式对象。第二个参数为默认值。*/
    public Double getDouble(String name, Double defaultValue) {
        return this.getToType(name, Double.class, defaultValue);
    };
    /**解析全局配置参数，并且返回其{@link Date}形式对象。*/
    public Date getDate(Enum<?> name) {
        return this.getToType(name, Date.class);
    };
    /**解析全局配置参数，并且返回其{@link Date}形式对象。*/
    public Date getDate(String name) {
        return this.getToType(name, Date.class);
    };
    /**解析全局配置参数，并且返回其{@link Date}形式对象。第二个参数为默认值。*/
    public Date getDate(Enum<?> name, Date defaultValue) {
        return this.getToType(name, Date.class, defaultValue);
    };
    /**解析全局配置参数，并且返回其{@link Date}形式对象。第二个参数为默认值。*/
    public Date getDate(String name, Date defaultValue) {
        return this.getToType(name, Date.class, defaultValue);
    };
    /**解析全局配置参数，并且返回其{@link Date}形式对象。第二个参数为默认值。*/
    public Date getDate(Enum<?> name, long defaultValue) {
        return this.getToType(name, Date.class, new Date(defaultValue));
    };
    /**解析全局配置参数，并且返回其{@link Date}形式对象。第二个参数为默认值。*/
    public Date getDate(String name, long defaultValue) {
        return this.getToType(name, Date.class, new Date(defaultValue));
    };
    /*------------------------------------------------------------------------*/
    /**视图访问以该枚举名称命名的属性文件或绑定在该枚举上的属性文件，如果尝试访问的属性文件不存在则规则与{@link #getOriginalString(String)}相同。*/
    public String getOriginalString(String scope, String name) {
        if (name == null)
            return null;
        //2.从list中获取Config
        IAttribute<String> config = null;
        if (scope == null || this.poolMap.containsKey(scope) == false)
            config = this.allData;
        else
            config = this.poolMap.get(scope);
        return config.getAttribute(name);
    };
    /**按照序列顺序在所有加入的全局配置文件中寻找指定名称的属性值，并且将原始信息返回。*/
    public String getOriginalString(String name) {
        return this.getOriginalString(null, name);
    };
    /**解析全局配置参数，并且返回toType参数指定的类型。*/
    public <T> T getToType(String name, Class<T> toType, T defaultValue) {
        String oriString = this.getOriginalString(null, name);
        if (oriString == null)
            return defaultValue;
        //
        Object var = this.getEval(oriString);
        return StringConvertUtil.changeType(var, toType, defaultValue);
    }
    /**解析全局配置参数，并且返回toType参数指定的类型。*/
    public <T> T getToType(Enum<?> enumItem, Class<T> toType, T defaultValue) {
        if (enumItem == null)
            return defaultValue;
        String oriString = this.getOriginalString(enumItem.getClass().getName(), enumItem.name());
        if (oriString == null)
            return defaultValue;
        //
        Object var = this.getEval(oriString);
        return StringConvertUtil.changeType(var, toType, defaultValue);
    }
    /**解析全局配置参数，并且返回toType参数指定的类型。*/
    public <T> T getToType(Enum<?> enumItem, Class<T> toType) {
        return this.getToType(enumItem, toType, null);
    };
    /**解析全局配置参数，并且返回toType参数指定的类型。*/
    public <T> T getToType(String name, Class<T> toType) {
        return this.getToType(name, toType, null);
    };
    /*------------------------------------------------------------------------*/
    public boolean contains(String name) {
        return this.allData.contains(name);
    }
    /**该方法等同于{@link #getObject(String)}*/
    public Object getAttribute(String name) {
        return this.getObject(name);
    }
    /**将{@link Global}类转换成{@link Map}接口形式。*/
    public Map<String, Object> toMap() {
        return new TransformToMap<Object>(this);
    }
    public String[] getAttributeNames() {
        return this.allData.getAttributeNames();
    }
    /**Global，不支持该方法。*/
    public void setAttribute(String name, Object value) {
        throw new SupportException("Global，不支持该方法。");
    }
    /**Global，不支持该方法。*/
    public void removeAttribute(String name) {
        throw new SupportException("Global，不支持该方法。");
    }
    /**Global，不支持该方法。*/
    public void clearAttribute() {
        throw new SupportException("Global，不支持该方法。");
    }
    /*------------------------------------------------------------------------*/
    /**将一组原始的配置信息添加到Global中，如果存在已经使用的名称则追加。*/
    public void addConfig(Class<? extends Enum<?>> enumType, IAttribute<String> config) {
        if (enumType == null || config == null)
            throw new NullPointerException("‘enumType’ or ‘config’ param is null");
        this.addConfig(enumType.getName(), config);
    }
    /**将一组原始的配置信息添加到Global中，如果存在已经使用的名称则追加。*/
    public void addConfig(String name, IAttribute<String> config) {
        if (name == null || config == null)
            throw new NullPointerException("‘name’ or ‘config’ param is null");
        SequenceStack<String> stack = null;
        if (this.poolMap.containsKey(name) == false) {
            //新增了一条，同时将这条增加到allData中
            stack = new SequenceStack<String>();
            this.poolMap.put(name, stack);
            this.allData.putStack(stack);
        } else
            stack = this.poolMap.get(name);
        stack.putStack(config);
    };
    /**检测name表示的配置信息是否已经注册。*/
    protected boolean containsConfig(String name) {
        return this.poolMap.containsKey(name);
    };
    /**解析流对象，并且将解析的结果返回为{@link IAttribute}接口形式。*/
    protected IAttribute<String> loadConfig(InputStream stream) throws IOException {
        return new AttBase<String>();
    };
    /**返回上下文对象。*/
    protected Object getContext() {
        return this.context;
    };
    /**设置上下文对象。*/
    protected void setContext(Object context) {
        this.context = context;
    };
    /**返回上下文对象。*/
    protected Map<String, Object> getRoot() {
        return this.rootMap.toMap();
    };
    /**返回一共增加了多少个config分组设置。*/
    protected int getConfigGroupCount() {
        return this.poolMap.size();
    }
    /**返回一共增加了多少个config设置。*/
    protected int getConfigAllCount() {
        return this.allData.size();
    }
    /**添加一个内置属性*/
    public void addGlobalProperty(String name, GlobalProperty property) {
        if (name == null || name.equals("") == true || property == null)
            throw new NullPointerException("");
        this.globalObject.addGlobalProperty(name, property);
    }
    /*------------------------------------------------------------------------*/
    private <T> T getEval(String elString) {
        elString = elString.trim();
        if (elString == null || elString.equals("") == true)
            return null;
        //1.获取标记位置
        StringBuffer elStr = new StringBuffer(elString);
        char firstChar = elStr.charAt(0);
        char lastChar = elStr.charAt(elStr.length() - 1);
        //2.截取尾巴
        if (lastChar == ';') {
            elStr.deleteCharAt(elStr.length());
            lastChar = elStr.charAt(elStr.length() - 1);//去掉尾部的分号
        }
        //3.判断类型
        elStr.deleteCharAt(0);
        elStr.deleteCharAt(elStr.length() - 1);
        Object res = null;
        if (firstChar == '(' && lastChar == ')')
            //整句JSON
            res = this.$evalJSON(elStr.toString());
        else if (firstChar == '{' && lastChar == '}')
            //整句EL
            res = this.$evalEL(elStr.toString());
        else if (firstChar == '"' && lastChar == '"')
            //整句字符串1
            res = this.$evalString(elStr.toString());
        else if (firstChar == '\'' && lastChar == '\'')
            //整句字符串2
            res = this.$evalString(elStr.toString());
        else
            //包含EL的文本 
            res = this.$evalEL2(elString);
        //4.返回解析结果
        return (T) res;
    }
    /**在解析过程中负责解析字符串*/
    protected String $evalString(String string) {
        return string;
    };
    /**在解析过程中负责解析EL串，如果_global.enableEL属性配置为false则不解析json数据。*/
    protected Object $evalEL(String elString) {
        //1.解析elString
        if (this.getBoolean(EnableEL, true) == false)
            return this.$evalString(elString);
        //2.解析elString
        try {
            return Ognl.getValue(elString, this.getRoot());
        } catch (OgnlException e) {
            throw new FormatException(elString + "：作为EL解析错误。");
        }
    };
    /**在解析过程中负责解析Json串，如果_global.enableJson属性配置为false则不解析json数据。*/
    protected Object $evalJSON(String jsonString) {
        if (this.getBoolean(EnableJson, true) == false)
            return this.$evalString(jsonString);
        else
            return JsonUtil.transformToObject(jsonString);
    };
    /**在解析过程中负责解析包含EL串的字符串，如果_global.enableEL属性配置为false则不解析json数据。，该字符串中是通过${和}块来标记EL部分。*/
    protected Object $evalEL2(String elString) {
        //如果要处理的字符串中不包含表达式部分则使用字符串方式处理。
        if (elString.matches("\\$\\{.*\\}") == false)
            return this.$evalString(elString);
        //XXX: 以后可以使用JavaCC进行解析，这样就可以处理“${}”的转义问题。目前只负责将${}部分通过正则表达式进行查找替换。
        //
        //
        final String startKEY = "${";
        final String endKEY = "}";
        //
        int index = elString.indexOf(startKEY);
        int length = 0;
        StringBuffer sb = new StringBuffer();
        while (true) {
            if (elString.indexOf(startKEY, index) != -1)
                break;
            length = elString.indexOf(endKEY, index);
            sb.append(elString.substring(index, length));
        }
        return this.$evalEL(sb.toString());
    };
    /*------------------------------------------------------------------------*/
    /**起缓存作用*/
    private static final HashMap<String, Class<?>> globalFactoryMap = new HashMap<String, Class<?>>();
    /**创建默认的{@link Global}对象。*/
    public static Global newInstance() throws IOException, ClassNotFoundException {
        return newInstance((Object) null);
    }
    /**创建默认的{@link Global}对象，参数是{@link GlobalFactory}在创建{@link Global}时候传入的参数。*/
    public static Global newInstance(Object... params) throws IOException, ClassNotFoundException {
        return newInstance("properties", params);
    }
    /**创建{@link Global}对象，参数是{@link GlobalFactory}在创建{@link Global}时候传入的参数。factoryName是指定注册的{@link GlobalFactory}。*/
    public static Global newInstance(String factoryName, Object... params) throws IOException, ClassNotFoundException {
        Class<?> globalFactoryType = null;
        if (globalFactoryMap.containsKey(factoryName) == true)
            globalFactoryType = globalFactoryMap.get(factoryName);
        else {
            IAttribute<String> configAtt = ResourcesUtil.getPropertys(Configs);
            String factoryType = configAtt.getAttribute(factoryName);
            if (factoryType == null)
                throw new SupportException("Global factory ‘" + factoryName + "’ is not define.");
            globalFactoryType = Thread.currentThread().getContextClassLoader().loadClass(factoryType);
            globalFactoryMap.put(factoryName, globalFactoryType);
        }
        //
        try {
            GlobalFactory globalFactory = (GlobalFactory) globalFactoryType.newInstance();
            return globalFactory.createGlobal(params);
        } catch (Exception e) {
            throw new InitializationException("init error can`t create type " + globalFactoryType);
        }
    };
    /**创建一个{@link Global}本体实例化对象。*/
    public static Global newInterInstance(IAttribute<String> configs) {
        return new Global(configs) {};
    };
    /**创建一个{@link Global}本体实例化对象。*/
    public static Global newInterInstance() {
        return new Global(null) {};
    };
};