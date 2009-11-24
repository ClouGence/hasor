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
 * （getAttribute）该任务是负责查找指定bean中的属性，并且返回属性值。<br/>
 * 参数beanName用于确定要查找的类名，参数attName用于确定要查找的属性名。
 * <br/>Date : 2009-11-24
 * @author 赵永春
 */
@SuppressWarnings("unchecked")
public class Task_GetBeanAttribute implements TaskProcess {
    private String beanName, attName, result = null;
    @Override
    public void setConfig(Map params) {
        this.beanName = (String) params.get("beanName");
        this.attName = (String) params.get("attName");
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