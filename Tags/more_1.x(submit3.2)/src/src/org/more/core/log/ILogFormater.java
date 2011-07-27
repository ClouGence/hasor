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
 * 日志数据格式化接口
 * @version 2009-5-13
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ILogFormater {
    /**
     * 格式化日志字符串。
     * @param level 此次格式化的日志信息是哪个级别上的日志。
     * @param msg 日志消息。
     * @return 返回格式化之后的日志信息。
     */
    public String getFormatMessage(String level, String msg);
}