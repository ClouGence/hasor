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
import net.hasor.core.spi.BeanCreaterListener;
import net.hasor.core.BindInfo;
import net.hasor.core.provider.InstanceProvider;
import net.hasor.web.definition.J2eeMapConfig;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Predicate;
/**
 * 线程安全
 * @version : 2013-6-5
 * @author 赵永春 (zyc@hasor.net)
 */
public class InMappingServlet extends InMappingDef implements BeanCreaterListener<HttpServlet> {
    private static Predicate<Method>   methodMatcher = target -> {
        Class<?>[] parameterTypes = target.getParameterTypes();
        return "service".equals(target.getName()) &&        //
                parameterTypes.length == 2 &&               //
                parameterTypes[0] == ServletRequest.class &&//
                parameterTypes[1] == ServletResponse.class;
    };
    //
    private        Map<String, String> initParams;
    private        ServletContext      servletContext;
    public InMappingServlet(int index, BindInfo<? extends HttpServlet> targetType, String mappingTo, Map<String, String> initParams, ServletContext servletContext) {
        super(index, targetType, mappingTo, methodMatcher, false);
        this.initParams = initParams;
        this.servletContext = servletContext;
    }
    //
    public Map<String, String> getInitParams() {
        return this.initParams;
    }
    //
    @Override
    public void beanCreated(HttpServlet newObject, BindInfo<? extends HttpServlet> bindInfo) throws Throwable {
        newObject.init(new J2eeMapConfig(getTargetType().toString(), this.initParams, InstanceProvider.wrap(this.servletContext)));
    }
}