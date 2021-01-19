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
import net.hasor.web.startup.RuntimeFilter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * RuntimeFilter to HandlerInterceptorAdapter
 * @version : 2020-04-22
 * @author 赵永春 (zyc@hasor.net)
 */
public class RuntimeFilter2Interceptor extends HandlerInterceptorAdapter {
    private final RuntimeFilter runtimeFilter;

    public RuntimeFilter2Interceptor(RuntimeFilter runtimeFilter) {
        this.runtimeFilter = runtimeFilter;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        this.runtimeFilter.doFilter(request, response, (req, res) -> {
            atomicBoolean.set(true);
        });
        return atomicBoolean.get();
    }
}