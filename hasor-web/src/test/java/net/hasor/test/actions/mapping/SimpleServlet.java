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
package net.hasor.test.actions.mapping;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import java.io.IOException;

/**
 * @version : 2017-01-08
 * @author 赵永春 (zyc@hasor.net)
 */
public class SimpleServlet extends HttpServlet {
    private boolean       init;
    private boolean       destroy;
    private boolean       doCall;
    private ServletConfig config;

    public boolean isInit() {
        return init;
    }

    public boolean isDestroy() {
        return destroy;
    }

    public boolean isDoCall() {
        return doCall;
    }

    public ServletConfig getConfig() {
        return config;
    }

    public void init(ServletConfig config) {
        this.init = true;
        this.config = config;
    }

    @Override
    public void destroy() {
        this.destroy = true;
    }

    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        this.doCall = true;
    }
}