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
package org.platform.context.setting;
import java.util.Map;
import org.more.global.Global;
import org.more.global.assembler.xml.XmlProperty;
/**
 * 
 * @version : 2013-4-23
 * @author 赵永春 (zyc@byshell.org)
 */
public class Settings extends Global {
    protected Settings(Map<String, Object> configs) {
        super(configs);
    };
    /**解析全局配置参数，并且返回其{@link XmlProperty}形式对象。*/
    public XmlProperty getXmlProperty(Enum<?> name) {
        return this.getToType(name, XmlProperty.class, null);
    };
    /**解析全局配置参数，并且返回其{@link XmlProperty}形式对象。*/
    public XmlProperty getXmlProperty(String name) {
        return this.getToType(name, XmlProperty.class, null);
    }
}