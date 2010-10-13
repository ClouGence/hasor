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
package org.more.hypha.event;
import org.more.hypha.Event;
import org.more.hypha.beans.AbstractBeanDefine;
import org.more.hypha.DefineResource;
/**
 * 开始初始化过程事件，该事件是{@link DefineResource}。收到一个新{@link AbstractBeanDefine}定义添加时引发。
 * @version 2010-10-10
 * @author 赵永春 (zyc@byshell.org)
 */
public class AddBeanDefineEvent extends Event {
    private AbstractBeanDefine define = null;
    /**创建{@link AddBeanDefineEvent}对象。*/
    public AddBeanDefineEvent(Object target, AbstractBeanDefine define) {
        super(target);
    }
    /**获取添加的新{@link AbstractBeanDefine}定义。*/
    public AbstractBeanDefine getDefine() {
        return define;
    }
}