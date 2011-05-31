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
package org.more.hypha.context.xml;
import org.more.core.error.LoadException;
/**
 * 为了{@link XmlDefineResource}类提供的一个注册器接口，如果要注册新的xml配置支持则需要实现这个接口并且
 * 留下一个无参的构造方法，同时在“/META-INF/resource/beans/regedit.xml”位置编写配置文件。
 * 配置文件格式参考more相关文档。
 * @version 2010-9-24
 * @author 赵永春 (zyc@byshell.org)
 */
public interface XmlNameSpaceRegister {
    /**
     * 执行注册
     * @param namespaceURL 配置文件配置的命名空间。
     * @param resource {@link XmlDefineResource}对象。
     */
    public void initRegister(String namespaceURL, XmlDefineResource resource) throws LoadException;
}