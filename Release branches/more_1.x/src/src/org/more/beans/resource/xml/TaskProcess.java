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
import java.util.Map;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
/**
 * 该接口是用于表示一个XML处理任务，查找beans、执行xpath等等任务都是DoEventIteration接口的实现类。
 * @version 2009-11-24
 * @author 赵永春 (zyc@byshell.org)
 */
public interface TaskProcess extends XMLStreamConstants {
    /**
     * xml解析引擎使用的是stax解析方式，当系统阅读到一个事件时将产生一次onEvent方法调用，eventType的值决定了是什么具体什么事件。
     * eventType值是由XMLStreamConstants接口定义的该接口是jdk1.6自带的一个接口(本接口已经继承了这个接口)。
     * 解析器使用堆栈方式来表示每一个元素节点，只有进入标签和离开标签时才会引发堆栈变动属性事件不会影响堆栈变动。
     * 你可以在解析标签时操作堆栈来保存数据，注意堆栈层级顺序是单向的。
     * @param elementStack 当前事件所处堆栈。
     * @param onXPath 当前事件发生所在的xpath路径。
     * @param eventType 事件类型。
     * @param reader 阅读器。
     * @param tagProcessMap 已经注册的标签处理器集合。
     * @throws Exception 如果在解析期间发生异常
     */
    public void onEvent(XmlContextStack elementStack, String onXPath, int eventType, XMLStreamReader reader, Map<String, TagProcess> tagProcessMap) throws XMLStreamException, Exception;
    /**获取返回值。*/
    public Object getResult();
    /**设置参数配置对象。*/
    public void setConfig(Object[] params);
}