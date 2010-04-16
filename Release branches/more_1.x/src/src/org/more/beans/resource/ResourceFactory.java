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
import java.io.File;
import java.net.URI;
import org.more.beans.BeanResource;
/**
 * 该类用于隐藏创建BeanResource细节的工厂类。
 * @version 2010-2-26
 * @author 赵永春 (zyc@byshell.org)
 */
public class ResourceFactory {
    /**创建一个建议的BeanResource接口对象。*/
    public static BeanResource create(File configFile) throws Exception {
        return new AnnoXmlFileResource(configFile);
    }
    /**创建一个建议的BeanResource接口对象。*/
    public static BeanResource create(URI configURI) throws Exception {
        return new AnnoXmlFileResource(configURI);
    }
    /**创建一个建议的BeanResource接口对象。*/
    public static BeanResource create() throws Exception {
        return new AnnoXmlFileResource();
    }
}