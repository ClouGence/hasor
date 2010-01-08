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
package org.more.beans.resource.xml.core;
import java.lang.reflect.Array;
import java.util.ArrayList;
import javax.xml.stream.XMLStreamReader;
import org.more.NoDefinitionException;
import org.more.beans.info.BeanDefinition;
import org.more.beans.info.BeanInterface;
import org.more.beans.info.BeanProperty;
import org.more.beans.info.CreateTypeEnum;
import org.more.beans.info.IocTypeEnum;
import org.more.beans.resource.xml.ContextStack;
import org.more.beans.resource.xml.TagProcess;
import org.more.util.StringConvert;
/**
 * 该类负责处理bean标签<br/>
 * id="" name="test_1" type="int" singleton="true" iocType="Export" export-refBean="abc" lazyInit="true" aopFilters-refBean="xxx,xxx,xxx"
 * @version 2009-11-21
 * @author 赵永春 (zyc@byshell.org)
 */
@SuppressWarnings("unchecked")
public class Tag_Bean extends TagProcess {
    @Override
    public void doStartEvent(String xPath, XMLStreamReader xmlReader, ContextStack context) {
        //
        BeanDefinition bean = new BeanDefinition();
        bean.setCreateType(CreateTypeEnum.New);
        //一、解析属性
        int attCount = xmlReader.getAttributeCount();
        for (int i = 0; i < attCount; i++) {
            String key = xmlReader.getAttributeLocalName(i);
            String var = xmlReader.getAttributeValue(i);
            if (key.equals("id") == true)
                bean.setId(var);
            else if (key.equals("name") == true)
                bean.setName(var);
            else if (key.equals("type") == true)
                bean.setPropType(var);
            else if (key.equals("singleton") == true)
                bean.setSingleton(StringConvert.parseBoolean(var, true));//默认单态开启
            else if (key.equals("iocType") == true)
                /* ---------- */
                if (var.equals("Ioc") == true)
                    bean.setIocType(IocTypeEnum.Ioc);
                else if (var.equals("Fact") == true)
                    bean.setIocType(IocTypeEnum.Fact);
                else if (var.equals("Export") == true)
                    bean.setIocType(IocTypeEnum.Export);
                else
                    bean.setIocType(IocTypeEnum.Ioc);
            /* ---------- */
            else if (key.equals("export-refBean") == true)
                bean.setExportRefBean(var);
            else if (key.equals("lazyInit") == true)
                bean.setLazyInit(StringConvert.parseBoolean(var, true));
            else if (key.equals("aopFilters-refBean") == true)
                bean.setAopFiltersRefBean(var.split(","));
            else
                throw new NoDefinitionException("bean标签出现未定义属性[" + key + "]");
        }
        context.context = bean;
    }
    @Override
    public void doEndEvent(String xPath, XMLStreamReader xmlReader, ContextStack context) {
        BeanDefinition bean = (BeanDefinition) context.context;
        /* 属性配置：tag_Property*/
        if (context.containsKey("tag_Property") == true) {
            ArrayList al = (ArrayList) context.get("tag_Property");
            BeanProperty[] propertys = (BeanProperty[]) this.toArray(al, BeanProperty.class);
            bean.setPropertys(propertys);
        }
        /* 工厂方法参数：tag_MethodParam*/
        if (context.containsKey("tag_MethodParam") == true) {
            ArrayList al = (ArrayList) context.get("tag_MethodParam");
            BeanProperty[] propertys = (BeanProperty[]) this.toArray(al, BeanProperty.class);
            bean.setFactoryMethodParams(propertys);
        }
        /* 构造方法参数：tag_ConstructorArg*/
        if (context.containsKey("tag_ConstructorArg") == true) {
            ArrayList al = (ArrayList) context.get("tag_ConstructorArg");
            BeanProperty[] propertys = (BeanProperty[]) this.toArray(al, BeanProperty.class);
            bean.setConstructorParams(propertys);
        }
        /* 附加接口实现：tag_AddImpl*/
        if (context.containsKey("tag_AddImpl") == true) {
            ArrayList al = (ArrayList) context.get("tag_AddImpl");
            BeanInterface[] propertys = (BeanInterface[]) this.toArray(al, BeanInterface.class);
            bean.setImplImplInterface(propertys);
        }
        //        /*------------------*/
        //        ContextStack parent = context.getParent();
        //        //保存所有bean的名称
        //        ArrayList allBeanNS = (ArrayList) parent.get("allBeanNS");
        //        allBeanNS.add(bean.getName());
        //        //保存配置lazy属性为false，并且配置了单态为true的bean名称。
        //        ArrayList initBeanNS = (ArrayList) parent.get("initBeanNS");
        //        if (bean.isLazyInit() == false && bean.isSingleton() == true)
        //            initBeanNS.add(bean.getName());
        //        //如果静态缓存中还有地方就保存一份。
        //        Integer staticCatch = (Integer) parent.get("staticCatch");
        //        Integer staticCatchCurrent = (Integer) parent.get("staticCatchCurrent");
        //        if (staticCatchCurrent < staticCatch) {
        //            ArrayList al = (ArrayList) parent.context;
        //            al.add(context.context);
        //            staticCatchCurrent++;
        //            parent.setAttribute("staticCatchCurrent", staticCatchCurrent);
        //        }
    }
    private Object[] toArray(ArrayList al, Class<?> toType) {
        Object array = Array.newInstance(toType, al.size());
        for (int i = al.size() - 1; i >= 0; i--) {
            Object obj = al.get(i);
            Array.set(array, i, toType.cast(obj));
        }
        return (Object[]) array;
    }
}