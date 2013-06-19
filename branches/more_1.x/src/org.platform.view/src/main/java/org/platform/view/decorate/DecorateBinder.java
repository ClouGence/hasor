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
package org.platform.view.decorate;
import java.util.Map;
import com.google.inject.Key;
/**
 * 
 * @version : 2013-6-19
 * @author 赵永春 (zyc@byshell.org)
 */
public interface DecorateBinder {
    /**使用传统表达式，创建一个{@link DecorateFilterBindingBuilder}。*/
    public DecorateFilterBindingBuilder decFilter(String contentType, String urlPattern, String... morePatterns);
    /**使用正则表达式，创建一个{@link DecorateFilterBindingBuilder}。*/
    public DecorateFilterBindingBuilder decFilterRegex(String contentType, String regex, String... regexes);
    /*----------------------------------------------------------------------------*/
    /**负责配置DecorateFilter，参考Guice 3.0接口设计。*/
    public static interface DecorateFilterBindingBuilder {
        public void through(Class<? extends DecorateFilter> filterKey);
        public void through(Key<? extends DecorateFilter> filterKey);
        public void through(DecorateFilter filter);
        public void through(Class<? extends DecorateFilter> filterKey, Map<String, String> initParams);
        public void through(Key<? extends DecorateFilter> filterKey, Map<String, String> initParams);
        public void through(DecorateFilter filter, Map<String, String> initParams);
    }
}