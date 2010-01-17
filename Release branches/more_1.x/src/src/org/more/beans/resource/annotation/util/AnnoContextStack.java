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
package org.more.beans.resource.annotation.util;
import org.more.util.attribute.AttBase;
/**
 * 处理注解的节点堆栈，从处理类的注解开始创建根堆栈往下每一层类元素都创建一个新的堆栈。<br/>
 * 通过堆栈可以向父节点传递主要的数据和一些附带数据。
 * @version 2010-1-10
 * @author 赵永春 (zyc@byshell.org)
 */
public class AnnoContextStack extends AttBase {
    /**  */
    private static final long serialVersionUID = 4300339262589765696L;
    private AnnoContextStack  parent           = null;                //当前堆栈的父级堆栈。
    /**当前堆栈中保存的重要数据对象。*/
    public Object             context          = null;
    /**正在被扫描的类*/
    private Class<?>          atClass          = null;
    /**当前堆栈所处作用域*/
    private AnnoScopeEnum     scope            = null;
    AnnoContextStack(AnnoContextStack parent, Class<?> atClass, AnnoScopeEnum scope) {
        this.parent = parent;
        this.atClass = atClass;
        this.scope = scope;
    }
    /**获取当前堆栈的父级堆栈。*/
    public AnnoContextStack getParent() {
        return this.parent;
    }
    /**正在被扫描的类*/
    public Class<?> getAtClass() {
        return atClass;
    }
    /**当前堆栈所处作用域*/
    public AnnoScopeEnum getScope() {
        return scope;
    }
}