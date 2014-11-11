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
package net.hasor.rsf.general;
/**
 * ChooseOther、Accepted
 * @version : 2014年9月20日
 * @author 赵永春(zyc@hasor.net)
 */
public enum ProtocolStatus {
    //-----------------------------------------------------
    /**未定义*/
    Unknown(0),
    /**内容正确返回。*/
    OK(200),
    /**已经接受请求处理正在进行中。*/
    Accepted(202),
    //-----------------------------------------------------
    /**服务重定向。*/
    MovedPermanently(301),
    /**服务器要求客户端选择其它服务提供者处理该请求。*/
    ChooseOther(302),
    //-----------------------------------------------------
    /**试图调用受保护的服务。*/
    Unauthorized(401),
    /**服务资源不可用。*/
    Forbidden(403),
    /**找不到服务*/
    NotFound(404),
    /**调用服务超时*/
    RequestTimeout(408),
    //-----------------------------------------------------
    /**调用服务执行出错*/
    InternalServerError(500),
    /**协议错误。*/
    ProtocolError(1000);
    //-----------------------------------------------------
    //
    //
    private short value = 0;
    private ProtocolStatus(int value) {
        if (value >= 0xFFFF) {
            throw new IndexOutOfBoundsException("value maximum is 0xFFFF.");
        }
        this.value = (short) value;
    }
    public short shortValue() {
        return this.value;
    }
    public String toString() {
        return String.format("%s(%s)", this.name(), this.value);
    }
    /**根据状态值获取状态枚举*/
    public static ProtocolStatus valueOf(short statusValue) {
        for (ProtocolStatus element : ProtocolStatus.values()) {
            if (element.shortValue() == statusValue)
                return element;
        }
        return Unknown;
    }
}