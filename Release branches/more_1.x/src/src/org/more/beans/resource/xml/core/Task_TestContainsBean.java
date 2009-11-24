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
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.more.beans.resource.xml.ContextStack;
import org.more.beans.resource.xml.TagProcess;
import org.more.beans.resource.xml.TaskProcess;
/**
 * （containsBean）测试是否存在某个bean，该任务需要字符串参数beanName用于表示要测试的bean名。
 * <br/>Date : 2009-11-24
 * @author 赵永春
 */
@SuppressWarnings("unchecked")
public class Task_TestContainsBean implements TaskProcess {
    private String  beanName = null;
    private boolean result   = false;
    @Override
    public void setConfig(Map params) {
        this.beanName = (String) params.get("beanName");
    }
    @Override
    public Object getResult() {
        return result;
    }
    @Override
    public void onEvent(ContextStack elementStack, String onXPath, int eventType, XMLStreamReader reader, Map<String, TagProcess> tagProcessMap) throws XMLStreamException {
        if (eventType != START_ELEMENT)
            return;
        if (elementStack.getTagName().equals("bean") == false)
            return;
        String name = reader.getAttributeValue(null, "name");
        if (name.equals(this.beanName) == true)
            this.result = true;
    }
}