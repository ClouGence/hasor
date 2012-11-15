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
package org.more.hypha.xml.tags;
import org.more.hypha.xml.XmlDefineResource;
/**
 * namespace包中凡是涉及解析xml的类都需要集成的类，该类目的是为了提供一个统一的{@link XmlDefineResource}对象获取接口和一些Tag解析时的公共方法。
 * @version 2010-9-23
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class Tag_Abstract {
    private XmlDefineResource configuration = null;
    /*------------------------------------------------------------------------------*/
    /**创建Tag_Abstract类型*/
    public Tag_Abstract(XmlDefineResource configuration) {
        this.configuration = configuration;
    }
    /**获取{@link XmlDefineResource}类型*/
    protected XmlDefineResource getDefineResource() {
        return this.configuration;
    }
}