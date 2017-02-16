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
package net.hasor.rsf.domain;
import net.hasor.rsf.RsfOptionSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
/**
 *
 * @version : 2015年1月24日
 * @author 赵永春(zyc@hasor.net)
 */
public class OptionInfo implements RsfOptionSet {
    private final Set<String>         optionKeys = new HashSet<String>();
    private final Map<String, String> optionMap  = new HashMap<String, String>();
    //
    /**获取选项Key集合。*/
    public String[] getOptionKeys() {
        return this.optionKeys.toArray(new String[this.optionKeys.size()]);
    }
    /**获取选项数据*/
    public String getOption(String key) {
        return this.optionMap.get(key);
    }
    /**设置选项数据*/
    public void addOption(String key, String value) {
        this.optionKeys.add(key);
        this.optionMap.put(key, value);
    }
    /**删除选项数据*/
    public void removeOption(String key) {
        this.optionKeys.remove(key);
        this.optionMap.remove(key);
    }
    public void addOptionMap(RsfOptionSet optSet) {
        if (optSet == null) {
            return;
        }
        for (String key : optSet.getOptionKeys()) {
            this.addOption(key, optSet.getOption(key));
        }
    }
}