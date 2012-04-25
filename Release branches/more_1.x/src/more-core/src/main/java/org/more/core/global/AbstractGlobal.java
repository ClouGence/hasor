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
import java.util.Map;
import org.more.core.error.FormatException;
import org.more.core.error.SupportException;
import org.more.core.iatt.Attribute;
import org.more.core.iatt.IAttribute;
import org.more.core.iatt.TransformToAttribute;
import org.more.core.json.JsonUtil;
import org.more.core.ognl.Node;
import org.more.core.ognl.Ognl;
import org.more.core.ognl.OgnlContext;
import org.more.core.ognl.OgnlException;
import org.more.util.StringConvertUtil;
/**
 * Global系统的核心实现
 * @version : 2011-12-31
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractGlobal implements IAttribute<Object> {
    /*------------------------------------------------------------------------*/
    private IAttribute<Object> targetContainer = new Attribute<Object>();
    public AbstractGlobal() {};
    public AbstractGlobal(Map<String, Object> configs) {
        this(new TransformToAttribute<Object>(configs));
    };
    public AbstractGlobal(IAttribute<Object> configs) {
        this.targetContainer = configs;
    };
    /**子类可以重写该方法以替换targetContainer属性容器。*/
    protected IAttribute<Object> getAttContainer() {
        return targetContainer;
    }
    /*------------------------------------------------------------------------*/
    @Override
    public boolean contains(String name) {
        return this.getAttContainer().contains(name);
    }
    @Override
    public void setAttribute(String name, Object value) {
        this.getAttContainer().setAttribute(name, value);
    }
    @Override
    public Object getAttribute(String name) {
        return this.getAttContainer().getAttribute(name);
    }
    @Override
    public void removeAttribute(String name) {
        this.getAttContainer().removeAttribute(name);
    }
    @Override
    public String[] getAttributeNames() {
        return this.getAttContainer().getAttributeNames();
    }
    @Override
    public void clearAttribute() {
        this.getAttContainer().clearAttribute();
    }
    @Override
    public Map<String, Object> toMap() {
        return this.getAttContainer().toMap();
    }
    @Override
    public void putMap(Map<String, Object> params) {
        this.getAttContainer().putMap(params);
    }
    @Override
    public int size() {
        return this.getAttContainer().size();
    }
    /*------------------------------------------------------------------------*/
    private OgnlContext ognlContext = null;
    private OgnlContext transformToOgnlContext() {
        if (this.ognlContext == null) {
            this.ognlContext = new OgnlContext(this.toMap());
        }
        return this.ognlContext;
    };
    /**是否启用el表达式解析。*/
    public boolean isEnableEL() {
        return this.getToType("_global.enableEL", Boolean.class, false);
    }
    /**是否启用json解析*/
    public boolean isEnableJson() {
        return this.getToType("_global.enableJson", Boolean.class, false);
    }
    private Map<String, Node> cacheNode = new java.util.Hashtable<String, Node>();
    /**使用Ognl计算字符串，并且返回其计算结果。*/
    public Object evalExpression(String ognlString) throws OgnlException {
        Node expressionNode = this.cacheNode.get(ognlString);
        if (expressionNode == null) {
            expressionNode = (Node) Ognl.parseExpression(ognlString);
            this.cacheNode.put(ognlString, expressionNode);
        }
        Object oriObject = expressionNode.getValue(this.transformToOgnlContext(), this.transformToOgnlContext());
        if (oriObject instanceof String)
            return this.getEval((String) oriObject);
        else
            return oriObject;
    };
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
        Object oriObject = this.getAttribute(name);
        if (oriObject == null)
            return defaultValue;
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
    /**解析全局配置参数，并且返回toType参数指定的类型。*/
    public <T> T getToType(Enum<?> enumItem, Class<T> toType, T defaultValue) {
        return getToType(enumItem.name(), toType, defaultValue);
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
            res = this.$evalEL2(elString);//TODO
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
        if (this.isEnableEL() == false)
            return this.$evalString(elString);//不进行EL计算
        //2.解析elString
        try {
            return this.evalExpression(elString);
        } catch (OgnlException e) {
            throw new FormatException("expression ‘" + elString + "’ error.");
        }
    };
    /**在解析过程中负责解析Json串，如果_global.enableJson属性配置为false则不解析json数据。*/
    protected Object $evalJSON(String jsonString) {
        if (this.isEnableJson() == true)
            return JsonUtil.transformToObject(jsonString);//不进行计算
        else
            return this.$evalString(jsonString);
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
    /**创建一个{@link Global}本体实例化对象。*/
    public static Global newInterInstance(Map<String, Object> configs) {
        return new Global(configs) {};
    };
    /**创建一个{@link Global}本体实例化对象。*/
    public static Global newInterInstance() {
        return new Global() {};
    };
};