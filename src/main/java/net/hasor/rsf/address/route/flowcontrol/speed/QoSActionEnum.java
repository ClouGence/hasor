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
package net.hasor.rsf.address.route.flowcontrol.speed;
/**
 * 流控级别，可选的级别有（服务级、方法级、地址级）
 * @version : 2015年4月6日
 * @author 赵永春(zyc@hasor.net)
 */
public enum QoSActionEnum {
    /**限制接口所有方法的总调用速率。*/
    Service, /**限制接口某一个方法的调用速率。*/
    Method, /**限制对某一个远程服务机器的调用速率。*/
    Address
}