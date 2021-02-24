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
package net.hasor.dataql.runtime.operator.ops;
import net.hasor.dataql.Hints;
import net.hasor.dataql.parser.location.RuntimeLocation;
import net.hasor.dataql.runtime.operator.OperatorProcess;

/**
 * 字符串拼接
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class StringJointDOP implements OperatorProcess {
    @Override
    public Object doProcess(RuntimeLocation location, String operator, Object[] args, Hints option) {
        String str1 = args[0] == null ? "null" : args[0].toString();
        String str2 = args[1] == null ? "null" : args[1].toString();
        return str1 + str2;
    }
}
