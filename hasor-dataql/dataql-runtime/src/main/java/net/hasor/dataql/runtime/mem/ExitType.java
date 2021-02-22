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
package net.hasor.dataql.runtime.mem;
/**
 * 退出模式
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2019-12-11
 */
public enum ExitType {
    /** 正常退出，后续指令序列继续执行 */
    Return,
    /** 非正常退出，终止后续指令序列执行并抛出异常 */
    Throw,
    /** 中断正常执行，并退出整个执行序列 */
    Exit,
}
