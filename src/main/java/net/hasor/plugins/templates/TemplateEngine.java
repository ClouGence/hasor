/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.plugins.templates;
import java.io.IOException;
import java.io.Writer;
import net.hasor.core.AppContext;
import net.hasor.plugins.resource.ResourceLoader;
/**
 * 
 * @version : 2016年1月3日
 * @author 赵永春(zyc@hasor.net)
 */
public interface TemplateEngine {
    /**初始化引擎*/
    public void initEngine(AppContext appContext) throws IOException;
    /**执行模版引擎*/
    public void process(String layoutFile, Writer writer, ContextMap dataModel, String characterEncoding) throws Throwable;
    /**获取模版Loader*/
    public ResourceLoader getRootLoader();
}