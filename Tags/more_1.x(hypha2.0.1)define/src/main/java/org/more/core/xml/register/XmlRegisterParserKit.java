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
package org.more.core.xml.register;
import org.more.core.xml.XmlParserKit;
/**
 * 继承了{@link XmlParserKit}并且增加了对创建它的{@link XmlRegisterHook}对象获取方法。
 * @version : 2012-8-3
 * @author 赵永春 (zyc@byshell.org)
 */
public class XmlRegisterParserKit extends XmlParserKit {
    private XmlRegisterHook xmlRegisterHook = null;
    public XmlRegisterHook getXmlRegisterHook() {
        return xmlRegisterHook;
    }
    void setXmlRegisterHook(XmlRegisterHook xmlRegisterHook) {
        this.xmlRegisterHook = xmlRegisterHook;
    }
}