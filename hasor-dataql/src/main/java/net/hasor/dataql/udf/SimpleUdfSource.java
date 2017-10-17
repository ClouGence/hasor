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
import net.hasor.dataql.UDF;
import net.hasor.dataql.UdfSource;

import java.util.HashMap;
import java.util.Map;
/**
 * 用于管理 UDF。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class SimpleUdfSource extends HashMap<String, UDF> implements UdfSource {
    public SimpleUdfSource() {
    }
    public SimpleUdfSource(Map<String, UDF> udfMap) {
        super(udfMap);
    }
    //
    @Override
    public UDF findUdf(String udfName) {
        return super.get(udfName);
    }
    public void addUdf(String udfName, UDF udf) {
        super.put(udfName, udf);
    }
}