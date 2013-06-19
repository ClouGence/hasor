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
package org.more.hypha.utils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang.StringUtils;
import org.more.hypha.ApplicationContext;
import org.more.hypha.define.BeanDefine;
import org.more.hypha.define.MethodDefine;
import org.more.hypha.define.ParamDefine;
import org.more.hypha.define.PropertyDefine;
/**
 * 
 *
 * @version : 2012-5-15
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class DefineUtils {
    /**返回bean的唯一编号，如果没有指定id属性则id值将是fullName属性值。*/
    public static String getFullName(BeanDefine define) {
        if (define == null)
            throw new NullPointerException("param BeanDefine is null.");
        //
        String _id = StringUtils.trimToNull(define.getId());
        String _name = StringUtils.trimToNull(define.getName());
        String _scope = StringUtils.trimToNull(define.getScope());
        //
        if (_id != null)
            return _id;
        return (_scope != null) ? _scope + "." + _name : _name;
    }
    /**获取方法的定义，该方法只会在当前定义中查找。*/
    public static MethodDefine getDeclaredMethod(BeanDefine define, String name) {
        if (define == null)
            throw new NullPointerException("param BeanDefine is null.");
        //
        return define.getMethods().get(name);
    };
    /**获取方法的定义，如果当前定义中没有声明则自动到使用的模板中查找。依次类推直到模板返回为空。*/
    public static MethodDefine getMethod(ApplicationContext context, BeanDefine define, String name) {
        if (define == null)
            throw new NullPointerException("param BeanDefine is null.");
        //
        MethodDefine md = getDeclaredMethod(define, name);
        if (md != null)
            return md;
        String _useTemplate = StringUtils.trimToNull(define.getUseTemplate());
        if (_useTemplate != null) {
            BeanDefine templateBeanDefine = context.getBeanDefinition(_useTemplate);
            if (templateBeanDefine != null)
                return getMethod(context, templateBeanDefine, name);
        }
        return null;
    };
    /**获取当前定义中声明的方法列表，返回的结果不包括使用的模板中的方法声明。*/
    public static Map<String, MethodDefine> getDeclaredMethods(BeanDefine define) {
        if (define == null)
            throw new NullPointerException("param BeanDefine is null.");
        //
        return define.getMethods();
    };
    /**获取当前定义中可用的方法声明集合，包含了继承模板中的方法。*/
    public static Map<String, MethodDefine> getMethods(ApplicationContext context, BeanDefine define) {
        if (define == null)
            throw new NullPointerException("param BeanDefine is null.");
        //
        HashMap<String, MethodDefine> ms = new HashMap<String, MethodDefine>();
        ms.putAll(define.getMethods());
        //
        String _useTemplate = StringUtils.trimToNull(define.getUseTemplate());
        if (_useTemplate != null) {
            BeanDefine templateBeanDefine = context.getBeanDefinition(_useTemplate);
            if (templateBeanDefine != null) {
                Map<String, MethodDefine> templateMethods = getMethods(context, templateBeanDefine);
                for (Entry<String, MethodDefine> ent : templateMethods.entrySet())
                    if (ms.containsKey(ent.getKey()) == false)
                        ms.put(ent.getKey(), ent.getValue());
            }
        }
        return ms;
    };
    /**获取属性定义，该方法只会在当前定义中查找。*/
    public static PropertyDefine getDeclaredProperty(BeanDefine define, String name) {
        if (define == null)
            throw new NullPointerException("param BeanDefine is null.");
        //
        return define.getPropertys().get(name);
    };
    /**获取属性定义，如果当前定义中没有声明则自动到使用的模板中查找。依次类推直到模板返回为空。*/
    public static PropertyDefine getProperty(ApplicationContext context, BeanDefine define, String name) {
        if (define == null)
            throw new NullPointerException("param BeanDefine is null.");
        //
        PropertyDefine pd = getDeclaredProperty(define, name);
        if (pd != null)
            return pd;
        String _useTemplate = StringUtils.trimToNull(define.getUseTemplate());
        if (_useTemplate != null) {
            BeanDefine templateBeanDefine = context.getBeanDefinition(_useTemplate);
            if (templateBeanDefine != null)
                return getProperty(context, templateBeanDefine, name);
        }
        return null;
    };
    /**获取当前定义中声明的属性列表，返回的结果不包括使用的模板中的属性声明。*/
    public static Map<String, PropertyDefine> getDeclaredPropertys(BeanDefine define) {
        if (define == null)
            throw new NullPointerException("param BeanDefine is null.");
        //
        return define.getPropertys();
    };
    /**获取当前定义中可用的属性声明集合，包含了继承模板中的属性。*/
    public static Map<String, PropertyDefine> getPropertys(ApplicationContext context, BeanDefine define) {
        if (define == null)
            throw new NullPointerException("param BeanDefine is null.");
        //
        HashMap<String, PropertyDefine> ms = new HashMap<String, PropertyDefine>();
        ms.putAll(define.getPropertys());
        //
        String _useTemplate = StringUtils.trimToNull(define.getUseTemplate());
        if (_useTemplate != null) {
            BeanDefine templateBeanDefine = context.getBeanDefinition(_useTemplate);
            if (templateBeanDefine != null) {
                Map<String, PropertyDefine> templateMethods = getPropertys(context, templateBeanDefine);
                for (Entry<String, PropertyDefine> ent : templateMethods.entrySet())
                    if (ms.containsKey(ent.getKey()) == false)
                        ms.put(ent.getKey(), ent.getValue());
            }
        }
        return ms;
    };
    /**添加一个启动参数，被添加的启动参数会自动进行排序。*/
    public static void addInitParam(BeanDefine define, int index, ParamDefine paramDefine) {
        if (define == null)
            throw new NullPointerException("param BeanDefine is null.");
        //
        List<ParamDefine> paramList = define.getInitParams();
        paramList.add(index, paramDefine);
        for (int i = 0; i < paramList.size(); i++)
            paramList.get(i).setIndex(i);
    };
    /**添加一个启动参数，被添加的启动参数会自动进行排序。*/
    public static void addInitParam(BeanDefine define, ParamDefine paramDefine) {
        if (define == null)
            throw new NullPointerException("param BeanDefine is null.");
        //
        List<ParamDefine> paramList = define.getInitParams();
        paramList.add(paramDefine);
        paramDefine.setIndex(paramList.indexOf(paramDefine));
        define.setInitParams(paramList);
    };
    /**添加一个属性。*/
    public static void addProperty(BeanDefine define, PropertyDefine propertyDefine) {
        if (define == null)
            throw new NullPointerException("param BeanDefine is null.");
        //
        define.getPropertys().put(propertyDefine.getName(), propertyDefine);
    };
    /**添加一个方法描述。*/
    public static void addMethod(BeanDefine define, MethodDefine methodDefine) {
        if (define == null)
            throw new NullPointerException("param BeanDefine is null.");
        //
        define.getMethods().put(methodDefine.getName(), methodDefine);
    };
    /**返回方法的完整描述。*/
    public static String getMethodDesc(MethodDefine methodDefine) {
        //Type0 method(Type1,Type2)
        StringBuffer descBuffer = new StringBuffer();
        descBuffer.append(methodDefine.getReturnType());
        descBuffer.append(" ");
        descBuffer.append(methodDefine.getName());
        descBuffer.append("(");
        List<ParamDefine> params = methodDefine.getParams();
        for (int i = 0; i < params.size(); i++)
            descBuffer.append(params.get(i).getClassType() + ",");
        if (descBuffer.charAt(descBuffer.length()) == ',')
            descBuffer = descBuffer.deleteCharAt(descBuffer.length());
        descBuffer.append(")");
        return descBuffer.toString();
    };
    /**添加一个方法参数，被添加的方法参数会自动进行排序。*/
    public static void addMethodParam(MethodDefine methodDefine, int index, ParamDefine paramDefine) {
        if (methodDefine == null || paramDefine == null)
            throw new NullPointerException("param methodDefine or paramDefine is null.");
        //
        List<ParamDefine> paramList = methodDefine.getParams();
        paramList.add(index, paramDefine);
        for (int i = 0; i < paramList.size(); i++)
            paramList.get(i).setIndex(i);
    };
    /**添加一个方法参数，被添加的方法参数会自动进行排序。*/
    public static void addMethodParam(MethodDefine methodDefine, ParamDefine paramDefine) {
        if (methodDefine == null || paramDefine == null)
            throw new NullPointerException("param methodDefine or paramDefine is null.");
        //
        List<ParamDefine> paramList = methodDefine.getParams();
        paramList.add(paramDefine);
        paramDefine.setIndex(paramList.indexOf(paramDefine));
        methodDefine.setParams(paramList);
    };
}