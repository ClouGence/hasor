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
package net.hasor.web.binder;
import net.hasor.core.Environment;
import net.hasor.core.HasorUtils;
import net.hasor.core.provider.SingleProvider;
import net.hasor.web.Controller;
import net.hasor.web.Invoker;
import net.hasor.web.annotation.Any;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * 线程安全
 * @version : 2013-6-5
 * @author 赵永春 (zyc@hasor.net)
 */
public class J2eeServletAsMapping implements Controller {
    private final OneConfig                   initParams;
    private       AtomicBoolean               inited;
    private       Supplier<? extends Servlet> targetServlet;

    public J2eeServletAsMapping(OneConfig initParams, Supplier<? extends Servlet> j2eeServlet) {
        this.initParams = initParams;
        this.inited = new AtomicBoolean(false);
        this.targetServlet = new SingleProvider<>(j2eeServlet);
    }

    public Supplier<? extends Servlet> getTarget() {
        return targetServlet;
    }

    public ServletConfig getInitParams() {
        return initParams;
    }

    @Override
    public void initController(Invoker invoker) throws ServletException {
        if (!this.inited.compareAndSet(false, true)) {
            return;
        }
        // 初始化
        this.targetServlet.get().init(this.initParams);
        // 注册销毁回调
        Environment environment = invoker.getAppContext().getEnvironment();
        HasorUtils.pushShutdownListener(environment, (event, eventData) -> {
            destroy();
        });
    }

    /** 执行Servlet */
    @Any
    public void doInvoke(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        if (!this.inited.get()) {
            throw new IllegalStateException("this Servlet uninitialized.");
        }
        this.targetServlet.get().service(request, response);
    }

    /** 销毁过滤器。 */
    public void destroy() {
        if (!this.inited.compareAndSet(true, false)) {
            return;
        }
        this.targetServlet.get().destroy();
    }
}