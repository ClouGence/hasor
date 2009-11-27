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
import javax.xml.stream.XMLStreamReader;
import org.more.DoesSupportException;
import org.more.beans.resource.xml.ContextStack;
import org.more.beans.resource.xml.TagProcess;
import org.more.util.attribute.IAttribute;
/**
 * 该类负责处理meta标签。
 * <br/>Date : 2009-11-23
 * @author 赵永春
 */
public class Tag_Meta extends TagProcess {
    @Override
    public void doStartEvent(String xPath, XMLStreamReader xmlReader, ContextStack context) {
        ContextStack parent = context.getParent();
        if (parent.context instanceof IAttribute == true) {
            IAttribute att = (IAttribute) parent.context;
            String key = xmlReader.getAttributeValue(null, "key");
            String value = xmlReader.getAttributeValue(null, "value");
            att.setAttribute(key, value);
        } else
            throw new DoesSupportException("标签" + parent.getTagName() + " 不具备处理meta信息的能力！");
    }
}