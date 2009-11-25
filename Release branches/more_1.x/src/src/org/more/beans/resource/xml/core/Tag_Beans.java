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

import org.more.beans.resource.xml.ContextStack;
import org.more.beans.resource.xml.TagProcess;
import org.more.util.StringConvert;
/**
 * 该类负责处理beans标签
 * Date : 2009-11-21
 * @author 赵永春
 */
public class Tag_Beans extends TagProcess {
    @Override
    public void doStartEvent(String xPath, XMLStreamReader xmlReader, ContextStack context) {
        String staticCacheSize = xmlReader.getAttributeValue(null, "staticCache");
        String dynamicCacheSize = xmlReader.getAttributeValue(null, "dynamicCache");
        context.setAttribute("staticCache", StringConvert.parseInt(staticCacheSize, 10));
        context.setAttribute("dynamicCache", StringConvert.parseInt(dynamicCacheSize, 50));
    }
}