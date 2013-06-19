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
package org.more.hypha.el.assembler;
import org.more.core.error.DefineException;
import org.more.hypha.ApplicationContext;
import org.more.hypha.ELObject;
import org.more.util.attribute.IAttribute;
/**
 * 可以在el中访问getBean参数的EL对象，不支持赋值操作。
 * Date : 2011-4-11
 * @author 赵永春 (zyc@byshell.org)
 */
public class EO_Params implements ELObject {
    private final String KEY   = "GETBEAN_PARAM";
    private IAttribute   flash = null;
    //---------------------------------------------------------------
    public void init(ApplicationContext context, IAttribute flash) {
        this.flash = flash;
    }
    public boolean isReadOnly() {
        return false;
    }
    public void setValue(Object value) {
        throw new DefineException("不支持的赋值操作。");
    }
    public Object getValue() {
        return this.flash.getAttribute(KEY);
    }
}