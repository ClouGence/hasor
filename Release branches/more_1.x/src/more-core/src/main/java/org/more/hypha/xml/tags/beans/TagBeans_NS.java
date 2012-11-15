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
package org.more.hypha.xml.tags.beans;
import org.more.hypha.xml.XmlDefineResource;
import org.more.hypha.xml.tags.Tag_Abstract;
import org.more.hypha.xml.tags.beans.qpp.QPP;
/**
 * 用于解析beans命名空间标签解析器基类，主要用于区分不同命名空间。
 * @version 2010-9-16
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class TagBeans_NS extends Tag_Abstract {
    private QPP parserRoot = null;
    public TagBeans_NS(XmlDefineResource configuration) {
        super(configuration);
    };
    protected QPP getRootTypeParser() {
        if (this.parserRoot == null)
            this.parserRoot = (QPP) this.getDefineResource().getFlash().getAttribute("org.more.hypha.beans.xml.QPP_ROOT");
        return this.parserRoot;
    }
};