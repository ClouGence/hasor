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
package net.hasor.core.setting;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
/**
 * @version : 2014年10月11日
 * @author 赵永春(zyc@hasor.net)
 */
public class SettingValue {
    private String space;
    private Object defaultVar;
    private List<Object> varList = new CopyOnWriteArrayList<Object>();
    //
    SettingValue(String space) {
        this.space = space;
    }
    //
    public String getSpace() {
        return space;
    }
    //
    public void newValue(Object value) {
        if (!this.varList.contains(value)) {
            this.varList.add(value);
        }
        this.defaultVar = value;
    }
    //
    public Object getDefaultVar() {
        return this.defaultVar;
    }
    public void setDefaultVar(Object defaultVar) {
        this.defaultVar = defaultVar;
    }
    public List<Object> getVarList() {
        return this.varList;
    }
    //
    public void replace(int index, Object oldVar, Object newVar) {
        if (this.defaultVar == oldVar) {
            this.defaultVar = newVar;
        }
        //
        if (index >= this.varList.size()) {
            return;
        }
        if (this.varList.get(index) != oldVar) {
            return;
        }
        this.varList.set(index, newVar);
    }
    //
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        for (Object obj : this.varList) {
            if (obj != null) {
                buffer.append(obj.toString());
            }
        }
        return buffer.toString();
    }
    public void clear() {
        this.varList.clear();
        this.defaultVar = null;
    }
}