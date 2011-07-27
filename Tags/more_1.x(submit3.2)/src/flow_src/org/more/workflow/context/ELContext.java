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
package org.more.workflow.context;
import org.more.core.ognl.OgnlContext;
/**
 * 
 * Date : 2010-5-17
 * @author Administrator
 */
public class ELContext {
    private Object      thisValue   = null;
    private OgnlContext ognlContext = new OgnlContext();
    public OgnlContext getOgnlContext() {
        this.ognlContext.put("this", this.thisValue);
        return this.ognlContext;
    };
    /**将一个对象加入到计算表达式中，如果这个对象名在ELContext中重复则由putLocal方法输出的对象优先级大于ELContext。*/
    public void putLocalThis(Object value) {
        this.thisValue = value;
    };
    public Object getLocalThis() {
        return thisValue;
    }
};