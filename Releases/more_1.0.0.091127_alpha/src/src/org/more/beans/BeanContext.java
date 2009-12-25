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
package org.more.beans;
import org.more.util.attribute.IAttribute;
/**
 * 
 * Date : 2009-11-3
 * @author 赵永春
 */
public interface BeanContext extends BeanFactory, IAttribute {
    /**
     * 获取Context的名称。
     * @return 返回Context的名称。
     */
    public String getName();
    /**
     * 获取Context所处的上下文。
     * @return 返回Context所处的上下文。
     */
    public Object getContext();
    /**
     * 获取Context的父级BeanContext，如果当前BeanContext就是顶层该方法返回null。
     * @return 返回Context的父级BeanContext，如果当前BeanContext就是顶层该方法返回null。
     */
    public BeanContext getParent();
}
