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
package net.hasor.graphql.dsl.domain;
import net.hasor.core.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class GraphUDF {
    private String                  funSpace   = null;
    private String                  funName    = null;
    private List<String>            paramNames = new ArrayList<String>();
    private Map<String, GraphValue> paramMap   = new HashMap<String, GraphValue>();
    //
    public GraphUDF(String funSpace, String funName) {
        this.funSpace = funSpace;
        this.funName = funName;
    }
    public void addParam(String paramName, GraphValue value) {
        if (this.paramMap.containsKey(paramName)) {
            throw new RuntimeException(paramName);
        }
        this.paramNames.add(paramName);
        this.paramMap.put(paramName, value);
    }
    public String getName() {
        if (StringUtils.isBlank(this.funSpace))
            return this.funName;
        return this.funSpace + "." + this.funName;
    }
    public List<String> getParamNames() {
        return this.paramNames;
    }
    public GraphValue getParam(String paramName) {
        return this.paramMap.get(paramName);
    }
}