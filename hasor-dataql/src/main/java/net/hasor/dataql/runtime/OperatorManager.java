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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public void registryOperator(Symbol symbolType, String symbolName, Class<?> fstType, Class<?> secType, OperatorProcess process) {
        // .确定ProcessMap
        Map<String, ProcessManager> mapping = null;
        if (Symbol.Unary == symbolType) {
            mapping = this.unaryProcessMap;
        }
        if (Symbol.Dyadic == symbolType) {
            mapping = this.dyadicProcessMap;
        }
        // .获取Manager
        ProcessManager manager = mapping.get(symbolName);
        if (manager == null) {
            manager = new ProcessManager(symbolName);
            mapping.put(symbolName, manager);
        }
        // .注册或重载
        manager.rewrite(fstType, secType, process);
    }
    public OperatorProcess findOperator(Symbol symbolType, String symbolName, Class<?> fstType, Class<?> secType) {
        // .一元
        if (Symbol.Unary == symbolType) {
            ProcessManager manager = this.unaryProcessMap.get(symbolName);
            if (manager == null)
                return null;
            return manager.findOnFst(fstType);
        }
        // .二元
        if (Symbol.Dyadic == symbolType) {
            ProcessManager manager = this.dyadicProcessMap.get(symbolName);
            if (manager == null)
                return null;
            return manager.findOnBoth(fstType, secType);
        }
        return null;
    }
    //
}
class ProcessManager {
    private String symbolName;
    private List<ProcessItem> processList = new ArrayList<ProcessItem>();
    public ProcessManager(String symbolName) {
        this.symbolName = symbolName;
    }
    //
    public void rewrite(Class<?> fstType, Class<?> secType, OperatorProcess process) {
        // TODO sss
        ProcessItem item = new ProcessItem();
        item.fstType = fstType;
        item.secType = secType;
        item.process = process;
        processList.add(item);
    }
    public OperatorProcess findOnFst(Class<?> fstType) {
        if (fstType == null) {
            return null;
        }
        for (ProcessItem item : this.processList) {
            if (item.fstType.isAssignableFrom(fstType)) {
                return item.process;
            }
        }
        return null;
    }
    public OperatorProcess findOnBoth(Class<?> fstType, Class<?> secType) {
        if (fstType == null || secType == null) {
            return null;
        }
        for (ProcessItem item : this.processList) {
            if (item.fstType != null && !item.fstType.isAssignableFrom(fstType))
                continue;
            if (item.secType != null && !item.secType.isAssignableFrom(secType))
                continue;
            //
            // TODO ssss
            if (item.fstType == null && item.secType.isAssignableFrom(secType))
                return item.process;
            if (item.secType == null && item.fstType.isAssignableFrom(fstType))
                return item.process;
        }
        return null;
    }
}
class ProcessItem {
    public Class<?>        fstType;
    public Class<?>        secType;
    public OperatorProcess process;
}
