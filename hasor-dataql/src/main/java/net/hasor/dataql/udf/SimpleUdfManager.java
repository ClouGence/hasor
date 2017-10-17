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
package net.hasor.dataql.udf;
import net.hasor.dataql.UdfManager;
import net.hasor.dataql.UdfSource;
import net.hasor.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 用于集中管理 UDF。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class SimpleUdfManager implements UdfManager {
    private final Map<String, UdfSource> udfSourceMap = new HashMap<String, UdfSource>();
    @Override
    public UdfSource getSourceByName(String sourceName) {
        return this.udfSourceMap.get(sourceName);
    }
    @Override
    public List<String> getSourceNames() {
        return new ArrayList<String>(this.udfSourceMap.keySet());
    }
    @Override
    public void addSource(String sourceName, UdfSource udfSource) {
        if (StringUtils.isBlank(sourceName) || DefaultSource.equalsIgnoreCase(sourceName)) {
            throw new UnsupportedOperationException("name of '" + sourceName + "' can't be added.");
        }
        this._addSource(sourceName, udfSource);
    }
    @Override
    public void addDefaultSource(UdfSource udfSource) {
        this._addSource(DefaultSource, udfSource);
    }
    //
    private void _addSource(String sourceName, UdfSource udfSource) {
        UdfSource source = this.udfSourceMap.get(sourceName);
        if (source != null) {
            source.putAll(udfSource);
        } else {
            this.udfSourceMap.put(sourceName.trim(), udfSource);
        }
    }
}