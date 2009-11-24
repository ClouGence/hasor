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
import java.util.HashMap;
import java.util.Map;
import javax.xml.stream.XMLStreamReader;
import org.more.beans.info.BeanDefinition;
import org.more.beans.resource.xml.ContextStack;
import org.more.beans.resource.xml.TagProcess;
import org.more.beans.resource.xml.TaskProcess;
import org.more.util.attribute.AttBase;
/**
 * （init）执行初始化扫描XML，本任务是装载静态缓存以及阅读beans配置属性。<br/>
 * (int)staticCatch默认值10，表示静态缓存大小。
 * (HashMap<String, BeanDefinition>)beanMap，表示静态缓存。
 * (int)dynamicCache默认值50，表示动态缓存大小。
 * <br/>Date : 2009-11-24
 * @author 赵永春
 */
@SuppressWarnings("unchecked")
public class Task_Init implements TaskProcess {
    private AttBase                         result             = new AttBase();
    private int                             currentStaticCatch = 0;
    private Integer                         maxStaticCatch     = 10;
    private HashMap<String, BeanDefinition> beanMap            = new HashMap<String, BeanDefinition>();
    @Override
    public void setConfig(Map params) {}
    @Override
    public Object getResult() {
        result.setAttribute("beanList", beanMap);
        return result;
    }
    @Override
    public void onEvent(ContextStack elementStack, String onXPath, int eventType, XMLStreamReader reader, Map<String, TagProcess> tagProcessMap) {
        String tagName = elementStack.getTagName();
        TagProcess process = tagProcessMap.get(tagName);
        /*------------*/
        switch (eventType) {
        case START_ELEMENT:
            process.doStartEvent(onXPath, reader, elementStack);
            if (tagName.equals("beans") == true) {
                maxStaticCatch = (Integer) elementStack.get("staticCatch");
                result.setAttribute("staticCatch", maxStaticCatch);
                result.setAttribute("dynamicCache", elementStack.get("dynamicCache"));
            }
            break;
        case END_ELEMENT:
            process.doEndEvent(onXPath, reader, elementStack);
            if (tagName.equals("bean") == true && maxStaticCatch > currentStaticCatch) {
                BeanDefinition bean = (BeanDefinition) elementStack.context;
                if (bean.isLazyInit() == false && bean.isSingleton() == true) {
                    beanMap.put(bean.getName(), bean);
                    currentStaticCatch++;
                }
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