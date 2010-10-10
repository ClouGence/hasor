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
package org.more.hypha.beans.support;
import org.more.hypha.beans.AbstractBeanDefine;
import org.more.hypha.beans.AbstractPropertyDefine;
import org.more.hypha.beans.define.QuickProperty_ValueMetaData;
import org.more.hypha.configuration.XmlConfiguration;
/**
 * {@link QuickPropertyParser}接口事件对象。该事件负责将传递一些参数。
 * @version 2010-9-22
 * @author 赵永春 (zyc@byshell.org)
 */
public class QuickParserEvent {
    private XmlConfiguration            configuration = null; //
    private AbstractBeanDefine          define        = null; //
    private AbstractPropertyDefine      property      = null; //
    private QuickProperty_ValueMetaData oldMetaData   = null; //等待解析的ValueMetaData
    public QuickParserEvent(XmlConfiguration configuration, AbstractBeanDefine define, AbstractPropertyDefine property, QuickProperty_ValueMetaData oldMetaData) {
        this.configuration = configuration;
        this.define = define;
        this.property = property;
        this.oldMetaData = oldMetaData;
    }
    /**获取当前解析到的{@link XmlConfiguration}对象。*/
    public XmlConfiguration getConfiguration() {
        return this.configuration;
    }
    /**获取当前解析到的{@link AbstractBeanDefine}对象。*/
    public AbstractBeanDefine getDefine() {
        return this.define;
    }
    /**获取当前解析到的{@link AbstractPropertyDefine}对象。*/
    public AbstractPropertyDefine getProperty() {
        return this.property;
    }
    /**获取等待解析的ValueMetaData信息。*/
    public QuickProperty_ValueMetaData getOldMetaData() {
        return this.oldMetaData;
    }
}