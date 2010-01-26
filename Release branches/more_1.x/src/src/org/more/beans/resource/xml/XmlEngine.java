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
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.more.util.attribute.AttBase;
/**
 * XML解析引擎，开发人员可以通过实现DoEventIteration接口使用runTask方法来执行扩展的xml任务。
 * @version 2010-1-11
 * @author 赵永春 (zyc@byshell.org)
 */
public class XmlEngine extends AttBase {
    /**  */
    private static final long           serialVersionUID = 2880738501389974190L;
    //========================================================================================Field
    private HashMap<String, TagProcess> tagProcessMap    = new HashMap<String, TagProcess>(); //标签处理对象。 ns:tagname
    //========================================================================================Event
    /**扫描XML，processXPath是要处理的xpath匹配正则表达式。*/
    protected Object scanningXML(InputStream in, String processXPath, TaskProcess doEventIteration) throws Exception {
        XMLStreamReader reader = this.getXMLStreamReader(in);
        XmlContextStack stack = null;//当前堆栈
        String tagPrefix = null;//当前标签命名空间
        String tagName = null;//当前标签
        int event = reader.getEventType();//当前事件对象
        while (true) {
            switch (event) {
            case XMLStreamConstants.START_DOCUMENT://文档开始
                stack = new XmlContextStack(null, null, null, "/");
                doEventIteration.onEvent(stack, stack.getXPath(), event, reader, tagProcessMap);
                break;
            case XMLStreamConstants.END_DOCUMENT://文档结束
                doEventIteration.onEvent(stack, stack.getXPath(), event, reader, tagProcessMap);
                break;
            case XMLStreamConstants.START_ELEMENT://元素开始
                tagPrefix = reader.getPrefix();
                tagName = reader.getLocalName();
                if (tagPrefix == null || tagPrefix.equals(""))
                    stack = new XmlContextStack(stack, tagPrefix, tagName, stack.getXPath() + tagName + "/");
                else
                    stack = new XmlContextStack(stack, tagPrefix, tagName, stack.getXPath() + tagPrefix + ":" + tagName + "/");
                doEventIteration.onEvent(stack, stack.getXPath(), event, reader, tagProcessMap);
                int attCount = reader.getAttributeCount();
                for (int i = 0; i < attCount; i++) {//遇到属性
                    String attPrefix = reader.getAttributeNamespace(i);
                    String attName = reader.getAttributeLocalName(i);
                    stack.attValue = reader.getAttributeValue(i);
                    String xpath_temp;
                    if (attPrefix == null || attPrefix.equals(""))
                        xpath_temp = stack.getXPath() + "@" + attName;
                    else
                        xpath_temp = stack.getXPath() + "@" + attPrefix + ":" + attName;
                    doEventIteration.onEvent(stack, xpath_temp, XMLStreamConstants.ATTRIBUTE, reader, tagProcessMap);
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
    };
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
    };
    //==========================================================================================Job
    /**执行XML任务。*/
    public Object runTask(InputStream xmlStream, TaskProcess task, String processXPath, Object[] params) throws Exception {
        task.setConfig(params);
        task.init();
        return this.scanningXML(xmlStream, processXPath, task);
    };
    /**注册一个标签处理程序*/
    public void regeditTag(String tagName, TagProcess tag) {
        if (this.tagProcessMap.containsKey(tagName) == false)
            this.tagProcessMap.put(tagName, tag);
    }
    public TagProcess getTagProcess(String tagName) {
        return this.tagProcessMap.get(tagName);
    }
    public void destroy() {
        this.tagProcessMap.clear();
    }
}