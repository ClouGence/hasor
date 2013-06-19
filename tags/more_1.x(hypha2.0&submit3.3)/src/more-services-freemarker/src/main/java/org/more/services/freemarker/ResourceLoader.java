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
package org.more.services.freemarker;
import java.io.IOException;
import java.io.InputStream;
import freemarker.cache.TemplateLoader;
/**
 * 该接口可以装载模板路径中的资源，需要作为{@link TemplateLoader}实现类去实现。
 * @version : 2011-9-14
 * @author 赵永春 (zyc@byshell.org) 
 */
public interface ResourceLoader {
    /**获取包括非模板在内的资源文件输入流。*/
    public InputStream getResourceAsStream(String resourcePath) throws IOException;
}