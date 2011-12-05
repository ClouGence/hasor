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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.more.core.error.FormatException;
import org.more.core.error.InitializationException;
import org.more.core.error.SupportException;
import org.more.core.json.JsonUtil;
import org.more.core.ognl.Ognl;
import org.more.core.ognl.OgnlContext;
import org.more.core.ognl.OgnlException;
import org.more.util.ResourcesUtil;
import org.more.util.StringConvertUtil;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
import org.more.util.attribute.SequenceStack;
import org.more.util.attribute.TransformToAttribute;
import org.more.util.attribute.TransformToMap;
/**
* 全局常量读取器
* @version : 2011-9-3
* @author 赵永春 (zyc@byshell.org)
*/
public abstract class Global implements IAttribute<Object> {
    /**是否启用el表达式解析。*/
    private final static String                          EnableEL     = "_global.enableEL";
    /**是否启用json解析*/
    private final static String                          EnableJson   = "_global.enableJson";
    /**其顺序是优先级顺序*/
    public final static String[]                         Configs      = new String[] { "META-INF/resource/core/global_config.properties", "META-INF/global_config.properties", "global_config.properties" };
    /**添加的所有配置文件都在这里保存，根据不同的注册名来进行分组，_global域和_cache域也在其中。*/
    private LinkedHashMap<String, SequenceStack<Object>> scopeMap     = null;
    private GlobalObject                                 globalObject = new GlobalObject(this);
    private AttBase<Object>                              cache        = new AttBase<Object>();
    // 
    //
    /*------------------------------------------------------------------------*/
    private SequenceStack<Object>                        $elData      = null;
    /**返回可用于计算EL的集合对象，该集合对象中包含了_Global和_Cache。*/
    protected IAttribute<Object> getALLRoot() {
        if (this.$elData == null) {
            this.$elData = new SequenceStack<Object>();
            for (IAttribute<Object> att : this.scopeMap.values())
                if (att != null)
                    $elData.putStack(att);
            this.$elData.putStack(cache);
        }
        return this.$elData;
    };
    public OgnlContext transformToOgnlContext() {
        HashMap<String, Object> all = new HashMap<String, Object>(this.getALLRoot().toMap());
        all.put(GlobalObject._Global, this.globalObject);
        return new OgnlContext(all);
    };
    /*------------------------------------------------------------------------*/
    public Global(IAttribute<Object> configs) {
        this();
        if (configs != null)
            this.addScope("", configs);
    };
    public Global() {
        this.scopeMap = new LinkedHashMap<String, SequenceStack<Object>>();
    };
    /*------------------------------------------------------------------------*/
    /**使用Ognl计算字符串，并且返回其计算结果。*/
    public Object evalName(String ognlString) throws OgnlException {
        Object oriObject = Ognl.getValue(ognlString, this.transformToOgnlContext());
        if (oriObject instanceof String)
            return this.getEval((String) oriObject);
        else
            return oriObject;
    };
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
    /**解析全局配置参数，并且返回toType参数指定的类型。*/
    public <T> T getToType(Enum<?> enumItem, Class<T> toType) {
        return this.getToType(enumItem, toType, null);
    };
    /**解析全局配置参数，并且返回toType参数指定的类型。*/
    public <T> T getToType(String name, Class<T> toType) {
        return this.getToType(name, toType, null);
    };
    /**解析全局配置参数，并且返回toType参数指定的类型。*/
    public <T> T getToType(String name, Class<T> toType, T defaultValue) {
        Object oriObject = this.getOriginalObject(null, name);
        if (oriObject == null)
            return defaultValue;
        //
        Object var = null;
        if (oriObject instanceof String)
            var = this.getEval((String) oriObject);
        else
            var = oriObject;
        return StringConvertUtil.changeType(var, toType, defaultValue);
    };
    /**解析全局配置参数，并且返回toType参数指定的类型。*/
    public <T> T getToType(Enum<?> enumItem, Class<T> toType, T defaultValue) {
        if (enumItem == null)
            return defaultValue;
        Object oriObject = this.getOriginalObject(enumItem.getClass().getName(), enumItem.name());//获取原始数据
        //
        Object var = null;
        if (oriObject instanceof String)
            //原始数据是字符串经过Eval过程
            var = this.getEval((String) oriObject);
        else if (oriObject instanceof GlobalProperty)
            //原始数据是GlobalProperty直接get
            var = ((GlobalProperty) oriObject).getValue(this);
        else
            //其他类型不予处理（数据就是要的值）
            var = oriObject;
        return StringConvertUtil.changeType(var, toType, defaultValue);
    };
    /** {@link Global#getOriginalObject(String)}的字符串形式.*/
    public String getOriginalString(String name) {
        Object obj = this.getOriginalObject(name);
        return (obj != null) ? obj.toString() : null;
    };
    /** {@link Global#getOriginalObject(String, String)}的字符串形式.*/
    public String getOriginalString(String scope, String name) {
        Object obj = this.getOriginalObject(scope, name);
        return (obj != null) ? obj.toString() : null;
    };
    /**获取指定名称的原始配置信息，该原始信息配置是存在于下面位置：<br/>1._Cache。<br/>2.装载的配置文件。<br/>3.通过addConfig方法加入的配置数据。*/
    public Object getOriginalObject(String name) {
        return this.getOriginalObject(null, name);
    };
    /**在指定的作用域中获取指定名称的原始配置信息，如果作用域参数为null那么将在所有作用域中搜寻，该原始信息配置是存在于下面位置：<br/>
     * 1._Cache。<br/>2.装载的配置文件。<br/>3.通过addConfig方法加入的配置数据。*/
    public Object getOriginalObject(String scope, String name) {
        if (name == null)
            return null;
        //1.从list中获取Config
        IAttribute<Object> config = null;
        if (scope == null || this.scopeMap.containsKey(scope) == false)
            config = this.getALLRoot();//不包含内置对象。
        else
            config = this.scopeMap.get(scope);
        //2.Name
        return config.getAttribute(name);
    };
    /*------------------------------------------------------------------------*/
    public boolean contains(String name) {
        return this.getALLRoot().contains(name);
    };
    /**该方法等同于{@link #getObject(String)}*/
    public Object getAttribute(String name) {
        return this.getObject(name);
    };
    /**将{@link Global}类转换成{@link Map}接口形式。*/
    public Map<String, Object> toMap() {
        return new TransformToMap<Object>(this);
    };
    /**将{@link Global}类转换成{@link Map}接口形式。*/
    public Map<String, Object> toMap(String scope) {
        return this.scopeMap.get(scope).toMap();
    };
    public String[] getAttributeNames() {
        return this.getALLRoot().getAttributeNames();
    };
    public int size() {
        return this.getALLRoot().size();
    };
    /**值会被直接设置到内置对象中，该方法不会影响到固定的内置属性（它们的优先级仍然是最高的）。*/
    public void setAttribute(String name, Object newValue) {
        this.cache.put(name, newValue);
    };
    /**从内置对象中删除属性，该方法不会影响到固定的内置属性（它们的优先级仍然是最高的）。*/
    public void removeAttribute(String name) {
        this.cache.remove(name);
    };
    /**清空内置对象中的属性，该方法不会影响到固定的内置属性（它们的优先级仍然是最高的）。*/
    public void clearAttribute() {
        this.cache.clear();
    };
    /*------------------------------------------------------------------------*/
    /**将一组原始的配置信息添加到Global中，如果存在已经使用的名称则追加。*/
    public void addScope(Class<? extends Enum<?>> enumType, Map config) {
        this.addScope(enumType, new TransformToAttribute<Object>(config));
    };
    /**将一组原始的配置信息添加到Global中，如果存在已经使用的名称则追加。*/
    public void addScope(String name, Map config) {
        this.addScope(name, new TransformToAttribute<Object>(config));
    };
    /**将一组原始的配置信息添加到Global中，如果存在已经使用的名称则追加。*/
    public void addScope(Class<? extends Enum<?>> enumType, IAttribute<Object> config) {
        if (enumType == null || config == null)
            throw new NullPointerException("‘enumType’ or ‘config’ param is null");
        this.addScope(enumType.getName(), config);
    };
    /**将一组原始的配置信息添加到Global中，如果存在已经使用的名称则追加。*/
    public void addScope(String name, IAttribute<Object> config) {
        if (name == null || config == null)
            throw new NullPointerException("‘name’ or ‘config’ param is null");
        if (this.scopeMap.containsKey(name) == false) {
            //新增了一条，同时将这条增加到allData中
            SequenceStack<Object> stack = new SequenceStack<Object>();
            stack.putStack(config);
            this.scopeMap.put(name, stack);
        } else {
            IAttribute<?> stack = this.scopeMap.get(name);
            if (stack instanceof SequenceStack == false)
                throw new SupportException("‘" + name + "’ scope does not support more of the same.");
            SequenceStack<Object> $stack = (SequenceStack<Object>) stack;
            $stack.putStack(config);
        }
    };
    /**按照添加顺序的位置获取一个作用域*/
    public IAttribute<Object> getScope(int index) {
        ArrayList<IAttribute<Object>> list = new ArrayList<IAttribute<Object>>();
        for (SequenceStack<Object> attSeq : this.scopeMap.values())
            for (IAttribute<Object> att : attSeq.getList())
                list.add(att);
        return list.get(index);
    };
    /**按照添加的名称获取一个作用域*/
    public IAttribute<Object> getScope(String name) {
        return this.scopeMap.get(name);
    };
    /**如果想获取重名作用域其中一项的请使用该方法。*/
    public IAttribute<Object> getScope(String name, int index) {
        IAttribute<Object> temp = this.getScope(name);
        if (temp instanceof SequenceStack == false)
            return temp;
        SequenceStack<Object> stack = (SequenceStack<Object>) temp;
        return stack.getIndex(index);
    };
    /**返回一共增加了多少个config分组设置，这里包括<b>内置属性</b>。所以该值应该大于等于<b>1</b>。*/
    public int getConfigGroupCount() {
        return this.scopeMap.size();
    };
    /**返回一共有多少条配置。其中包含<b>内置属性</b>。*/
    public int getConfigItemCount() {
        return this.getALLRoot().size();
    };
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
            elStr.deleteCharAt(elStr.length() - 1);
            lastChar = elStr.charAt(elStr.length() - 1);//去掉尾部的分号
        }
        //3.判断类型
        elStr.deleteCharAt(0);
        if (elStr.length() > 1)
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
    };
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
            return Ognl.getValue(elString, this.transformToOgnlContext());
        } catch (OgnlException e) {
            throw new FormatException("expression ‘" + elString + "’ error.");
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
        if (elString.matches(".*\\$\\{.*\\}.*") == false)
            return this.$evalString(elString);
        //TODO:目前版本暂不支持包含EL表达式的字符串解析。以后可以考虑使用JavaCC或者正则表达式进行解析。
        throw new SupportException("目前版本暂不支持包含EL表达式的字符串解析。");
        //return this.$evalEL(elString);//执行el
    };
    /*------------------------------------------------------------------------*/
    /**起缓存作用*/
    private static final HashMap<String, Class<?>> globalFactoryMap = new HashMap<String, Class<?>>();
    /**创建默认的{@link Global}对象。*/
    public static Global newInstance() throws IOException, ClassNotFoundException {
        return newInstance((Object) null);
    };
    /**创建默认的{@link Global}对象，参数是{@link GlobalFactory}在创建{@link Global}时候传入的参数。*/
    public static Global newInstance(Object... params) throws IOException, ClassNotFoundException {
        return newInstanceByFactory("properties", params);
    };
    /**创建{@link Global}对象，参数是{@link GlobalFactory}在创建{@link Global}时候传入的参数。factoryName是指定注册的{@link GlobalFactory}。*/
    public static Global newInstanceByFactory(String factoryName, Object... params) throws IOException, ClassNotFoundException {
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
            Global global = globalFactory.createGlobal(params);
            //XXX:可以装载内置属性
            return global;
        } catch (Throwable e) {
            throw new InitializationException("init error can`t create type " + globalFactoryType);
        }
    };
    /**创建一个{@link Global}本体实例化对象。*/
    public static Global newInterInstance(IAttribute<Object> configs) {
        return new Global(configs) {};
    };
    /**创建一个{@link Global}本体实例化对象。*/
    public static Global newInterInstance() {
        return new Global(null) {};
    };
};