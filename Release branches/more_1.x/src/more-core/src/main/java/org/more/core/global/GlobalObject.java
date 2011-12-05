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
package org.more.core.global;
import java.util.HashMap;
import java.util.Map;
import org.more.core.global.gp.CFGList_GP;
import org.more.core.global.gp.Count_GP;
import org.more.core.global.gp.EnableEL_GP;
import org.more.core.global.gp.EnableJson_GP;
import org.more.core.global.gp.GroupCount_GP;
import org.more.util.attribute.IAttribute;
import org.more.util.attribute.SequenceStack;
/**
 * 内置对象
 * @version : 2011-9-29
 * @author 赵永春 (zyc@byshell.org)
 */
class GlobalObject extends HashMap<String, Object> {
    private static final long           serialVersionUID = -4297024677619713055L;
    /**动态操作Global属性的属性缓存器。*/
    public final static String          _Global          = "_global";
    private Global                      onGlobal         = null;
    private Map<String, GlobalProperty> propertys        = null;
    //
    public GlobalObject(Global onGlobal) {
        this.onGlobal = onGlobal;
        this.propertys = new HashMap<String, GlobalProperty>();
        //植入固定的内置属性
        this.propertys.put("enableEL", new EnableEL_GP());
        this.propertys.put("enableJson", new EnableJson_GP());
        this.propertys.put("groupCount", new GroupCount_GP());
        this.propertys.put("count", new Count_GP());
        this.propertys.put("cfgList", new CFGList_GP());
    };
    public boolean containsKey(Object key) {
        if (this.propertys.containsKey(key) == false)
            return super.containsKey(key);
        return true;
    }
    public Object get(Object name) {
        //XXX:内置属性读，System.out.println("get   :" + name);
        if (this.propertys.containsKey(name) == false) {
            if (name instanceof Integer)
                return new LastList(this.onGlobal.getScope((Integer) name));
            IAttribute<Object> iatt = this.onGlobal.getScope((String) name);
            return (iatt != null) ? new LastList(iatt) : super.get(name);
        }
        GlobalProperty property = propertys.get(name);
        if (property == null)
            return null;//内置对象为空
        return property.getValue(this.onGlobal);
    }
    public Object put(String name, Object newValue) {
        //XXX:内置属性写，System.out.println("set   :" + name);
        if (propertys.containsKey(name) == false)
            return super.put(name, newValue);
        GlobalProperty property = propertys.get(name);
        property.setValue(newValue, this.onGlobal);
        return newValue;
    }
};
/**处理“_global['global.properties'][1].config_1”*/
class LastList extends HashMap<Object, Object> {
    private static final long  serialVersionUID = 5149441075415231185L;
    private IAttribute<Object> iatt             = null;
    public LastList(IAttribute<Object> iatt) {
        this.iatt = iatt;
    }
    public Object get(Object key) {
        if (key.equals("count") == true)
            return this.iatt.size();
        if (key instanceof Integer && this.iatt instanceof SequenceStack)
            return new LastMap(((SequenceStack) this.iatt).getIndex((Integer) key));
        return this.iatt.getAttribute((String) key);
    }
}
/**处理“_global['global.properties'][1].config_1”*/
class LastMap extends HashMap<Object, Object> {
    private static final long  serialVersionUID = 5149441075415231185L;
    private IAttribute<Object> iatt             = null;
    public LastMap(IAttribute<Object> iatt) {
        this.iatt = iatt;
    }
    public Object get(Object key) {
        if (key.equals("count") == true)
            return this.iatt.size();
        return this.iatt.getAttribute((String) key);
    }
}