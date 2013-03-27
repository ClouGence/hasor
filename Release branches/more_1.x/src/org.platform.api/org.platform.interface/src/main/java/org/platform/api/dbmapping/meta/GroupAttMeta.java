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
package org.platform.api.dbmapping.meta;
import java.util.ArrayList;
import java.util.List;
/**
 * 分组属性
 * @version : 2013-1-27
 * @author 赵永春 (zyc@byshell.org)
 */
public class GroupAttMeta {
    /**属性名*/
    private String        name    = "userName";
    /**分组属性中的属性元素*/
    private List<AttMeta> attList = new ArrayList<AttMeta>();
    //
    //
    //
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<AttMeta> getAttList() {
        return attList;
    }
    public void setAttList(List<AttMeta> attList) {
        this.attList = attList;
    }
}