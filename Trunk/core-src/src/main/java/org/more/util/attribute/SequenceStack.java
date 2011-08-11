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
package org.more.util.attribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
/**
 * 
 * @version : 2011-7-22
 * @author 赵永春 (zyc@byshell.org)
 */
public class SequenceStack implements IAttribute {
    private LinkedList<IAttribute> attList = new LinkedList<IAttribute>();
    //
    public void putStack(IAttribute scope) {
        if (this.attList.contains(scope) == false)
            this.attList.addFirst(scope);
    };
    public boolean contains(String name) {
        for (IAttribute iatt : this.attList)
            return iatt.contains(name);
        return false;
    };
    public Object getAttribute(String name) {
        for (IAttribute iatt : this.attList)
            return iatt.getAttribute(name);
        return null;
    };
    public String[] getAttributeNames() {
        ArrayList<String> as = new ArrayList<String>();
        for (IAttribute iatt : this.attList)
            for (String n : iatt.getAttributeNames())
                if (as.contains(n) == false)
                    as.add(n);
        String[] array = new String[as.size()];
        as.toArray(array);
        return array;
    };
    public Map<String, Object> toMap() {
        HashMap<String, Object> as = new HashMap<String, Object>();
        for (IAttribute iatt : this.attList) {
            Map<String, Object> map = iatt.toMap();
            for (String n : map.keySet())
                if (as.containsKey(n) == false)
                    as.put(n, map.get(n));
        }
        return as;
    };
    /**该方法只会对最后一个加入的{@link IAttribute}对象起作用。*/
    public void clearAttribute() {
        this.attList.get(0).clearAttribute();
    };
    /**该方法只会对最后一个加入的{@link IAttribute}对象起作用。*/
    public void removeAttribute(String name) {
        this.attList.get(0).removeAttribute(name);
    };
    /**该方法只会对最后一个加入的{@link IAttribute}对象起作用。*/
    public void setAttribute(String name, Object value) {
        this.attList.get(0).setAttribute(name, value);
    };
};