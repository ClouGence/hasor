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
package org.more.core.global.gp;
import java.util.ArrayList;
import org.more.core.error.SupportException;
import org.more.core.global.Global;
import org.more.core.global.GlobalProperty;
/**
* _global.cfgList
* @version : 2011-9-30
* @author 赵永春 (zyc@byshell.org)
*/
public class CFGList_GP implements GlobalProperty {
    private CFGList list = null;
    public Object getValue(Global global) {
        if (this.list == null)
            this.list = new CFGList(global);
        return this.list;
    }
    public void setValue(Object value, Global global) {
        throw new SupportException("_global.cfgList，属性不支持写操作。");
    }
}
/**该类是为了实现在el中以list结构访问cfgList内置属性。*/
class CFGList extends ArrayList<Object> {
    private static final long serialVersionUID = -5144216708454403683L;
    private Global            global           = null;
    public CFGList(Global global) {
        this.global = global;
    }
    public Object get(int index) {
        return this.global.getScope(index).toMap();
    }
}