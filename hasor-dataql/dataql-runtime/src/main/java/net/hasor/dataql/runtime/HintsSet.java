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
import net.hasor.dataql.Hints;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于封装 Hint。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class HintsSet implements Hints {
    private final Map<String, Object> optionMap;

    public HintsSet() {
        this.optionMap = new HashMap<>();
    }

    public HintsSet(Hints optionSet) {
        this.optionMap = new HashMap<>();
        optionSet.forEach(this.optionMap::put);
    }

    @Override
    public String[] getHints() {
        return this.optionMap.keySet().toArray(new String[0]);
    }

    /** 获取选项参数 */
    public Object getHint(String optionKey) {
        return this.optionMap.get(optionKey);
    }

    /** 删除选项参数 */
    public void removeHint(String key) {
        this.optionMap.remove(key);
    }

    /** 设置选项参数 */
    public void setHint(String hintName, String value) {
        this.optionMap.put(hintName, value);
    }

    /** 设置选项参数 */
    public void setHint(String hintName, Number value) {
        this.optionMap.put(hintName, value);
    }

    /** 设置选项参数 */
    public void setHint(String hintName, boolean value) {
        this.optionMap.put(hintName, value);
    }
}