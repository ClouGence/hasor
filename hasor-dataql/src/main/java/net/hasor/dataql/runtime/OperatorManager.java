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
import net.hasor.core.utils.StringUtils;
import net.hasor.dataql.OperatorProcess;
import net.hasor.dataql.runtime.operator.BooleanUOP;
import net.hasor.dataql.runtime.operator.CompareDOP;
import net.hasor.dataql.runtime.operator.EvaluationDOP;
import net.hasor.dataql.runtime.operator.NumberUOP;

import java.util.HashMap;
import java.util.Map;
/**
 * 用于管理 UDF。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
class OperatorManager {
    public final static OperatorManager DEFAULT;

    static {
        DEFAULT = new OperatorManager();
        // .一元运算(注册一元操作符，第二个操作数类型无效但是必须要有，所以给 Object)
        DEFAULT.registryOperator(Symbol.Unary, "!", Boolean.class, Object.class, new BooleanUOP());
        DEFAULT.registryOperator(Symbol.Unary, "-", Number.class, Object.class, new NumberUOP());
        // .二元，求值运算
        DEFAULT.registryOperator(Symbol.Dyadic, "+", Number.class, Number.class, new EvaluationDOP());
        DEFAULT.registryOperator(Symbol.Dyadic, "-", Number.class, Number.class, new EvaluationDOP());
        DEFAULT.registryOperator(Symbol.Dyadic, "*", Number.class, Number.class, new EvaluationDOP());
        DEFAULT.registryOperator(Symbol.Dyadic, "/", Number.class, Number.class, new EvaluationDOP());
        DEFAULT.registryOperator(Symbol.Dyadic, "%", Number.class, Number.class, new EvaluationDOP());
        DEFAULT.registryOperator(Symbol.Dyadic, "\\", Number.class, Number.class, new EvaluationDOP());
        // .二元，比较运算
        DEFAULT.registryOperator(Symbol.Dyadic, ">", Comparable.class, Comparable.class, new CompareDOP());
        DEFAULT.registryOperator(Symbol.Dyadic, ">=", Comparable.class, Comparable.class, new CompareDOP());
        DEFAULT.registryOperator(Symbol.Dyadic, "<", Comparable.class, Comparable.class, new CompareDOP());
        DEFAULT.registryOperator(Symbol.Dyadic, "<=", Comparable.class, Comparable.class, new CompareDOP());
        DEFAULT.registryOperator(Symbol.Dyadic, "==", Comparable.class, Comparable.class, new CompareDOP());
        DEFAULT.registryOperator(Symbol.Dyadic, "!=", Comparable.class, Comparable.class, new CompareDOP());
        //            | < BIT_AND             : "&" >
        //                | < BIT_OR              : "|" >
        //                | < XOR                 : "^" >
        //
        //                | < LSHIFT              : "<<" >
        //                | < RSIGNEDSHIFT        : ">>" >
        //                | < RUNSIGNEDSHIFT      : ">>>" >
        //
        //                | < SC_OR               : "||" >
        //                | < SC_AND              : "&&" >
    }

    //
    private final Map<String, OperatorProcessManager> unaryProcessMap  = new HashMap<String, OperatorProcessManager>();
    private final Map<String, OperatorProcessManager> dyadicProcessMap = new HashMap<String, OperatorProcessManager>();
    //
    /** 添加 操作符 实现 */
    public void registryOperator(Symbol symbolType, String symbolName, OperatorProcess process) {
        this.registryOperator(symbolType, symbolName, Object.class, Object.class, process);
    }
    /** 添加 操作符 实现 */
    public void registryOperator(Symbol symbolType, String symbolName, Class<?> fstType, Class<?> secType, OperatorProcess process) {
        if (symbolType == null || StringUtils.isBlank(symbolName))
            throw new NullPointerException("symbolType or symbolName is null.");
        if (fstType == null || secType == null)
            throw new NullPointerException("fstType or secType is null.");
        if (process == null)
            throw new NullPointerException("OperatorProcess is null.");
        //
        //
        // .确定ProcessMap
        Map<String, OperatorProcessManager> mapping = null;
        if (Symbol.Unary == symbolType) {
            mapping = this.unaryProcessMap;
        }
        if (Symbol.Dyadic == symbolType) {
            mapping = this.dyadicProcessMap;
        }
        // .获取Manager
        OperatorProcessManager manager = mapping.get(symbolName);
        if (manager == null) {
            manager = new OperatorProcessManager(symbolName);
            mapping.put(symbolName, manager);
        }
        // .注册或重载
        manager.rewrite(fstType, secType, process);
    }
    public OperatorProcess findOperator(Symbol symbolType, String symbolName, Class<?> fstType, Class<?> secType) {
        // .一元
        if (Symbol.Unary == symbolType) {
            OperatorProcessManager manager = this.unaryProcessMap.get(symbolName);
            if (manager == null)
                return null;
            return manager.findOnFst(fstType);
        }
        // .二元
        if (Symbol.Dyadic == symbolType) {
            OperatorProcessManager manager = this.dyadicProcessMap.get(symbolName);
            if (manager == null)
                return null;
            return manager.findOnBoth(fstType, secType);
        }
        return null;
    }
    //
}