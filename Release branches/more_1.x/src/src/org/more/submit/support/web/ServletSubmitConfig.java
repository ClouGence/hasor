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
package org.more.submit.support.web;
import java.util.Enumeration;
import javax.servlet.ServletConfig;
import org.more.submit.Config;
/**
 * ServletConfig对象到Config接口的转换类
 * Date : 2009-6-30
 * @author 赵永春
 */
@SuppressWarnings("unchecked")
class ServletSubmitConfig implements Config {
    //========================================================================================Field
    private ServletConfig config = null;
    //==================================================================================Constructor
    public ServletSubmitConfig(ServletConfig config) {
        this.config = config;
    }
    //==========================================================================================Job
    @Override
    public Object getContext() {
        return this.config.getServletContext();
    }
    @Override
    public String getInitParameter(String name) {
        return this.config.getInitParameter(name);
    }
    @Override
    public Enumeration getInitParameterNames() {
        return this.config.getInitParameterNames();
    }
}