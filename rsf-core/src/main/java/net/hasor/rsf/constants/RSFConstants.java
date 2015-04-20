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
package net.hasor.rsf.constants;
import java.nio.charset.Charset;
/**
 * 各种常量
 * @version : 2014年9月20日
 * @author 赵永春(zyc@hasor.net)
 */
public interface RSFConstants {
    // RSF数据包   -（1000 0000）
    public static final byte    RSF_Packet          = (byte) (0x80);
    // RSF心跳包   -（1000 0000）
    public static final byte    RSF_Packet_Heart    = RSF_Packet | 0x00;
    // RSF请求包   -（1001 0000）
    public static final byte    RSF_Packet_Request  = RSF_Packet | 0x10;
    // RSF响应包   -（1010 0000）
    public static final byte    RSF_Packet_Response = RSF_Packet | 0x20;
    // RSF备用包   -（1011 0000）
    public static final byte    RSF_Packet_xxxx     = RSF_Packet | 0x30;
    //
    //
    //
    // 协议1.0
    public static final byte    Version_1           = (byte) (0x01);
    // RSF请求标记（1100 0000）
    public static final byte    RSF_Request         = Version_1 | RSF_Packet_Request;
    // RSF响应标记（1000 0000）
    public static final byte    RSF_Response        = Version_1 | RSF_Packet_Response;
    //
    public static final Charset DEFAULT_CHARSET     = Charset.forName("UTF-8");
}