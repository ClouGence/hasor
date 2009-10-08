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
package org.more.submit.support;
import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;
import org.more.submit.Config;
/**
 * Map对象到Config接口的转换类
 * Date : 2009-6-30
 * @author 赵永春
 */
@SuppressWarnings("unchecked")
public class MapSubmitConfig implements Config {
    private Object context = null;
    private Map    params  = null;
    public MapSubmitConfig(Map params, Object context) {
        this.context = context;
        this.params = params;
    }
    @Override
    public Object getContext() {
        return this.context;
    }
    @Override
    public String getInitParameter(String name) {
        Object v = params.get(name);
        if (v == null)
            return null;
        else
            return v.toString();
    }
    @Override
    public Enumeration getInitParameterNames() {
        return new Vector(params.keySet()).elements();
    }
}
