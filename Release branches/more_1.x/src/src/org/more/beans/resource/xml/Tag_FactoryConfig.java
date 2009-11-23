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
import javax.xml.stream.XMLStreamReader;
import org.more.NoDefinitionException;
import org.more.beans.info.BeanDefinition;
import org.more.beans.info.CreateTypeEnum;
import org.more.util.StringConvert;
/**
 * 负责处理factoryConfig标签。<br/>
 * refBean="factoryRefBean" isStaticMethod="true" methodName="create"
 * Date : 2009-11-21
 * @author 赵永春
 */
class Tag_FactoryConfig extends DoTagEvent {
    @Override
    public void doStartEvent(String xPath, XMLStreamReader xmlReader, ContextStack context) {
        //refBean="factoryRefBean" isStaticMethod="true" methodName="create"
        BeanDefinition bean = (BeanDefinition) context.getParent().context;
        bean.setCreateType(CreateTypeEnum.Factory);
        int attCount = xmlReader.getAttributeCount();
        for (int i = 0; i < attCount; i++) {
            String key = xmlReader.getAttributeLocalName(i);
            String var = xmlReader.getAttributeValue(i);
            if (key.equals("refBean") == true)
                bean.setFactoryRefBean(var);
            else if (key.equals("isStaticMethod") == true)
                bean.setFactoryIsStaticMethod(StringConvert.parseBoolean(var, true));
            else if (key.equals("methodName") == true)
                bean.setFactoryMethodName(var);
            else
                throw new NoDefinitionException("factoryConfig标签出现未定义属性[" + key + "]");
        }
    }
}