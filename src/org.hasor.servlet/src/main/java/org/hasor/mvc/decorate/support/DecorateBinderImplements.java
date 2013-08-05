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
package org.hasor.mvc.decorate.support;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hasor.context.ApiBinder;
import org.hasor.mvc.decorate.DecorateBinder;
import org.hasor.mvc.decorate.DecorateFilter;
import org.more.util.ArrayUtils;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.internal.UniqueAnnotations;
/**
 * 该类是{@link ApiBinder}接口实现。
 * @version : 2013-4-10
 * @author 赵永春 (zyc@byshell.org)
 */
class DecorateBinderImplements implements Module, DecorateBinder {
    private DecorateFilterModuleBuilder filterModuleBinder = new DecorateFilterModuleBuilder(); /*Filters*/
    @Override
    public DecorateFilterBindingBuilder decFilter(String contentType, String urlPattern, String... morePatterns) {
        return this.filterModuleBinder.decFilterPattern(contentType, ArrayUtils.newArrayList(morePatterns, urlPattern));
    };
    @Override
    public DecorateFilterBindingBuilder decFilterRegex(String contentType, String regex, String... regexes) {
        return this.filterModuleBinder.decFilterRegex(contentType, ArrayUtils.newArrayList(regexes, regex));
    };
    @Override
    public void configure(Binder binder) {
        binder.install(this.filterModuleBinder);
    }
    /*----------------------------------------------------------------------------------*/
    private class DecorateFilterModuleBuilder implements Module {
        /*Filter 定义*/
        private final List<DecorateFilterDefine> filterDefinitions = new ArrayList<DecorateFilterDefine>();
        //
        public DecorateFilterBindingBuilder decFilterPattern(String contentType, List<String> servletPattern) {
            return new DecorateFilterBindingBuilderImpl(contentType, UriPatternType.SERVLET, servletPattern);
        }
        public DecorateFilterBindingBuilder decFilterRegex(String contentType, List<String> regexPattern) {
            return new DecorateFilterBindingBuilderImpl(contentType, UriPatternType.REGEX, regexPattern);
        }
        @Override
        public void configure(Binder binder) {
            /*将FilterDefinition绑定到Guice身上，在正式使用时利用findBindingsByType方法将其找回来。*/
            for (DecorateFilterDefine define : filterDefinitions)
                binder.bind(DecorateFilterDefine.class).annotatedWith(UniqueAnnotations.create()).toProvider(define);
        }
        /*-----------------------------------------------------------------------------------------*/
        /** DecorateFilterBindingBuilder接口实现 */
        private class DecorateFilterBindingBuilderImpl implements DecorateFilterBindingBuilder {
            private String         contentType    = null;
            private UriPatternType uriPatternType = null;
            private List<String>   uriPatterns    = null;
            // 
            public DecorateFilterBindingBuilderImpl(String contentType, UriPatternType uriPatternType, List<String> uriPatterns) {
                this.contentType = contentType;
                this.uriPatternType = uriPatternType;
                this.uriPatterns = uriPatterns;
            }
            @Override
            public void through(Class<? extends DecorateFilter> filterKey) {
                through(Key.get(filterKey));
            }
            @Override
            public void through(Key<? extends DecorateFilter> filterKey) {
                through(filterKey, new HashMap<String, String>());
            }
            @Override
            public void through(DecorateFilter filter) {
                through(filter, new HashMap<String, String>());
            }
            @Override
            public void through(Class<? extends DecorateFilter> filterKey, Map<String, String> initParams) {
                // Careful you don't accidentally make this method recursive, thank you IntelliJ IDEA!
                through(Key.get(filterKey), initParams);
            }
            @Override
            public void through(Key<? extends DecorateFilter> filterKey, Map<String, String> initParams) {
                through(filterKey, initParams, null);
            }
            @Override
            public void through(DecorateFilter filter, Map<String, String> initParams) {
                Key<DecorateFilter> filterKey = Key.get(DecorateFilter.class, UniqueAnnotations.create());
                through(filterKey, initParams, filter);
            }
            private void through(Key<? extends DecorateFilter> filterKey, Map<String, String> initParams, DecorateFilter filterInstance) {
                for (String pattern : uriPatterns)
                    filterDefinitions.add(new DecorateFilterDefine(pattern, this.contentType, filterKey, UriPatternType.get(uriPatternType, pattern), initParams, filterInstance));
            }
        }
    }
}