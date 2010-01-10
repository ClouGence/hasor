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
import java.util.Map;
import javax.xml.stream.XMLStreamReader;
import org.more.beans.info.BeanDefinition;
import org.more.beans.resource.xml.XmlContextStack;
import org.more.beans.resource.xml.TagProcess;
import org.more.beans.resource.xml.TaskProcess;
/**
 * 查找某个bean，如果找不到则返回null。
 * 测试是否存在某个bean的任务。<br/>
 * 任务名：findBean<br/>
 * 任务参数：1.String要查找的bean名<br/>
 * 返回值：属性值
 * @version 2009-11-24
 * @author 赵永春 (zyc@byshell.org)
 */
public class Task_FindBeanDefinition implements TaskProcess {
    private String findName = null;
    private Object result   = null;
    //
    @Override
    public void setConfig(Object[] params) {
        this.findName = (String) params[0];
    }
    @Override
    public Object getResult() {
        return result;
    }
    @Override
    public void onEvent(XmlContextStack elementStack, String onXPath, int eventType, XMLStreamReader reader, Map<String, TagProcess> tagProcessMap) {
        String tagName = elementStack.getTagName();
        TagProcess process = tagProcessMap.get(tagName);
        /*------------*/
        switch (eventType) {
        case START_ELEMENT:
            process.doStartEvent(onXPath, reader, elementStack);
            break;
        case END_ELEMENT:
            process.doEndEvent(onXPath, reader, elementStack);
            if (tagName.equals("bean") == true) {
                BeanDefinition bean = (BeanDefinition) elementStack.context;
                if (bean.getName().equals(this.findName) == true)
                    this.result = elementStack.context;
            }
            break;
        case CDATA:
            process.doCharEvent(onXPath, reader, elementStack);
            break;
        case CHARACTERS:
            process.doCharEvent(onXPath, reader, elementStack);
            break;
        }
    }
}