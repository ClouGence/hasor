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
package net.hasor.rsf.utils;
import java.lang.reflect.Array;
import java.util.Map;
import net.hasor.rsf.constants.ProtocolStatus;
import net.hasor.rsf.constants.RSFConstants;
import net.hasor.rsf.remoting.transport.protocol.block.RequestSocketBlock;
import net.hasor.rsf.remoting.transport.protocol.block.ResponseSocketBlock;
import net.hasor.rsf.remoting.transport.protocol.codec.Protocol;
import net.hasor.rsf.remoting.transport.protocol.codec.RpcRequestProtocol;
import net.hasor.rsf.remoting.transport.protocol.codec.RpcResponseProtocol;
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
        char atChar = tType.charAt(0);
        if (/*   */'I' == atChar) {
            return int.class;
        } else if ('B' == atChar) {
            return byte.class;
        } else if ('C' == atChar) {
            return char.class;
        } else if ('D' == atChar) {
            return double.class;
        } else if ('F' == atChar) {
            return float.class;
        } else if ('J' == atChar) {
            return long.class;
        } else if ('S' == atChar) {
            return short.class;
        } else if ('Z' == atChar) {
            return boolean.class;
        } else if ('V' == atChar) {
            return void.class;
        } else if (atChar == '[') {
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
            Class<?> cache = loadClassCache.get(tType);
            if (cache == null) {
                cache = loader.loadClass(tType);
                loadClassCache.put(tType, cache);
            }
            return cache;
        }
    }
    /**将某一个类型转为asm形式的表述， int 转为 I，String转为 Ljava/lang/String。*/
    public static String toAsmType(final Class<?> classType) {
        if (classType == int.class) {
            return "I";
        } else if (classType == byte.class) {
            return "B";
        } else if (classType == char.class) {
            return "C";
        } else if (classType == double.class) {
            return "D";
        } else if (classType == float.class) {
            return "F";
        } else if (classType == long.class) {
            return "J";
        } else if (classType == short.class) {
            return "S";
        } else if (classType == boolean.class) {
            return "Z";
        } else if (classType == void.class) {
            return "V";
        } else if (classType.isArray() == true) {
            return "[" + toAsmType(classType.getComponentType());
        } else {
            return classType.getName();
        }
    }
    private static Map<String, Class<?>> loadClassCache = new java.util.concurrent.ConcurrentHashMap<String, Class<?>>();
}