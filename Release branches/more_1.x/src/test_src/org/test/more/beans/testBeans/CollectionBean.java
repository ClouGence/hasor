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
package org.test.more.beans.testBeans;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * 
 * Date : 2009-11-25
 * @author Administrator
 */
@SuppressWarnings("unchecked")
public class CollectionBean {
    private Map  objectMap;
    private List objectList;
    private Set  objectSet;
    //========================================================================================Field
    public Map getObjectMap() {
        return objectMap;
    }
    public void setObjectMap(Map objectMap) {
        this.objectMap = objectMap;
    }
    public List getObjectList() {
        return objectList;
    }
    public void setObjectList(List objectList) {
        this.objectList = objectList;
    }
    public Set getObjectSet() {
        return objectSet;
    }
    public void setObjectSet(Set objectSet) {
        this.objectSet = objectSet;
    }
}