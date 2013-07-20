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
package org.hasor.servlet.binder.support;
import java.util.ArrayList;
import org.hasor.context.InitContext;
import org.hasor.context.binder.ApiBinderModule;
import org.hasor.servlet.WebApiBinder;
import org.more.util.ArrayUtils;
import com.google.inject.Binder;
/**
 * 该类是{@link WebApiBinder}接口实现。
 * @version : 2013-4-10
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class WebApiBinderModule extends ApiBinderModule implements WebApiBinder {
    private FiltersModuleBuilder   filterModuleBinder     = new FiltersModuleBuilder();  /*Filters*/
    private ServletsModuleBuilder  servletModuleBinder    = new ServletsModuleBuilder(); /*Servlets*/
    private ErrorsModuleBuilder    errorsModuleBuilder    = new ErrorsModuleBuilder();   /*Errors*/
    private ListenerBindingBuilder listenerBindingBuilder = new ListenerBindingBuilder(); /*Listener*/
    //
    protected WebApiBinderModule(InitContext initContext) {
        super(initContext);
    }
    @Override
    public FilterBindingBuilder filter(String urlPattern, String... morePatterns) {
        return this.filterModuleBinder.filterPattern(ArrayUtils.newArrayList(morePatterns, urlPattern));
    };
    @Override
    public FilterBindingBuilder filterRegex(String regex, String... regexes) {
        return this.filterModuleBinder.filterRegex(ArrayUtils.newArrayList(regexes, regex));
    };
    @Override
    public ServletBindingBuilder serve(String urlPattern, String... morePatterns) {
        return this.servletModuleBinder.filterPattern(ArrayUtils.newArrayList(morePatterns, urlPattern));
    };
    @Override
    public ServletBindingBuilder serveRegex(String regex, String... regexes) {
        return this.servletModuleBinder.filterRegex(ArrayUtils.newArrayList(regexes, regex));
    };
    @Override
    public ErrorBindingBuilder error(Class<? extends Throwable> error) {
        ArrayList<Class<? extends Throwable>> errorList = new ArrayList<Class<? extends Throwable>>();
        errorList.add(error);
        return this.errorsModuleBuilder.errorTypes(errorList);
    }
    @Override
    public SessionListenerBindingBuilder sessionListener() {
        return this.listenerBindingBuilder.sessionListener();
    }
    @Override
    public void configure(Binder binder) {
        super.configure(binder);
        binder.install(this.filterModuleBinder);
        binder.install(this.servletModuleBinder);
        binder.install(this.errorsModuleBuilder);
        binder.install(this.listenerBindingBuilder);
    }
}