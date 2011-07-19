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
package org.more.core.log;
/**
 * 日志输出接口
 * @version 2009-5-13
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ILogWrite {
    /**
     * 输出日志。
     * @param msg 日志消息。
     * @return 返回日志输出是否成功。在默认组建中日志输出是否成功不会影响任何其他功能，
     *         在扩展日志系统时可以使用该返回值来确定日志是否输出到特定位置。
     */
    public boolean writeLog(String msg);
}