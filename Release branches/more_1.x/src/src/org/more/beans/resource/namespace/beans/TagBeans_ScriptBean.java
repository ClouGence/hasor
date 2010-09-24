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
package org.more.beans.resource.namespace.beans;
import java.util.Map;
import org.more.beans.define.ScriptBeanDefine;
import org.more.beans.resource.XmlConfiguration;
import org.more.core.xml.XmlStackDecorator;
import org.more.core.xml.XmlTextHook;
import org.more.core.xml.stream.EndElementEvent;
import org.more.core.xml.stream.StartElementEvent;
import org.more.core.xml.stream.TextEvent;
/**
 * 用于解析/beans/scriptBean标签
 * @version 2010-9-16
 * @author 赵永春 (zyc@byshell.org)
 */
public class TagBeans_ScriptBean extends TagBeans_AbstractBeanDefine<ScriptBeanDefine> implements XmlTextHook {
    /**保存于上下文中的脚本值对象*/
    private static final String ScriptText = "$more_ScriptText";
    /**创建{@link TagBeans_ScriptBean}对象*/
    public TagBeans_ScriptBean(XmlConfiguration configuration) {
        super(configuration);
    }
    /**创建ScriptBeanDefine类型对象。*/
    protected ScriptBeanDefine createDefine() {
        return new ScriptBeanDefine();
    }
    /**定义脚本Bean属性*/
    public enum PropertyKey {
        language, scriptText, sourcePath
    };
    /**关联属性与xml的属性对应关系。*/
    protected Map<Enum<?>, String> getPropertyMappings() {
        Map<Enum<?>, String> propertys = super.getPropertyMappings();
        propertys.put(PropertyKey.language, "language");
        propertys.put(PropertyKey.scriptText, "scriptText");
        propertys.put(PropertyKey.sourcePath, "sourcePath");
        return propertys;
    }
    /**开始解析标签，该方法用于读取implements属性。*/
    public void beginElement(XmlStackDecorator context, String xpath, StartElementEvent event) {
        super.beginElement(context, xpath, event);
        ScriptBeanDefine define = this.getDefine(context);
        String impl = event.getAttributeValue("implements");
        if (impl != null)
            define.addImplement(impl);
    }
    /**结束解析标签，该方法用于写入配置的CDATA信息。*/
    public void endElement(XmlStackDecorator context, String xpath, EndElementEvent event) {
        ScriptBeanDefine define = this.getDefine(context);
        if (define.getScriptText() == null) {
            StringBuffer scriptText = (StringBuffer) context.getAttribute(ScriptText);
            if (scriptText != null)
                define.setScriptText(scriptText.toString());
        }
        super.endElement(context, xpath, event);
    }
    /**脚本内容CDATA解析。*/
    public void text(XmlStackDecorator context, String xpath, TextEvent event) {
        if (event.isCommentEvent() == true)
            return;
        StringBuffer scriptText = (StringBuffer) context.getAttribute(ScriptText);
        if (scriptText == null) {
            scriptText = new StringBuffer();
            //TODO 不需要明确删除它，因为endElement方法会在最后直接清空当前堆栈
            context.setAttribute(ScriptText, scriptText);
        }
        String value = event.getTrimText();
        if (value != null)
            scriptText.append(value);
    };
}