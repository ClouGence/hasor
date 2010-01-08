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
package org.more.core.task;
/**
 * 任务处理器的运行状态。
 * @version 2009-5-16
 * @author 赵永春 (zyc@byshell.org)
 */
public enum TaskState {
    /** 新的任务，没有进行过任何执行。 */
    New,
    /** 正在执行。 */
    Run,
    /** 已经执行完毕。 */
    RunEnd
}