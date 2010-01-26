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
import java.util.ArrayList;
import javax.xml.stream.XMLStreamReader;
import org.more.ResourceException;
import org.more.beans.resource.xml.XmlContextStack;
import org.more.beans.resource.xml.TagProcess;
/**
 * 该类负责解析mapVar标签。
 * @version 2009-11-23
 * @author 赵永春 (zyc@byshell.org)
 */
@SuppressWarnings("unchecked")
public class Tag_MapVar extends TagProcess {
    @Override
    public void doStartEvent(String xPath, XMLStreamReader xmlReader, XmlContextStack context) {}
    @Override
    public void doEndEvent(String xPath, XMLStreamReader xmlReader, XmlContextStack context) {
        ArrayList elementList = (ArrayList) context.get("tag_element");
        if (elementList == null || elementList.size() == 0)
            throw new ResourceException("没有配置map数据的value");
        else
            context.getParent().put("map_var", elementList.get(0));
    }
}