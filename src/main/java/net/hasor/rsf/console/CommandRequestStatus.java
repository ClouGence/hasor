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
package net.hasor.rsf.console;
/**
 *
 * @version : 2016年4月3日
 * @author 赵永春(zyc@hasor.net)
 */
enum CommandRequestStatus {
    /**命令准备，用于多行命令在执行前接受内容。（单行命令不支持此状态）*/
    Prepare, /**命令就绪，多行命令在Prepare模式下输入一个空行即可进入状态，如果命令在该状态下输入了新的内容会重新跳回到Prepare状态。（单行命令默认值）*/
    Ready, /**命令即将执行，等待任务执行调度系统调度。*/
    StandBy, /**命令运行中，命令在Ready状态下输入一个空行即可进入该状态。*/
    Running, /**命令执行完毕*/
    Complete
}