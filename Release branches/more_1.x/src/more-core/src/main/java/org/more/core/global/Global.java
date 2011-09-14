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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.more.core.error.FormatException;
import org.more.core.error.SupportException;
import org.more.core.io.AutoCloseInputStream;
import org.more.core.ognl.Ognl;
import org.more.core.ognl.OgnlException;
import org.more.util.ResourcesUtil;
import org.more.util.StringConvertUtil;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
import org.more.util.attribute.SequenceStack;
import org.more.util.attribute.TransformToMap;
/**
* 
* @version : 2011-9-3
* @author 赵永春 (zyc@byshell.org)
*/
public abstract class Global implements IAttribute<Object> {
    private Map<String, IAttribute<String>> listConfigAtt = null;
    private SequenceStack<String>           allConfigAtt  = null;
    private Object                          context       = null;
    /*------------------------------------------------------------------------*/
    public Global() {
        this.listConfigAtt = new HashMap<String, IAttribute<String>>();
        this.allConfigAtt = new SequenceStack<String>();
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
    /**解析全局配置参数，并且返回其{@link Character}形式对象。*/
    public Character getChar(Enum<?> name) {
        return this.getToType(name, Character.class);
    };
    /**解析全局配置参数，并且返回其{@link Character}形式对象。*/
    public Character getChar(String name) {
        return this.getToType(name, Character.class);
    };
    /**解析全局配置参数，并且返回其{@link String}形式对象。*/
    public String getString(Enum<?> name) {
        return this.getToType(name, String.class);
    };
    /**解析全局配置参数，并且返回其{@link String}形式对象。*/
    public String getString(String name) {
        return this.getToType(name, String.class);
    };
    /**解析全局配置参数，并且返回其{@link Boolean}形式对象。*/
    public Boolean getBoolean(Enum<?> name) {
        return this.getToType(name, Boolean.class);
    };
    /**解析全局配置参数，并且返回其{@link Boolean}形式对象。*/
    public Boolean getBoolean(String name) {
        return this.getToType(name, Boolean.class);
    };
    /**解析全局配置参数，并且返回其{@link Short}形式对象。*/
    public Short getShort(Enum<?> name) {
        return this.getToType(name, Short.class);
    };
    /**解析全局配置参数，并且返回其{@link Short}形式对象。*/
    public Short getShort(String name) {
        return this.getToType(name, Short.class);
    };
    /**解析全局配置参数，并且返回其{@link Integer}形式对象。*/
    public Integer getInteger(Enum<?> name) {
        return this.getToType(name, Integer.class);
    };
    /**解析全局配置参数，并且返回其{@link Integer}形式对象。*/
    public Integer getInteger(String name) {
        return this.getToType(name, Integer.class);
    };
    /**解析全局配置参数，并且返回其{@link Long}形式对象。*/
    public Long getLong(Enum<?> name) {
        return this.getToType(name, Long.class);
    };
    /**解析全局配置参数，并且返回其{@link Long}形式对象。*/
    public Long getLong(String name) {
        return this.getToType(name, Long.class);
    };
    /**解析全局配置参数，并且返回其{@link Float}形式对象。*/
    public Float getFloat(Enum<?> name) {
        return this.getToType(name, Float.class);
    };
    /**解析全局配置参数，并且返回其{@link Float}形式对象。*/
    public Float getFloat(String name) {
        return this.getToType(name, Float.class);
    };
    /**解析全局配置参数，并且返回其{@link Double}形式对象。*/
    public Double getDouble(Enum<?> name) {
        return this.getToType(name, Double.class);
    };
    /**解析全局配置参数，并且返回其{@link Double}形式对象。*/
    public Double getDouble(String name) {
        return this.getToType(name, Double.class);
    };
    /**解析全局配置参数，并且返回其{@link Date}形式对象。*/
    public Date getDate(Enum<?> name) {
        return this.getToType(name, Date.class);
    };
    /**解析全局配置参数，并且返回其{@link Date}形式对象。*/
    public Date getDate(String name) {
        return this.getToType(name, Date.class);
    };
    public boolean contains(String name) {
        return this.allConfigAtt.contains(name);
    }
    public Object getAttribute(String name) {
        return this.getObject(name);
    }
    public Map<String, Object> toMap() {
        return new TransformToMap<Object>(this);
    }
    public String[] getAttributeNames() {
        return this.allConfigAtt.getAttributeNames();
    }
    public void setAttribute(String name, Object value) {
        throw new SupportException("Global，不支持该方法。");
    }
    public void removeAttribute(String name) {
        throw new SupportException("Global，不支持该方法。");
    }
    public void clearAttribute() {
        throw new SupportException("Global，不支持该方法。");
    }
    /*------------------------------------------------------------------------*/
    /**视图访问以该枚举名称命名的属性文件或绑定在该枚举上的属性文件，如果尝试访问的属性文件不存在则规则与{@link #getOriginalString(String)}相同。*/
    public String getOriginalString(Enum<?> name) {
        if (name == null)
            return null;
        //1.取得名称
        String simpleName = name.getClass().getSimpleName();
        //2.从list中获取Config
        IAttribute<String> att = null;
        if (this.listConfigAtt.containsKey(simpleName) == false)
            att = this.allConfigAtt;
        else
            att = this.listConfigAtt.get(simpleName);
        return att.getAttribute(name.name());
    };
    /**按照序列顺序在所有加入的全局配置文件中寻找指定名称的属性值，并且将原始信息返回。*/
    public String getOriginalString(String name) {
        if (name == null)
            return null;
        return this.allConfigAtt.getAttribute(name);
    };
    /**将一组原始的配置信息添加到列表中，如果存在已经使用的名称则覆盖。*/
    protected void addConfig(String name, IAttribute<String> config) {
        this.listConfigAtt.put(name, config);
    };
    /**检测name表示的配置信息是否已经注册。*/
    protected boolean addConfig(String name) {
        return this.listConfigAtt.containsKey(name);
    };
    /**解析全局配置参数，并且返回toType参数指定的类型。*/
    public <T> T getToType(Enum<?> enumItem, Class<T> toType) {
        String oriString = this.getOriginalString(enumItem);
        if (oriString == null)
            return null;
        //
        Object var = this.evalEL(oriString);
        return StringConvertUtil.changeType(var, toType);
    };
    /**解析全局配置参数，并且返回toType参数指定的类型。*/
    public <T> T getToType(String name, Class<T> toType) {
        String oriString = this.getOriginalString(name);
        if (oriString == null)
            return null;
        //
        Object var = this.evalEL(oriString);
        return StringConvertUtil.changeType(var, toType);
    };
    /*------------------------------------------------------------------------*/
    /**绑定一个枚举到一个配置文件上，如果这个枚举配置了{@link PropFile}注解则使用该注解标记的属性文件进行装载，否则就装载与枚举名同名的属性文件。*/
    public void addEnum(Class<? extends Enum<?>> enumType) throws Throwable {
        PropFile pFile = enumType.getAnnotation(PropFile.class);
        if (pFile != null)
            if (pFile.file().equals("") == false)
                this.addResource(enumType, new File(pFile.file()));
            else if (pFile.uri().equals("") == false)
                this.addResource(enumType, new URI(pFile.uri()));
            else if (pFile.value().equals("") == false)
                this.addResource(enumType, pFile.value());
            else
                this.addResource(enumType, enumType.getSimpleName());
        this.addResource(enumType, enumType.getSimpleName());
    };
    /**添加一个配置文件，并且绑定到指定的枚举上。*/
    public void addResource(Class<? extends Enum<?>> enumType, String resource) throws IOException {
        InputStream stream = ResourcesUtil.getResourceAsStream(resource);
        IAttribute<String> att = this.loadConfig(stream);
        this.addAttribute(enumType, att);
    };
    /**添加一个配置文件，并且绑定到指定的枚举上。*/
    public void addResource(Class<? extends Enum<?>> enumType, URI resource) throws MalformedURLException, IOException {
        IAttribute<String> att = this.loadConfig(new AutoCloseInputStream(resource.toURL().openStream()));
        this.addAttribute(enumType, att);
    };
    /**添加一个配置文件，并且绑定到指定的枚举上。*/
    public void addResource(Class<? extends Enum<?>> enumType, File resource) throws IOException {
        IAttribute<String> att = this.loadConfig(new AutoCloseInputStream(new FileInputStream(resource)));
        this.addAttribute(enumType, att);
    };
    /*------------------------------------------------------------------------*/
    /**解析流对象，并且将解析的结果返回为{@link IAttribute}接口形式。*/
    protected abstract IAttribute<String> loadConfig(InputStream stream) throws IOException;
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
        AttBase<Object> root = new AttBase<Object>();
        root.put("context", this.context);
        return root;
    };
    /**添加到集合中*/
    private void addAttribute(Class<? extends Enum<?>> enumType, IAttribute<String> att) {
        String name = enumType.getSimpleName();
        this.listConfigAtt.put(name, att);
        this.allConfigAtt.putStack(att);
    }
    //
    //
    private <T> T evalEL(String elString) {
        //1.整理elString
        elString = elString;
        //2.解析elString
        try {
            return (T) Ognl.getValue(elString, this.getRoot());
        } catch (OgnlException e) {
            throw new FormatException(elString + "：作为EL解析错误。");
        }
    };
    /*------------------------------------------------------------------------*/
    public static Global createForXml(String xmlPath) throws IOException {
        return createForXml(xmlPath, null);
    };
    public static Global createForFile(String propPath) throws IOException {
        return createForFile(propPath, null);
    };
    public static Global createForXml(String xmlPath, Object context) throws IOException {
        XmlGlobal xml = new XmlGlobal();
        xml.setContext(context);
        xml.addResource(GlobalEnum.class, xmlPath);
        return xml;
    };
    public static Global createForFile(String propPath, Object context) throws IOException {
        FileGlobal file = new FileGlobal();
        file.setContext(context);
        file.addResource(GlobalEnum.class, propPath);
        return file;
    };
};
enum GlobalEnum {}