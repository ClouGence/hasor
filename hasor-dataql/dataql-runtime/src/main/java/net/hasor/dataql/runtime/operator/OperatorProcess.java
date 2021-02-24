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
package net.hasor.dataql.runtime.operator;
import net.hasor.dataql.Hints;
import net.hasor.dataql.parser.location.RuntimeLocation;
import net.hasor.dataql.runtime.QueryRuntimeException;
import net.hasor.utils.StringUtils;

/**
 * 一元或二元运算，用于运算符重载。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public interface OperatorProcess {
    /**执行运算*/
    public Object doProcess(RuntimeLocation location, String operator, Object[] args, Hints option) throws QueryRuntimeException;

    public default boolean testIn(String[] dataSet, String test) {
        if (dataSet == null || dataSet.length == 0 || StringUtils.isBlank(test)) {
            return false;
        }
        for (String str : dataSet) {
            if (test.equalsIgnoreCase(str)) {
                return true;
            }
        }
        return false;
    }
}
