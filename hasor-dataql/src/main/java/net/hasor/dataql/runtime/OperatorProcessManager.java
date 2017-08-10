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
import java.util.List;
/**
 * 用于管理 UDF。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
class OperatorProcessManager {
    private String symbolName;
    private List<OperatorProcessItem> processList = new ArrayList<OperatorProcessItem>();
    public OperatorProcessManager(String symbolName) {
        this.symbolName = symbolName;
    }
    //
    public void rewrite(Class<?> fstType, Class<?> secType, OperatorProcess process) {
        OperatorProcessItem item = new OperatorProcessItem(0, fstType, secType);
        item.process = process;
        this.processList.add(item);
    }
    public OperatorProcess findOnFst(Class<?> fstType) {
        if (fstType == null) {
            return null;
        }
        for (OperatorProcessItem item : this.processList) {
            if (item.testMatch(fstType, Object.class)) {
                return item.process;
            }
        }
        return null;
    }
    public OperatorProcess findOnBoth(Class<?> fstType, Class<?> secType) {
        if (fstType == null || secType == null) {
            return null;
        }
        for (OperatorProcessItem item : this.processList) {
            if (item.testMatch(fstType, secType)) {
                return item.process;
            }
        }
        return null;
    }
    //
    private static class OperatorProcessItem {
        private int             priority;
        private Class<?>        fstType;
        private Class<?>        secType;
        public  OperatorProcess process;
        //
        OperatorProcessItem(int priority, Class<?> fstType, Class<?> secType) {
            this.priority = priority;
            this.fstType = fstType;
            this.secType = secType;
        }
        public boolean testMatch(Class<?> fstType, Class<?> secType) {
            if (!this.fstType.isAssignableFrom(fstType))
                return false;
            if (!this.secType.isAssignableFrom(secType))
                return false;
            return true;
        }
    }
}