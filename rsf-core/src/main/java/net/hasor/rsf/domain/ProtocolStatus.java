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
package net.hasor.rsf.domain;
/**
 * Server:Unknown、Message、MovedPermanently、Unauthorized
 * Client:
 * @version : 2014年9月20日
 * @author 赵永春(zyc@hasor.net)
 */
public enum ProtocolStatus {
    /**未定义*/
    Unknown(0, ""), //
    //-----------------------------------------------------
    /**内容正确返回。*/
    OK(200, ""), //
    /**已经接受请求处理正在进行中。*/
    Accepted(202, ""), //
    //    //-----------------------------------------------------
    //    /**试图调用受保护的服务。*/
    //    Unauthorized(401, ""), //
    /**服务资源不可用。*/
    Forbidden(403, ""), //
    //    /**找不到服务*/
    //    NotFound(404, ""), //
    /**调用服务超时*/
    RequestTimeout(408, "超出客户端允许时间。"), //
    //-----------------------------------------------------
    /**调用服务执行出错，通常是遭到异常抛出。*/
    InvokeError(500, ""), //
    /**不支持的协议版本。*/
    ProtocolUnknown(505, "不支持的协议版本。"), //
    /**协议编码解码错误。*/
    ProtocolError(505, "协议编码解码错误。"), //
    /**序列化异常。*/
    SerializeError(511, ""), //
    //    /**序列化类型未定义。*/
    //    SerializeForbidden(510, ""), //
    //    /**buildResponse错误。*/
    //    BuildResponse(504, ""), //
    //    /***/
    //    BuildSocketBlock(505, ""), //
    //    /***/
    //    ResponseNullError(506, ""), //
    //-----------------------------------------------------
    /**客户端错误。*/
    ClientError(600, "");
    //
    //
    private short type;
    private String desc;
    ProtocolStatus(int type, String desc) {
        if (0 <= type && type < Short.MAX_VALUE) {
            this.type = (short) type;
            this.desc = desc;
        } else {
            throw new IndexOutOfBoundsException("out of range.");
        }
    }
    public boolean equalsValue(short resStatus) {
        return type == resStatus;
    }
    public short getType() {
        return type;
    }
    public String getDesc() {
        return desc;
    }
    public static ProtocolStatus valueOf(short status) {
        for (ProtocolStatus protocol : ProtocolStatus.values()) {
            if (protocol.type == status) {
                return protocol;
            }
        }
        return ProtocolStatus.Unknown;
    }
    @Override
    public String toString() {
        return "Enum[type = " + this.type + " , desc = " + this.desc + "]";
    }
}