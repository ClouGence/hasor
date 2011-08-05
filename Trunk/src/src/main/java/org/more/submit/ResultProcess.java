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
import org.more.submit.impl.DefaultActionStack;
/**
 * 当action调用结束时会执行该结果处理器进行后续处理。
 * @version : 2011-7-25
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ResultProcess {
    /**执行回调处理 */
    public Object invoke(DefaultActionStack onStack, Result res) throws Throwable;
    /**添加配置参数*/
    public void addParam(String key, String value);
};