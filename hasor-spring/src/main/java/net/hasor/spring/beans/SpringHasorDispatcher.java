/*
 * Copyright 2002-2005 the original author or authors.
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
package net.hasor.spring.beans;
import net.hasor.core.AppContext;
import net.hasor.utils.StringUtils;
import net.hasor.web.binder.OneConfig;
import net.hasor.web.startup.RuntimeFilter;
import net.hasor.web.startup.RuntimeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * RuntimeFilter to Controller
 * @version : 2021-01-20
 * @author 赵永春 (zyc@hasor.net)
 */
public class SpringHasorDispatcher implements InitializingBean, ApplicationContextAware {
    protected static Logger             logger      = LoggerFactory.getLogger(SpringHasorDispatcher.class);
    private          ApplicationContext applicationContext;
    private          List<String>       mappingPath = new ArrayList<>();

    public List<String> getMappingPath() {
        return this.mappingPath;
    }

    public void setMappingPath(List<String> mappingPath) {
        this.mappingPath = mappingPath;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String[] mappingPaths = evalFilterPath(this.mappingPath.toArray(new String[0]));
        logger.info("spring hasor web-dispatcher mappingPath -> " + StringUtils.join(mappingPaths, ","));
        //
        ServletContext contextBean = this.applicationContext.getBean(ServletContext.class);
        AppContext appContext = RuntimeListener.getAppContext(contextBean);
        RuntimeFilter runtimeFilter = new RuntimeFilter(appContext);
        runtimeFilter.init(new OneConfig("", () -> appContext));
        //
        RequestMappingHandlerMapping requestMappingHandlerMapping = this.applicationContext.getBean(RequestMappingHandlerMapping.class);
        RuntimeFilter2Controller handler = new RuntimeFilter2Controller(runtimeFilter, appContext);
        Method handlerMethod = RuntimeFilter2Controller.class.getMethod("doHandler", HttpServletRequest.class, HttpServletResponse.class);
        RequestMappingInfo info = RequestMappingInfo.paths(mappingPaths).methods(RequestMethod.values()).build();
        requestMappingHandlerMapping.registerMapping(info, handler, handlerMethod);
    }

    private static String[] evalFilterPath(String[] filterPath) {
        String[] filterPathArray = new String[filterPath.length];
        for (int i = 0; i < filterPath.length; i++) {
            String tmp = filterPath[i];
            if (tmp.endsWith("/*")) {
                tmp = tmp.substring(0, tmp.length() - 2) + "/**";
            }
            filterPathArray[i] = tmp;
        }
        return filterPathArray;
    }
}