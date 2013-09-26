/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.servlet.binder.support;
import java.util.ArrayList;
import java.util.List;
import net.hasor.core.Environment;
import net.hasor.core.ModuleInfo;
import net.hasor.core.binder.ApiBinderModule;
import net.hasor.servlet.WebApiBinder;
import com.google.inject.Binder;
/**
 * 该类是{@link WebApiBinder}接口实现。
 * @version : 2013-4-10
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class WebApiBinderModule extends ApiBinderModule implements WebApiBinder {
    private FiltersModuleBuilder              filterModuleBinder                = new FiltersModuleBuilder();             /*Filters*/
    private ServletsModuleBuilder             servletModuleBinder               = new ServletsModuleBuilder();            /*Servlets*/
    private HttpSessionListenerBindingBuilder httpSessionListenerBindingBuilder = new HttpSessionListenerBindingBuilder(); /*Listener*/
    private ContextListenerBindingBuilder     contextListenerBuilder            = new ContextListenerBindingBuilder();    /*Listener*/
    //
    protected WebApiBinderModule(Environment envContext, ModuleInfo forModule) {
        super(envContext, forModule);
    }
    /***/
    private static List<String> newArrayList(String[] arr, String object) {
        ArrayList<String> list = new ArrayList<String>();
        if (arr != null)
            for (String item : arr)
                list.add(item);
        if (object != null)
            list.add(object);
        return list;
    }
    public FilterBindingBuilder filter(String urlPattern, String... morePatterns) {
        return this.filterModuleBinder.filterPattern(newArrayList(morePatterns, urlPattern));
    };
    public FilterBindingBuilder filterRegex(String regex, String... regexes) {
        return this.filterModuleBinder.filterRegex(newArrayList(regexes, regex));
    };
    public ServletBindingBuilder serve(String urlPattern, String... morePatterns) {
        return this.servletModuleBinder.filterPattern(newArrayList(morePatterns, urlPattern));
    };
    public ServletBindingBuilder serveRegex(String regex, String... regexes) {
        return this.servletModuleBinder.filterRegex(newArrayList(regexes, regex));
    };
    public SessionListenerBindingBuilder sessionListener() {
        return this.httpSessionListenerBindingBuilder.sessionListener();
    }
    public ServletContextListenerBindingBuilder contextListener() {
        return this.contextListenerBuilder.contextListener();
    }
    public void configure(Binder binder) {
        super.configure(binder);
        binder.install(this.filterModuleBinder);
        binder.install(this.servletModuleBinder);
        binder.install(this.httpSessionListenerBindingBuilder);
        binder.install(this.contextListenerBuilder);
    }
}