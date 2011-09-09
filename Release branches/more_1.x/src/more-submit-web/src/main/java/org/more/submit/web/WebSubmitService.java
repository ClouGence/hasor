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
package org.more.submit.web;
import java.net.URI;
import java.util.Map;
import org.more.hypha.ApplicationContext;
import org.more.submit.ActionStack;
import org.more.submit.impl.DefaultSubmitService;
import org.more.submit.web.scope.CookieScope;
import org.more.submit.web.scope.HttpSessionScope;
import org.more.submit.web.scope.JspPageScope;
import org.more.submit.web.scope.RequestScope;
import org.more.submit.web.scope.ServletContextScope;
import org.more.util.attribute.IAttribute;
/**
 * 
 * @version : 2011-7-21
 * @author 赵永春 (zyc@byshell.org)
 */
public class WebSubmitService extends DefaultSubmitService {
    private static final long serialVersionUID = 5453676869981171561L;
    /**当服务启动时候注册一些作用域*/
    public void start(ApplicationContext context, IAttribute<Object> flash) {
        //注册顺序决定查找顺序。
        this.regeditScope(ServletContextScope.Name, new ServletContextScope());
        this.regeditScope(HttpSessionScope.Name, new HttpSessionScope());
        this.regeditScope(CookieScope.Name, new CookieScope());
        this.regeditScope(RequestScope.Name, new RequestScope());
        this.regeditScope(JspPageScope.Name, new JspPageScope());
    };
    protected WebActionStack createStack(URI uri, ActionStack onStack, Map<String, ?> params) {
        return new WebActionStack(uri, onStack, this);
    };
};