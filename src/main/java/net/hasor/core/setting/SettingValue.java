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
import java.util.ArrayList;
import java.util.List;
/**
 * @version : 2014年10月11日
 * @author 赵永春(zyc@hasor.net)
 */
public class SettingValue {
    private Object       defaultVar;
    private List<Object> varList = new ArrayList<Object>();
    //
    //
    public void newValue(Object value) {
        if (this.varList.contains(value) == false) {
            this.varList.add(value);
        }
        this.defaultVar = value;
    }
    //
    public Object getDefaultVar() {
        return this.defaultVar;
    }
    public List<Object> getVarList() {
        return this.varList;
    }
    public String toString() {
        StringBuffer buffer = new StringBuffer();
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