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
 * Telnet指令执行器。
 * @version : 20169年09月20日
 * @author 赵永春 (zyc@hasor.net)
 */
public interface TelOptions {
    public static final String CLOSE_SESSION    = "TEL_CLOSE"; // 命令结束之后立刻关闭 session
    public static final String SILENT           = "TEL_SILENT";         // 静默：只输出命令本身的返回信息
    public static final String COST             = "TEL_COST";         // 输出每条命令的成本时间
    public static final String MAX_EXECUTOR_NUM = "TEL_MAX_EXECUTOR_NUM";// 当会话中执行命令数量达到设定值之后立刻关闭 session
}