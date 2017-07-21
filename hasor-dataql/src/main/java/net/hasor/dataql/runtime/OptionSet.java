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
package net.hasor.dataql.runtime;
import net.hasor.dataql.Option;

import java.util.HashMap;
import java.util.Map;
/**
 * 用于封装 Option。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
class OptionSet implements Option {
    private Map<String, Object> optionMap;
    //
    OptionSet(Option optionSet) {
        this.optionMap = new HashMap<String, Object>();
        for (String name : optionSet.getOptionNames()) {
            this.optionMap.put(name, optionSet.getOption(name));
        }
    }
    OptionSet() {
        this.optionMap = new HashMap<String, Object>();
    }
    //
    @Override
    public String[] getOptionNames() {
        return this.optionMap.keySet().toArray(new String[this.optionMap.size()]);
    }
    /** 获取选项参数 */
    public Object getOption(String optionKey) {
        return this.optionMap.get(optionKey);
    }
    /** 删除选项参数 */
    public void removeOption(String key) {
        this.optionMap.remove(key);
    }
    /** 设置选项参数 */
    public void setOption(String key, String value) {
        this.optionMap.put(key, value);
    }
    /** 设置选项参数 */
    public void setOption(String key, Number value) {
        this.optionMap.put(key, value);
    }
    /** 设置选项参数 */
    public void setOption(String key, boolean value) {
        this.optionMap.put(key, value);
    }
}