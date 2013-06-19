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
 * 日志源接口。
 * @version 2009-5-13
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ILog {
    //    /** 调试级别 */
    //    public static final String LogLevel_Debug   = "Debug";
    //    /** 信息级别 */
    //    public static final String LogLevel_Info    = "Info";
    //    /** 错误级别 */
    //    public static final String LogLevel_Error   = "Error";
    //    /** 警告级别 */
    //    public static final String LogLevel_Warning = "Warning";
    /**
     * 以调试级别输出日志。
     * @param msg 输出的日志。
     */
    public void debug(String msg, Object... infoObjects);
    /**
     * 以信息级别输出日志。
     * @param msg 输出的日志。
     */
    public void info(String msg, Object... infoObjects);
    /**
     * 以错误级别输出日志。
     * @param msg 输出的日志。
     */
    public void error(String msg, Object... infoObjects);
    /**
     * 以警告级别输出日志。
     * @param msg 输出的日志。
     */
    public void warning(String msg, Object... infoObjects);
}