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
package org.platform.view.freemarker;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import freemarker.core.Environment;
import freemarker.template.TemplateException;
/**
 * 
 * @version : 2012-6-14
 * @author 赵永春 (zyc@byshell.org)
 */
public interface TemplateBody {
    /**标签属性*/
    public Map<String, Object> tagProperty();
    /**获取标签执行环境*/
    public Environment getEnvironment();
    /**渲染输出标签内容*/
    public void render(Writer arg0) throws TemplateException, IOException;
}