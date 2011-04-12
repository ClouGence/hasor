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
package org.more.hypha;
/**
 * EL方法，可以实现该接口向hypha中注册一个方法，并且通过el表达式进行调用。
 * Date : 2011-4-11
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ELMethod {
    /**初始化{@link ELMethod}方法对象。*/
    public void init(ApplicationContext context);
    /**重置方法状态。*/
    public void reset();
    /**调用目标方法，方法参数是一个可变的参数。表示在调用方法时传递的参数值。*/
    public Object invoke(Object... objects);
};