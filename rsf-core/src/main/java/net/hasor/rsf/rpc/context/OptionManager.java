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
package net.hasor.rsf.rpc.context;
import java.util.HashMap;
import java.util.Map;
import net.hasor.rsf.RsfOptionSet;
/**
 * 
 * @version : 2015年1月24日
 * @author 赵永春(zyc@hasor.net)
 */
public class OptionManager implements RsfOptionSet {
    private final Map<String, String> optionMap = new HashMap<String, String>();
    //
    /**获取选项Key集合。*/
    public String[] getOptionKeys() {
        return this.optionMap.keySet().toArray(new String[this.optionMap.size()]);
    }
    /**获取选项数据*/
    public String getOption(String key) {
        return this.optionMap.get(key);
    }
    /**设置选项数据*/
    public void addOption(String key, String value) {
        this.optionMap.put(key, value);
    }
    /**删除选项数据*/
    public void removeOption(String key) {
        this.optionMap.remove(key);
    }
}