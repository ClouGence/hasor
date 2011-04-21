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
package org.more.hypha.anno;
import org.more.hypha.DefineResource;
import org.more.hypha.context.xml.XmlDefineResource;
/**
 * 注解注册解析器。该接口的功能是负责接收并处理由{@link AnnoServices}接口注册的注解监视。
 * 可以将这个监视器注册到{@link AnnoServices}插件中。
 * @version 2010-10-26
 * @author 赵永春 (zyc@byshell.org) 
 */
public interface KeepWatchParser {
    /**
     * 处理注解监视器。
     * @param beanType 被监视到的Bean。
     * @param resource {@link DefineResource}对象。
     * @param plugin {@link AnnoServices}对象。
     */
    public void process(Class<?> beanType, XmlDefineResource resource, AnnoServices plugin);
};