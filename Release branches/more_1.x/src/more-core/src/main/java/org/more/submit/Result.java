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
package org.more.submit;
/**
 * 结果处理标记对象。
 * @version : 2011-7-27
 * @author 赵永春 (zyc@byshell.org)
 */
public interface Result<T> {
    /**获取action执行完毕返回的对象。*/
    public T getReturnValue();
    /**设置当action执行完毕返回的对象。*/
    public void setReturnValue(T returnValue);
    public String getName();
}