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
package org.platform.api.binder;
import java.util.HashMap;
import java.util.Map;
import org.more.util.ArrayUtil;
import org.platform.Assert;
import org.platform.api.context.InitContext;
import com.google.inject.AbstractModule;
import com.google.inject.Binder;
/**
 * 该类是代理了{@link Binder}并且提供了注册Servlet和Filter的方法。
 * @version : 2013-4-10
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractApiBinder extends AbstractModule implements ApiBinder {
    private InitContext                         initContext         = null;
    private Map<String, Object>                 extData             = null;
    private FiltersModuleBuilder  filterModuleBinder  = null;
    private ServletsModuleBuilder servletModuleBinder = null;
    //
    /**构建InitEvent对象。*/
    protected AbstractApiBinder(InitContext initContext) {
        Assert.isNotNull(initContext, "param initContext is null.");
        this.initContext = initContext;
    }
    @Override
    public InitContext getInitContext() {
        return initContext;
    }
    /**获取用于携带参数的数据。*/
    public Map<String, Object> getExtData() {
        if (this.extData == null)
            this.extData = new HashMap<String, Object>();
        return this.extData;
    }
    @Override
    public FilterBindingBuilder filter(String urlPattern, String... morePatterns) {
        return this.filterModuleBinder.filterPattern(ArrayUtil.newArrayList(morePatterns, urlPattern));
    };
    @Override
    public FilterBindingBuilder filterRegex(String regex, String... regexes) {
        return this.filterModuleBinder.filterRegex(ArrayUtil.newArrayList(regexes, regex));
    };
    @Override
    public ServletBindingBuilder serve(String urlPattern, String... morePatterns) {
        return this.servletModuleBinder.filterPattern(ArrayUtil.newArrayList(morePatterns, urlPattern));
    };
    @Override
    public ServletBindingBuilder serveRegex(String regex, String... regexes) {
        return this.servletModuleBinder.filterRegex(ArrayUtil.newArrayList(regexes, regex));
    };
    @Override
    protected void configure() {
        this.install(this.filterModuleBinder);
        this.install(this.servletModuleBinder);
    }
}