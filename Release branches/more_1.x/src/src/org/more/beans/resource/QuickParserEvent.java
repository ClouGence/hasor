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
package org.more.beans.resource;
import org.more.beans.AbstractBeanDefine;
import org.more.beans.AbstractPropertyDefine;
import org.more.beans.define.QuickProperty_ValueMetaData;
/**
 * {@link QuickPropertyParser}接口事件对象。该事件负责将传递一些参数。
 * @version 2010-9-22
 * @author 赵永春 (zyc@byshell.org)
 */
public class QuickParserEvent {
    private XmlConfiguration            configuration = null; //
    private AbstractBeanDefine          define        = null; //
    private AbstractPropertyDefine      property      = null; //
    private QuickProperty_ValueMetaData oldMetaData   = null; //
    public QuickParserEvent(XmlConfiguration configuration, AbstractBeanDefine define, AbstractPropertyDefine property, QuickProperty_ValueMetaData oldMetaData) {
        this.configuration = configuration;
        this.define = define;
        this.property = property;
        this.oldMetaData = oldMetaData;
    }
    public XmlConfiguration getConfiguration() {
        return configuration;
    }
    public AbstractBeanDefine getDefine() {
        return define;
    }
    public AbstractPropertyDefine getProperty() {
        return property;
    }
    public QuickProperty_ValueMetaData getOldMetaData() {
        return oldMetaData;
    }
}