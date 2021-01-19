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
import net.hasor.web.binder.OneConfig;
import net.hasor.web.startup.RuntimeFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * @version : 2021-01-19
 * @author 赵永春 (zyc@hasor.net)
 */
public class RuntimeFilter2Controller {
    private final RuntimeFilter runtimeFilter;

    public RuntimeFilter2Controller(RuntimeFilter runtimeFilter, AppContext appContext) throws ServletException {
        this.runtimeFilter = runtimeFilter;
        runtimeFilter.init(new OneConfig("", () -> appContext));
    }

    public void doHandler(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        this.runtimeFilter.doFilter(request, response, (req, res) -> {
            HttpServletResponse httpRes = (HttpServletResponse) res;
            if (!httpRes.isCommitted()) {
                httpRes.sendError(404, "Not Found Resource.");
            }
        });
    }
}