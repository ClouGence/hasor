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
package org.more.log;
/**
 * 日志对象重复定义
 * Date : 2009-5-13
 * @author 赵永春
 */
public class LogRepeatException extends LogException {
    private static final long serialVersionUID = 8629440806807181417L;
    // ===============================================================
    /** 日志对象重复定义 */
    public LogRepeatException() {
        super("日志对象重复定义");
    }
    /** 日志对象重复定义，错误信息由参数给出 */
    public LogRepeatException(String msg) {
        super(msg);
    }
    /** 日志对象重复定义，错误信息是承接上一个异常而来 */
    public LogRepeatException(Exception e) {
        super(e);
    }
}
