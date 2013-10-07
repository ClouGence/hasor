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
package net.hasor.web.servlet.binder.support;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.Filter;
import net.hasor.web.servlet.WebApiBinder.FilterBindingBuilder;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.internal.UniqueAnnotations;
/**
 * 用于处理FilterBindingBuilder接口对象的创建
 * @version : 2013-4-12
 * @author 赵永春 (zyc@hasor.net)
 */
class FiltersModuleBuilder implements Module {
    /*Filter 定义*/
    private final List<FilterDefinition> filterDefinitions = new ArrayList<FilterDefinition>();
    //
    public FilterBindingBuilder filterPattern(List<String> servletPattern) {
        return new FilterBindingBuilderImpl(UriPatternType.SERVLET, servletPattern);
    }
    public FilterBindingBuilder filterRegex(List<String> regexPattern) {
        return new FilterBindingBuilderImpl(UriPatternType.REGEX, regexPattern);
    }
    public void configure(Binder binder) {
        /*将FilterDefinition绑定到Guice身上，在正式使用时利用findBindingsByType方法将其找回来。*/
        for (FilterDefinition define : filterDefinitions)
            binder.bind(FilterDefinition.class).annotatedWith(UniqueAnnotations.create()).toProvider(define);
    }
    /*-----------------------------------------------------------------------------------------*/
    /** FilterBindingBuilder接口实现 */
    class FilterBindingBuilderImpl implements FilterBindingBuilder {
        private final UriPatternType uriPatternType;
        private final List<String>   uriPatterns;
        //
        public FilterBindingBuilderImpl(UriPatternType uriPatternType, List<String> uriPatterns) {
            this.uriPatternType = uriPatternType;
            this.uriPatterns = uriPatterns;
        }
        public void through(Class<? extends Filter> filterKey) {
            through(0, Key.get(filterKey));
        }
        public void through(Key<? extends Filter> filterKey) {
            through(0, filterKey, new HashMap<String, String>());
        }
        public void through(Filter filter) {
            through(0, filter, new HashMap<String, String>());
        }
        public void through(Class<? extends Filter> filterKey, Map<String, String> initParams) {
            // Careful you don't accidentally make this method recursive, thank you IntelliJ IDEA!
            through(0, Key.get(filterKey), initParams);
        }
        public void through(Key<? extends Filter> filterKey, Map<String, String> initParams) {
            through(0, filterKey, initParams, null);
        }
        public void through(Filter filter, Map<String, String> initParams) {
            Key<Filter> filterKey = Key.get(Filter.class, UniqueAnnotations.create());
            through(0, filterKey, initParams, filter);
        }
        public void through(int index, Class<? extends Filter> filterKey) {
            through(index, Key.get(filterKey));
        }
        public void through(int index, Key<? extends Filter> filterKey) {
            through(index, filterKey, new HashMap<String, String>());
        }
        public void through(int index, Filter filter) {
            through(index, filter, new HashMap<String, String>());
        }
        public void through(int index, Class<? extends Filter> filterKey, Map<String, String> initParams) {
            // Careful you don't accidentally make this method recursive, thank you IntelliJ IDEA!
            through(index, Key.get(filterKey), initParams);
        }
        public void through(int index, Key<? extends Filter> filterKey, Map<String, String> initParams) {
            through(index, filterKey, initParams, null);
        }
        public void through(int index, Filter filter, Map<String, String> initParams) {
            Key<Filter> filterKey = Key.get(Filter.class, UniqueAnnotations.create());
            through(index, filterKey, initParams, filter);
        }
        private void through(int index, Key<? extends Filter> filterKey, Map<String, String> initParams, Filter filterInstance) {
            for (String pattern : uriPatterns)
                filterDefinitions.add(new FilterDefinition(index, pattern, filterKey, UriPatternType.get(uriPatternType, pattern), initParams, filterInstance));
        }
    }
    /*--*/
}