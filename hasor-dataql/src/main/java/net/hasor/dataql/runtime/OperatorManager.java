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
import net.hasor.dataql.runtime.operator.*;
import net.hasor.utils.StringUtils;

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
        DEFAULT.registryOperator(Symbol.Unary, "!", Boolean.class, new BooleanUOP());
        DEFAULT.registryOperator(Symbol.Unary, "-", Number.class, new NumberUOP());
        // .二元，求值运算
        DEFAULT.registryOperator(Symbol.Dyadic, "+", Number.class, Number.class, new NumberDOP());
        DEFAULT.registryOperator(Symbol.Dyadic, "-", Number.class, Number.class, new NumberDOP());
        DEFAULT.registryOperator(Symbol.Dyadic, "*", Number.class, Number.class, new NumberDOP());
        DEFAULT.registryOperator(Symbol.Dyadic, "/", Number.class, Number.class, new NumberDOP());
        DEFAULT.registryOperator(Symbol.Dyadic, "%", Number.class, Number.class, new NumberDOP());
        DEFAULT.registryOperator(Symbol.Dyadic, "\\", Number.class, Number.class, new NumberDOP());
        // .二元，数值比较运算
        Class[] classSet = { Number.class, Boolean.class };
        DEFAULT.registryOperator(Symbol.Dyadic, ">", Number.class, Number.class, new CompareDOP());
        DEFAULT.registryOperator(Symbol.Dyadic, ">=", Number.class, Number.class, new CompareDOP());
        DEFAULT.registryOperator(Symbol.Dyadic, "<", Number.class, Number.class, new CompareDOP());
        DEFAULT.registryOperator(Symbol.Dyadic, "<=", Number.class, Number.class, new CompareDOP());
        DEFAULT.registryOperator(Symbol.Dyadic, "==", classSet, classSet, new CompareDOP());
        DEFAULT.registryOperator(Symbol.Dyadic, "!=", classSet, classSet, new CompareDOP());
        // .二元，逻辑比较
        DEFAULT.registryOperator(Symbol.Dyadic, "||", Boolean.class, Boolean.class, new CompareDOP());
        DEFAULT.registryOperator(Symbol.Dyadic, "&&", Boolean.class, Boolean.class, new CompareDOP());
        // .二元，位运算
        DEFAULT.registryOperator(Symbol.Dyadic, "&", classSet, classSet, new BinaryDOP());
        DEFAULT.registryOperator(Symbol.Dyadic, "|", classSet, classSet, new BinaryDOP());
        DEFAULT.registryOperator(Symbol.Dyadic, "^", classSet, classSet, new BinaryDOP());
        DEFAULT.registryOperator(Symbol.Dyadic, "<<", classSet, classSet, new BinaryDOP());
        DEFAULT.registryOperator(Symbol.Dyadic, ">>", classSet, classSet, new BinaryDOP());
        DEFAULT.registryOperator(Symbol.Dyadic, ">>>", classSet, classSet, new BinaryDOP());
        // .通用类型运算
        DEFAULT.registryOperator(Symbol.Dyadic, "+", Object.class, Object.class, new StringJointDOP());
        DEFAULT.registryOperator(Symbol.Dyadic, "==", Object.class, Object.class, new ObjectEqDOP());
        DEFAULT.registryOperator(Symbol.Dyadic, "!=", Object.class, Object.class, new ObjectEqDOP());
    }

    //
    private final Map<String, OperatorProcessManager> unaryProcessMap  = new HashMap<String, OperatorProcessManager>();
    private final Map<String, OperatorProcessManager> dyadicProcessMap = new HashMap<String, OperatorProcessManager>();
    //
    /** 添加 操作符 实现 */
    public void registryOperator(Symbol symbolType, String symbolName, Class<?> opeType, OperatorProcess process) {
        this.registryOperator(symbolType, symbolName, opeType, Object.class, process);
    }
    /** 添加 操作符 实现 */
    private void registryOperator(Symbol symbolType, String symbolName, Class[] classSetA, Class[] classSetB, OperatorProcess process) {
        if (classSetA == null || classSetA.length == 0 || classSetB == null || classSetB.length == 0) {
            throw new NullPointerException("classSetA or classSetB is empty.");
        }
        for (Class fstType : classSetA) {
            for (Class secType : classSetB) {
                this.registryOperator(symbolType, symbolName, fstType, secType, process);
            }
        }
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
    //
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