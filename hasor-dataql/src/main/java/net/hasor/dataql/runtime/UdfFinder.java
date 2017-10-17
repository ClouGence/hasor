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
import net.hasor.dataql.UDF;
import net.hasor.dataql.UdfManager;
import net.hasor.dataql.UdfSource;
import net.hasor.utils.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
/**
 * Udf查找器。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-10-17
 */
class UdfFinder extends HashMap<String, UDF> {
    public UdfFinder(UdfManager udfManager) {
        List<String> sourceNames = udfManager.getSourceNames();
        if (sourceNames == null || sourceNames.isEmpty()) {
            return;
        }
        for (String sourceName : sourceNames) {
            UdfSource source = udfManager.getSourceByName(sourceName);
            if (source == null) {
                continue;
            }
            Set<String> keySet = source.keySet();
            if (keySet.isEmpty()) {
                continue;
            }
            for (String udfName : keySet) {
                UDF udf = source.get(udfName);
                if (udf == null || StringUtils.isBlank(udfName)) {
                    continue;
                }
                if (StringUtils.isNotBlank(sourceName) && !UdfManager.DefaultSource.equalsIgnoreCase(sourceName)) {
                    super.put(sourceName + "." + udfName, udf);
                } else {
                    super.put(udfName, udf);
                }
            }
        }
    }
}