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
import java.util.Map;
import org.more.core.error.SupportException;
import org.more.util.StringConvertUtil;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
/**
 * 内置对象
 * @version : 2011-9-29
 * @author 赵永春 (zyc@byshell.org)
 */
class GlobalObject extends AttBase<Object> implements IAttribute<Object> {
    private static final long           serialVersionUID = -4297024677619713055L;
    private Global                      onGlobal         = null;
    private Map<String, GlobalProperty> propertys        = null;
    //
    public GlobalObject(Global onGlobal) {
        this.onGlobal = onGlobal;
        //植入固定的内置属性
        this.addGlobalProperty("enableEL", new EnableEL_GP());
        this.addGlobalProperty("enableJson", new EnableJson_GP());
        this.addGlobalProperty("context", new Context_GP());
        this.addGlobalProperty("groupSize", new GroupCount_GP());
        this.addGlobalProperty("size", new Count_GP());
    };
    public void addGlobalProperty(String name, GlobalProperty property) {
        if (propertys.containsKey(name) == false)
            propertys.put(name, property);
    };
    public Object getAttribute(String name) {
        System.out.println("ggggg   :" + name);
        //属性读取操作。
        if (propertys.containsKey(name) == false)
            return super.getAttribute(name);
        GlobalProperty property = propertys.get(name);
        return property.getValue(this.onGlobal);
    };
    public void setAttribute(String name, Object newValue) {
        System.out.println("ggggg   :" + name);
        //属性写入操作。
        if (propertys.containsKey(name) == false)
            throw new SupportException("Global，不支持该方法。");
        GlobalProperty property = propertys.get(name);
        property.setValue(newValue, this.onGlobal);
    };
};
/**
 * _global.enableEL
 * @version : 2011-9-30
 * @author 赵永春 (zyc@byshell.org)
 */
class EnableEL_GP implements GlobalProperty {
    public Object getValue(Global global) {
        String oriString = global.getOriginalString("_global.enableEL");
        return StringConvertUtil.parseBoolean(oriString, true);
    }
    public void setValue(Object value, Global global) {
        throw new SupportException("_global.enableEL，属性不支持写操作。");
    }
}
/**
 * _global.enableJson
 * @version : 2011-9-30
 * @author 赵永春 (zyc@byshell.org)
 */
class EnableJson_GP implements GlobalProperty {
    public Object getValue(Global global) {
        String oriString = global.getOriginalString("_global.enableJson");
        return StringConvertUtil.parseBoolean(oriString, true);
    }
    public void setValue(Object value, Global global) {
        throw new SupportException("_global.enableJson，属性不支持写操作。");
    }
}
/**
 * _global.context
 * @version : 2011-9-30
 * @author 赵永春 (zyc@byshell.org)
 */
class Context_GP implements GlobalProperty {
    public Object getValue(Global global) {
        return global.getContext();
    }
    public void setValue(Object value, Global global) {
        throw new SupportException("_global.context，属性不支持写操作。");
    }
}
/**
 * _global.groupSize
 * @version : 2011-9-30
 * @author 赵永春 (zyc@byshell.org)
 */
class GroupCount_GP implements GlobalProperty {
    public Object getValue(Global global) {
        return global.getConfigGroupCount();
    }
    public void setValue(Object value, Global global) {
        throw new SupportException("_global.groupSize，属性不支持写操作。");
    }
}
/**
* _global.size
* @version : 2011-9-30
* @author 赵永春 (zyc@byshell.org)
*/
class Count_GP implements GlobalProperty {
    public Object getValue(Global global) {
        return global.getConfigAllCount();
    }
    public void setValue(Object value, Global global) {
        throw new SupportException("_global.size，属性不支持写操作。");
    }
}