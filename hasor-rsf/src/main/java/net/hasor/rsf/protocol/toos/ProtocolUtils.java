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
package net.hasor.rsf.protocol.toos;
import java.lang.reflect.Array;
import net.hasor.rsf.general.ProtocolStatus;
import net.hasor.rsf.general.RSFConstants;
import net.hasor.rsf.protocol.block.RequestSocketBlock;
import net.hasor.rsf.protocol.block.ResponseSocketBlock;
import net.hasor.rsf.protocol.codec.Protocol;
import net.hasor.rsf.protocol.codec.RpcRequestProtocol;
import net.hasor.rsf.protocol.codec.RpcResponseProtocol;
import org.more.util.StringUtils;
/**
 * Protocol Interface,for custom network protocol
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
public class ProtocolUtils {
    private static Protocol<RequestSocketBlock>[]  reqProtocolPool = new Protocol[63];
    private static Protocol<ResponseSocketBlock>[] resProtocolPool = new Protocol[63];
    //
    static {
        reqProtocolPool[0] = new RpcRequestProtocol();
        resProtocolPool[0] = new RpcResponseProtocol();
    }
    /**获取协议版本。*/
    private static byte version(byte version) {
        return (byte) (0x3F & version);
    }
    //
    public static Protocol<RequestSocketBlock> requestProtocol(byte ver) {
        return reqProtocolPool[version(ver) - 1];
    }
    public static Protocol<ResponseSocketBlock> responseProtocol(byte ver) {
        return resProtocolPool[version(ver) - 1];
    }
    //
    //
    //
    /**判断 response 是否为一个ACK包。*/
    public static boolean isACK(ResponseSocketBlock socketMessage) {
        return socketMessage.getStatus() == ProtocolStatus.Accepted;
    }
    /**是否为Request消息。*/
    public static boolean isRequest(byte version) {
        return (0xC1 | version) == version;
    }
    /**是否为Response消息。*/
    public static boolean isResponse(byte version) {
        return (0x81 | version) == version;
    }
    /**获取协议版本。*/
    public static byte getVersion(byte version) {
        return (byte) (0x3F & version);
    }
    /**生成 request 的 version 信息。*/
    public static byte finalVersionForRequest(byte version) {
        return (byte) (RSFConstants.RSF_Request | version);
    }
    /**生成 response 的 version 信息。*/
    public static byte finalVersionForResponse(byte version) {
        return (byte) (RSFConstants.RSF_Response | version);
    }
    /**使用指定的ClassLoader将一个asm类型转化为Class对象。*/
    public static Class<?> toJavaType(final String tType, final ClassLoader loader) throws ClassNotFoundException {
        if (/*   */tType.equals("I") == true || StringUtils.equalsIgnoreCase(tType, "int") == true) {
            return int.class;
        } else if (tType.equals("B") == true || StringUtils.equalsIgnoreCase(tType, "byte") == true) {
            return byte.class;
        } else if (tType.equals("C") == true || StringUtils.equalsIgnoreCase(tType, "char") == true) {
            return char.class;
        } else if (tType.equals("D") == true || StringUtils.equalsIgnoreCase(tType, "double") == true) {
            return double.class;
        } else if (tType.equals("F") == true || StringUtils.equalsIgnoreCase(tType, "float") == true) {
            return float.class;
        } else if (tType.equals("J") == true || StringUtils.equalsIgnoreCase(tType, "long") == true) {
            return long.class;
        } else if (tType.equals("S") == true || StringUtils.equalsIgnoreCase(tType, "short") == true) {
            return short.class;
        } else if (tType.equals("Z") == true || StringUtils.equalsIgnoreCase(tType, "bit") == true || StringUtils.equalsIgnoreCase(tType, "boolean") == true) {
            return boolean.class;
        } else if (tType.equals("V") == true || StringUtils.equalsIgnoreCase(tType, "void") == true) {
            return void.class;
        } else if (tType.charAt(0) == '[') {
            int length = 0;
            while (true) {
                if (tType.charAt(length) != '[') {
                    break;
                }
                length++;
            }
            String arrayType = tType.substring(length, tType.length());
            Class<?> returnType = toJavaType(arrayType, loader);
            for (int i = 0; i < length; i++) {
                Object obj = Array.newInstance(returnType, length);
                returnType = obj.getClass();
            }
            return returnType;
        } else {
            return loader.loadClass(tType);
        }
    }
}