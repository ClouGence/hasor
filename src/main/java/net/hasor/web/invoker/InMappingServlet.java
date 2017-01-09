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
package net.hasor.web.invoker;
import net.hasor.core.BindInfo;
import net.hasor.web.Invoker;

import javax.servlet.http.HttpServlet;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
/**
 * 线程安全
 * @version : 2013-6-5
 * @author 赵永春 (zyc@hasor.net)
 */
public class InMappingServlet extends InMappingDef {
    private Map<String, String> initParams;
    public InMappingServlet(long index, BindInfo<? extends HttpServlet> targetType, String mappingTo, List<Method> methodList, boolean force, Map<String, String> initParams) {
        super(index, targetType, mappingTo, methodList, force);
        this.initParams = initParams;
    }
    //
    @Override
    public Object newInstance(Invoker invoker) {
        return super.newInstance(invoker);
    }
}