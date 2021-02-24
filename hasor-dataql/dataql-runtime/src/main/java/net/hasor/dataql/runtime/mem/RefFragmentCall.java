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
package net.hasor.dataql.runtime.mem;
import net.hasor.dataql.FragmentProcess;
import net.hasor.dataql.Hints;
import net.hasor.dataql.Udf;
import net.hasor.dataql.parser.location.RuntimeLocation;
import net.hasor.dataql.runtime.QueryRuntimeException;

import java.util.*;

/**
 * 代理 Fragment 使其成为 UDF.
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class RefFragmentCall implements Udf {
    private final RuntimeLocation location;
    private final boolean         isBach;
    private final FragmentProcess fragmentProcess;

    public RefFragmentCall(RuntimeLocation location, boolean isBach, FragmentProcess fragmentProcess) {
        this.location = location;
        this.isBach = isBach;
        this.fragmentProcess = fragmentProcess;
    }

    @Override
    public Object call(Hints readOnly, Object... params) throws Throwable {
        String fragmentString = params[1].toString();
        Map<String, Object> fragmentParams = (Map<String, Object>) params[0];
        if (this.isBach) {
            List<Map<String, Object>> fragmentParamsArray = new ArrayList<>();
            Map<String, Integer> argsLengthMap = new TreeMap<>();
            int lastSize = -1;
            boolean argsLengthError = false;
            //
            for (String key : fragmentParams.keySet()) {
                // .参数类型校验
                Object dataModel = fragmentParams.get(key);
                if (!(dataModel instanceof List)) {
                    throw new QueryRuntimeException(this.location, "The batch fragment args must be an array.");
                }
                List<?> listData = (List<?>) dataModel;
                //
                // .参数长度校验
                int tmpSize = listData.size();
                argsLengthMap.put(key, tmpSize);
                if (lastSize < 0) {
                    lastSize = tmpSize;
                } else {
                    if (tmpSize != lastSize) {
                        argsLengthError = true;
                    }
                }
                // .参数拆分
                //      param1,    param2        => [ {param1:1,param2:1}, {param1:2,param2:2}, {param1:3,param2:3} ]
                //        [1,2,3]    [1,2,3]
                //
                if (!argsLengthError) {
                    for (int i = 0; i < listData.size(); i++) {
                        if (i >= fragmentParamsArray.size()) {
                            fragmentParamsArray.add(new HashMap<>());
                        }
                        Map<String, Object> objectMap = fragmentParamsArray.get(i);
                        objectMap.put(key, listData.get(i));
                    }
                }
            }
            if (argsLengthError) {
                StringBuilder strBuild = new StringBuilder();
                argsLengthMap.forEach((key, integer) -> {
                    strBuild.append(key + "=" + integer + ",");
                });
                if (strBuild.length() > 0) {
                    strBuild.deleteCharAt(strBuild.length() - 1);
                }
                throw new QueryRuntimeException(this.location, "batch fragment,All args must have the same length -> [" + strBuild.toString() + "]");
            }
            //
            return this.fragmentProcess.batchRunFragment(readOnly, fragmentParamsArray, fragmentString);
        } else {
            return this.fragmentProcess.runFragment(readOnly, fragmentParams, fragmentString);
        }
    }
}
