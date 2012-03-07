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
import org.more.core.log.Log;
import org.more.core.log.LogFactory;
import org.more.core.xml.XmlParserKit;
/**
 * 
 * @version : 2011-12-5
 * @author 赵永春 (zyc@byshell.org)
 */
class XmlRegisterHookUtil {
    private static Log                  log         = LogFactory.getLog(XmlRegisterHookUtil.class);
    public static final XmlRegisterHook DefaultHook = new DefaultHook() {};
    //
    //
    //
    /**根据参数指定的类型创建一个{@link xmlRegisterHook}对象*/
    public final static XmlRegisterHook getHook(String xmlRegisterHookClass) {
        try {
            Class<?> type = Thread.currentThread().getContextClassLoader().loadClass(xmlRegisterHookClass);
            return (XmlRegisterHook) type.newInstance();
        } catch (Exception e) {
            /**错误*/
            log.error("create xmlRegisterHook Type error =%0.", e);
            e.printStackTrace();
            return DefaultHook;
        }
    };
};
class DefaultHook implements XmlRegisterHook {
    public XmlParserKit createXmlParserKit(String namespace, XmlRegister manager) {
        return new XmlParserKit();
    };
}