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
package org.more.hypha.xml.tags.el;
import java.util.ArrayList;
import java.util.List;
import org.more.core.xml.XmlElementHook;
import org.more.core.xml.XmlStackDecorator;
import org.more.core.xml.stream.EndElementEvent;
import org.more.core.xml.stream.StartElementEvent;
import org.more.hypha.xml.XmlDefineResource;
/**
 * 用于解析el:add标签
 * @version 2011-6-23
 * @author 赵永春 (zyc@byshell.org)
 */
public class EL_Add extends EL_NS implements XmlElementHook {
    public EL_Add(XmlDefineResource configuration) {
        super(configuration);
    }
    public void beginElement(XmlStackDecorator<Object> context, String xpath, StartElementEvent event) {
        XmlDefineResource resource = this.getDefineResource();
        List<B_EL> elList = (List<B_EL>) resource.getFlash().getAttribute(ELConfigList);
        if (elList == null) {
            elList = new ArrayList<B_EL>();
            resource.getFlash().setAttribute(ELConfigList, elList);
        }
        B_EL el = new B_EL();
        el.setName(event.getAttributeValue("name"));
        el.setClassName(event.getAttributeValue("class"));
        elList.add(el);
    }
    public void endElement(XmlStackDecorator<Object> context, String xpath, EndElementEvent event) {}
}