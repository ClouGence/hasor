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
package org.more.webui.freemarker.loader.template.mto;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
/**
 * 模板资源读取类，该类是一个抽象类。其实现类决定如何读取不同形式的资源。
 * @version : 2011-9-16
 * @author 赵永春 (zyc@byshell.org)
 */
public interface AbstractTemplateObject {
    public final static String DefaultEncoding = "utf-8";
    /**获取该对象最后修改时间。*/
    public long lastModified();
    /**获取对象的{@link Reader}，在获取之前可能需要调用{@link #openObject()}方法打开该对象。*/
    public Reader getReader(String encoding) throws IOException;
    /**获取对象的{@link InputStream}。*/
    public InputStream getInputStream() throws IOException;
    /**打开资源*/
    public void openObject();
    /**关闭资源*/
    public void closeObject();
};