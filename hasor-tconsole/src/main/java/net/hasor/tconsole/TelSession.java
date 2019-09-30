/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.tconsole;
/**
 * 控制台会话
 * @version : 20169年09月20日
 * @author 赵永春 (zyc@hasor.net)
 */
public interface TelSession extends TelAttribute {
    /** 获取 SessionID */
    public String getSessionID();

    /** 获取当前计数（命令无论成功或失败当执行之后计数器就会+1） */
    public int curentCounter();

    /** 获取Telnet工具的上下文 */
    public TelContext getTelContext();

    /** 立即关闭Telnet连接 */
    public default void close() {
        close(0, false);
    }

    /** 延迟 afterSeconds 秒之后，关闭Telnet连接；显示倒计时。 */
    public default void close(int afterSeconds) {
        close(afterSeconds, true);
    }

    /** 延迟 afterSeconds 秒之后，关闭Telnet连接 */
    public void close(int afterSeconds, boolean countdown);

    /** 判断会话是否已经被关闭 */
    public boolean isClose();

    /** 输出状态（带有换行）*/
    public default void writeMessageLine(String message) {
        this.writeMessage(message + "\n");
    }

    /**输出状态（不带换行）。*/
    public void writeMessage(String message);
}