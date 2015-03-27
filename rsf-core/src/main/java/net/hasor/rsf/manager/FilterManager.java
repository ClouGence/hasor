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
package net.hasor.rsf.manager;
import net.hasor.core.Provider;
import net.hasor.core.binder.InstanceProvider;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.domain.FilterDefine;
/**
 * 
 * @version : 2015年3月28日
 * @author 赵永春(zyc@hasor.net)
 */
public class FilterManager extends ComfitManager<FilterDefine> {
    public FilterDefine createDefine(String filterID, String forServiceID, RsfFilter filter) {
        return createDefine(filterID, forServiceID, new InstanceProvider<RsfFilter>(filter));
    }
    public FilterDefine createDefine(String filterID, String forServiceID, Provider<? extends RsfFilter> provider) {
        return new FilterDefine(filterID, forServiceID, provider);
    }
}