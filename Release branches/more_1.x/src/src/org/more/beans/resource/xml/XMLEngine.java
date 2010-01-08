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
import java.util.HashMap;
import java.util.Properties;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.more.InitializationException;
import org.more.InvokeException;
import org.more.core.io.AutoCloseInputStream;
import org.more.util.attribute.AttBase;
/**
 * XML解析引擎，开发人员可以通过实现DoEventIteration接口使用runTask方法来执行扩展的xml任务。
 * @version 2009-11-23
 * @author 赵永春 (zyc@byshell.org)
 */
public class XMLEngine extends AttBase {
    /**  */
    private static final long             serialVersionUID = 2880738501389974190L;
    //========================================================================================Field
    protected HashMap<String, TagProcess> tagProcessMap    = new HashMap<String, TagProcess>(); //标签处理对象。
    protected HashMap<String, Class<?>>   taskProcessMap   = new HashMap<String, Class<?>>();  //任务处理对象
    //==================================================================================Constructor
    /**创建XMLEngine对象。*/
    public XMLEngine() {
        try {
            Class<?> type = XMLEngine.class;
            InputStream in = type.getResourceAsStream("/org/more/beans/resource/xml/core/tagProcess.properties");
            Properties pro = new Properties();
            pro.load(new AutoCloseInputStream(in));//装载属性文件
            for (Object key : pro.keySet()) {
                String kn = key.toString();
                String classType = pro.getProperty(kn);
                tagProcessMap.put(kn, (TagProcess) Class.forName(classType).newInstance());
            }
            /*---------------*/
            in = type.getResourceAsStream("/org/more/beans/resource/xml/core/taskProcess.properties");
            pro = new Properties();
            pro.load(new AutoCloseInputStream(in));//装载属性文件
            for (Object key : pro.keySet()) {
                String kn = key.toString();
                String classType = pro.getProperty(kn);
                taskProcessMap.put(kn, Class.forName(classType));
            }
        } catch (Exception e) {
            throw new InitializationException("无法初始化XMLEngine对象，在装载或处理资源文件时发生异常。msg=" + e.getMessage());
        }
    }
    //========================================================================================Event
    /**扫描XML，processXPath是要处理的xpath匹配正则表达式。*/
    protected Object scanningXML(InputStream in, String processXPath, TaskProcess doEventIteration) throws Exception {
        XMLStreamReader reader = this.getXMLStreamReader(in);
        ContextStack stack = null;//当前堆栈
        String onTag = null;//当前标签
        int event = reader.getEventType();//当前事件对象
        while (true) {
            switch (event) {
            case XMLStreamConstants.START_DOCUMENT://文档开始
                stack = new ContextStack(null, null, "/");
                doEventIteration.onEvent(stack, stack.getXPath(), event, reader, tagProcessMap);
                break;
            case XMLStreamConstants.END_DOCUMENT://文档结束
                doEventIteration.onEvent(stack, stack.getXPath(), event, reader, tagProcessMap);
                break;
            case XMLStreamConstants.START_ELEMENT://元素开始
                onTag = reader.getLocalName();
                stack = new ContextStack(stack, onTag, stack.getXPath() + onTag + "/");
                doEventIteration.onEvent(stack, stack.getXPath(), event, reader, tagProcessMap);
                int attCount = reader.getAttributeCount();
                for (int i = 0; i < attCount; i++) {//遇到属性
                    String key = reader.getAttributeLocalName(i);
                    stack.attValue = reader.getAttributeValue(i);
                    doEventIteration.onEvent(stack, stack.getXPath() + "@" + key, XMLStreamConstants.ATTRIBUTE, reader, tagProcessMap);
                    stack.attValue = null;
                }
                break;
            case XMLStreamConstants.END_ELEMENT://元素结束
                doEventIteration.onEvent(stack, stack.getXPath(), event, reader, tagProcessMap);
                stack = stack.getParent();
                break;
            case XMLStreamConstants.CDATA://解析到CDATA
                doEventIteration.onEvent(stack, stack.getXPath(), event, reader, tagProcessMap);
                break;
            case XMLStreamConstants.CHARACTERS://解析到字符
                doEventIteration.onEvent(stack, stack.getXPath(), event, reader, tagProcessMap);
                break;
            }
            if (reader.hasNext() == false)
                break;
            event = reader.next();
        }
        return doEventIteration.getResult();
    }
    /**获取Stax阅读器，该阅读器忽略所有COMMENT节点。*/
    private XMLStreamReader getXMLStreamReader(InputStream in) throws XMLStreamException {
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
        return reader;
    }
    //==========================================================================================Job
    /**执行XML任务。*/
    public Object runTask(InputStream xmlStream, TaskProcess task, String processXPath, Object... params) throws InvokeException {
        try {
            task.setConfig(params);
            return this.scanningXML(xmlStream, processXPath, task);
        } catch (Exception e) {
            throw new InvokeException(e);
        }
    }
    /**执行XML任务。*/
    public Object runTask(InputStream xmlStream, String taskName, String processXPath, Object... params) throws InvokeException {
        try {
            TaskProcess task = (TaskProcess) this.taskProcessMap.get(taskName).newInstance();
            task.setConfig(params);
            return this.scanningXML(xmlStream, processXPath, task);
        } catch (Exception e) {
            throw new InvokeException(e);
        }
    }
}