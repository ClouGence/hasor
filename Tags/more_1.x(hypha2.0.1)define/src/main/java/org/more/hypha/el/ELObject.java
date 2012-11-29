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
package org.more.hypha.el;
import org.more.hypha.ApplicationContext;
/**
 * EL对象，该接口可以明确分界。执行el表达式时对对象的读或者写操作。
 * Date : 2011-4-11
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ELObject {
    /**初始化{@link ELObject}对象。*/
    public void init(ApplicationContext context);
    /**如果该对象是不可写的则可以在实现该接口时返回true。*/
    public boolean isReadOnly();
    /**改变这个el对象值（或引用）。*/
    public void setValue(Object value) throws ELException;
    /**获取这个el对象。*/
    public Object getValue() throws ELException;
};