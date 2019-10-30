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
package net.hasor.tconsole.spi;
import net.hasor.tconsole.TelCommandOption;

/**
 * 在某一个命令开始执行之前触发，参数是准备要执行的那个命令。可以通过 {@link TelCommandOption#cancel()} 方法来取消这一次调用。
 * @version : 2019年10月30日
 * @author 赵永春 (zyc@hasor.net)
 */
@FunctionalInterface
public interface TelBeforeExecutorListener extends java.util.EventListener {
    public void beforeExecCommand(TelCommandOption telCommand);
}