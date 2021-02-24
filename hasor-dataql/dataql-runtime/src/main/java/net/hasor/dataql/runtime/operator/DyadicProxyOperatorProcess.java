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

/**
 * 二元运算代理。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
class DyadicProxyOperatorProcess implements OperatorMatch {
    private final Class<?>        fstType;
    private final Class<?>        secType;
    private final OperatorProcess process;

    public DyadicProxyOperatorProcess(Class<?> fstType, Class<?> secType, OperatorProcess process) {
        this.fstType = fstType;
        this.secType = secType;
        this.process = process;
    }

    @Override
    public Object doProcess(RuntimeLocation location, String operator, Object[] args, Hints option) throws QueryRuntimeException {
        return this.process.doProcess(location, operator, args, option);
    }

    @Override
    public boolean testMatch(Class<?>... fstType) {
        if (!this.fstType.isAssignableFrom(fstType[0])) {
            return false;
        }
        if (!this.secType.isAssignableFrom(fstType[1])) {
            return false;
        }
        return true;
    }
}
