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
import org.more.beans.resource.xml.ContextStack;
import org.more.beans.resource.xml.TagProcess;
import org.more.beans.resource.xml.TaskProcess;
/**
 * 该任务是负责查找指定bean中的属性，并且返回属性值。<br/>
 * 测试是否存在某个bean的任务。<br/>
 * 任务名：getAttribute<br/>
 * 任务参数：1.String要查找的bean名，2.String要查找的属性名<br/>
 * 返回值：属性值<br/>
 * <br/>Date : 2009-11-24
 * @author 赵永春
 */
public class Task_GetBeanAttribute implements TaskProcess {
    private String beanName, attName, result = null;
    @Override
    public void setConfig(Object[] params) {
        this.beanName = (String) params[0];
        this.attName = (String) params[1];
    }
    @Override
    public Object getResult() {
        return result;
    }
    @Override
    public void onEvent(ContextStack elementStack, String onXPath, int eventType, XMLStreamReader reader, Map<String, TagProcess> tagProcessMap) {
        if (eventType != ATTRIBUTE)
            return;
        if (elementStack.getTagName().equals("bean") == false)
            return;
        String name = reader.getAttributeValue(null, "name");
        if (name.equals(this.beanName) == false)
            return;
        this.result = reader.getAttributeValue(null, this.attName);
    }
}