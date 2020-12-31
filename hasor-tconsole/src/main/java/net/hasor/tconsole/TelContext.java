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
package net.hasor.tconsole;
import net.hasor.core.AppContext;

import java.util.List;

/**
 * tConsol 为您提供 telnet 下和应用程序交互的能力。
 * @version : 2016年09月20日
 * @author 赵永春 (zyc@hasor.net)
 */
public interface TelContext {
    /** 查找命令 */
    public TelExecutor findCommand(String cmdName);

    /** 获取所有命令 */
    public List<String> getCommandNames();

    /** 是否是 Host 模式 */
    public boolean isHost();

    public AppContext getAppContext();
}