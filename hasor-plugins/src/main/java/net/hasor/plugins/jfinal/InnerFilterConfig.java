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
package net.hasor.plugins.jfinal;
import com.jfinal.core.JFinal;
import net.hasor.utils.Iterators;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import java.util.Enumeration;
/**
 * @version : 2016-11-03
 * @author 赵永春 (zyc@byshell.org)
 */
class InnerFilterConfig implements FilterConfig {
    private final JFinal   jFinal;
    private final InnerMap innerMap;
    public InnerFilterConfig(JFinal jFinal, InnerMap innerMap) {
        this.jFinal = jFinal;
        this.innerMap = innerMap;
    }
    @Override
    public String getFilterName() {
        return this.getClass().getName();
    }
    @Override
    public ServletContext getServletContext() {
        return this.jFinal.getServletContext();
    }
    @Override
    public String getInitParameter(String name) {
        return this.innerMap.get(name);
    }
    @Override
    public Enumeration getInitParameterNames() {
        return Iterators.asEnumeration(this.innerMap.keySet().iterator());
    }
}
