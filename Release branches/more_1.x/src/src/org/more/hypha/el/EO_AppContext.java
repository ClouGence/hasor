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
import org.more.DoesSupportException;
import org.more.hypha.ApplicationContext;
import org.more.hypha.ELObject;
/**
 * EL中对应为{@link ApplicationContext $context}EL对象。
 * Date : 2011-4-11
 * @author 赵永春 (zyc@byshell.org)
 */
public class EO_AppContext implements ELObject {
    private ApplicationContext context = null;
    public void init(ApplicationContext context) {
        this.context = context;
    };
    public boolean isReadOnly() {
        return true;
    };
    public void setValue(Object value) {
        throw new DoesSupportException("不允许替换context对象。");
    };
    public Object getValue() {
        return this.context;
    };
};