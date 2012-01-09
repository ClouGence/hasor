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
import org.more.core.error.SupportException;
import org.more.core.global.AbstractGlobal;
import org.more.core.global.GlobalProperty;
/**
* _global.count
* @version : 2011-9-30
* @author 赵永春 (zyc@byshell.org)
*/
public class Count_GP implements GlobalProperty {
    public Object getValue(AbstractGlobal global) {
        return global.getConfigItemCount();
    }
    public void setValue(Object value, AbstractGlobal global) {
        throw new SupportException("_global.count，属性不支持写操作。");
    }
}