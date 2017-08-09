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
import net.hasor.dataql.OperatorProcess;

import java.util.HashMap;
import java.util.Map;
/**
 * 用于管理 UDF。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
class OperatorManager {
    private final Map<String, ProcessManager> unaryProcessMap  = new HashMap<String, ProcessManager>();
    private final Map<String, ProcessManager> dyadicProcessMap = new HashMap<String, ProcessManager>();
    //
    //
    /** 添加 UDF */
    public OperatorProcess registryOperator(Symbol symbol, String dyadicSymbol, Class<?> fstType, Class<?> secType) {
        //        if (this.udfMap.containsKey(udfName)) {
        //            throw new IllegalStateException("udf name ‘" + udfName + "’ already exist.");
        //        }
        //        this.udfMap.put(udfName, udf);
        return null;
    }
    public OperatorProcess findOperator(Symbol symbolType, String symbolName, Class<?> fstType, Class<?> secType) {
        // .一元
        if (Symbol.Unary == symbolType) {
            ProcessManager manager = this.unaryProcessMap.get(symbolName);
            return manager.findOnFst(fstType);
        }
        // .二元
        if (Symbol.Dyadic == symbolType) {
            ProcessManager manager = this.dyadicProcessMap.get(symbolName);
            return manager.findOnBoth(fstType, secType);
        }
        return null;
    }
    //
}
class ProcessManager {
    public OperatorProcess findOnFst(Class<?> fstType) {
    }
    public OperatorProcess findOnBoth(Class<?> fstType, Class<?> secType) {
    }
}