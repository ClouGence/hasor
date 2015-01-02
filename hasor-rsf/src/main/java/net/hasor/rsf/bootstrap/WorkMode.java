/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.rsf.bootstrap;
/**
 * 工作模式
 * @version : 2014年12月22日
 * @author 赵永春(zyc@hasor.net)
 */
public enum WorkMode {
    /**RSF 仅作为客户端启动，本地任何注册的服务均不会对外发布。*/
    Customer,
    /**正常模式*/
    None,
}