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
package org.more.beans.resource.xml;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.more.beans.info.BeanDefinition;
/**
 * 
 * <br/>Date : 2009-11-23
 * @author Administrator
 */
@SuppressWarnings("unchecked")
public class XMLEngine {
    //========================================================================================Field
    private HashMap<String, DoTagEvent>     doEventMap       = new HashMap<String, DoTagEvent>();    //
    private int                             staticCacheSize  = 10;                                   //
    private HashMap<String, BeanDefinition> staticCache      = new HashMap<String, BeanDefinition>(); //静态缓存
    private int                             dynamicCacheSize = 50;                                   //
    private ArrayList                       initBeanNS       = null;                                 //要求初始化的bean名称。
    private ArrayList                       allBeanNS        = null;                                 //所有bean名称。
    //==================================================================================Constructor
    public XMLEngine() {
        doEventMap.put("bean", new Tag_Bean());
        doEventMap.put("addImpl", new Tag_AddImpl());
        doEventMap.put("factoryConfig", new Tag_FactoryConfig());
        doEventMap.put("beans", new Tag_Beans());
        doEventMap.put("property", new Tag_Property());
        doEventMap.put("constructor-arg", new Tag_ConstructorArg());
        doEventMap.put("methodParam", new Tag_MethodParam());
        doEventMap.put("array", new Tag_Array());
        doEventMap.put("list", new Tag_List());
        doEventMap.put("set", new Tag_Set());
        doEventMap.put("value", new Tag_Value());
        doEventMap.put("meta", new Tag_Meta());
    }
    //==========================================================================================JOB
    /**processXPath是要处理的XPATH正则表达式*/
    public void scanningXML(InputStream in, String processXPath) throws XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(in);
        reader = factory.createFilteredReader(reader, new StreamFilter() {
            @Override
            public boolean accept(XMLStreamReader reader) {
                int event = reader.getEventType();
                if (event == XMLStreamConstants.COMMENT)
                    return false;
                else
                    return true;
            }
        });
        //==================================================================
        ContextStack stack = null;
        int event = reader.getEventType();
        DoTagEvent doEvent = null;
        String loaclName = null;
        while (true) {
            switch (event) {
            case XMLStreamConstants.START_DOCUMENT:
                stack = new ContextStack(null, null, "/");
                break;
            case XMLStreamConstants.END_DOCUMENT:
                this.staticCacheSize = (Integer) stack.get("staticCatch");
                this.dynamicCacheSize = (Integer) stack.get("dynamicCache");
                this.initBeanNS = (ArrayList) stack.get("initBeanNS");
                this.allBeanNS = (ArrayList) stack.get("allBeanNS");
                ArrayList al = (ArrayList) stack.context;
                for (Object obj : al) {
                    BeanDefinition bd = (BeanDefinition) obj;
                    this.staticCache.put(bd.getName(), bd);
                }
                stack = null;
                break;
            case XMLStreamConstants.START_ELEMENT:
                loaclName = reader.getLocalName();
                String xpath = stack.getXPath() + loaclName + "/";
                if (xpath.matches(processXPath) == false)
                    break;
                stack = new ContextStack(stack, loaclName, xpath);
                doEvent = doEventMap.get(loaclName);
                if (doEvent != null)
                    doEvent.doStartEvent(stack.getXPath(), reader, stack);
                break;
            case XMLStreamConstants.END_ELEMENT:
                loaclName = reader.getLocalName();
                doEvent = doEventMap.get(loaclName);
                if (doEvent != null)
                    doEvent.doEndEvent(stack.getXPath(), reader, stack);
                stack = stack.getParent();
                break;
            case XMLStreamConstants.CDATA:
                doEvent = doEventMap.get(loaclName);
                if (doEvent != null)
                    doEvent.doCharEvent(stack.getXPath(), reader, stack);
                break;
            case XMLStreamConstants.CHARACTERS:
                doEvent = doEventMap.get(loaclName);
                if (doEvent != null)
                    doEvent.doCharEvent(stack.getXPath(), reader, stack);
                break;
            }
            if (reader.hasNext() == false)
                break;
            event = reader.next();
        }
    }
    /**启动引擎查找名称为name的bean*/
    public BeanDefinition findBeanDefinition(String name) {
        return null;
    }
    /**启动引擎查找bean名称为name的attName属性值*/
    public String getPath(String xpath) {
        return null;
    }
    /**启动引擎查找bean名称为name的attName属性值*/
    public boolean testPath(String xPath) {
        return false;
    }
}