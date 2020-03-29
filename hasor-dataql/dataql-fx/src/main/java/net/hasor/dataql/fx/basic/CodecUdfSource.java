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
package net.hasor.dataql.fx.basic;
import net.hasor.dataql.UdfSourceAssembly;
import net.hasor.utils.CommonCodeUtils;
import net.hasor.utils.ExceptionUtils;

import javax.inject.Singleton;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * 编解码函数库。函数库引入 <code>import 'net.hasor.dataql.fx.basic.CodecUdfSource' as codec;</code>
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2019-12-12
 */
@Singleton
public class CodecUdfSource implements UdfSourceAssembly {
    /** 计算字节数组的MD5 */
    public static String byteMD5(byte[] content) {
        if (content == null) {
            return null;
        }
        try {
            return CommonCodeUtils.MD5.getMD5(content);
        } catch (Exception e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }

    /** 计算字符串的MD5 */
    public static String stringMD5(String content) {
        if (content == null) {
            return null;
        }
        try {
            return CommonCodeUtils.MD5.getMD5(content);
        } catch (Exception e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }

    /** 将字符串进行 Base64编码。 */
    public static String stringEncodeBase64(String content) {
        try {
            return CommonCodeUtils.Base64.base64Encode(content);
        } catch (Exception e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }

    /** 对字节数组 Base64 进行编码 */
    public static String byteEncodeBase64(byte[] content) {
        if (content == null || content.length == 0) {
            return null;
        }
        try {
            return CommonCodeUtils.Base64.base64EncodeFoArray(content);
        } catch (Exception e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }

    /** 将 Base64 编码数据，解码为字符串 */
    public static String stringDecodeBase64(String content) {
        try {
            return CommonCodeUtils.Base64.base64Decode(content);
        } catch (Exception e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }

    /** 将 Base64 编码数据，解码为字节数组 */
    public static byte[] byteDecodeBase64(String content) {
        if (content == null || content.equals("")) {
            return new byte[0];
        }
        try {
            return CommonCodeUtils.Base64.base64DecodeToArray(content);
        } catch (Exception e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }

    /** 将二进制数据转换为 16进制字符串 */
    public static String byteToHex(byte[] content) {
        if (content == null || content.length == 0) {
            return null;
        }
        return CommonCodeUtils.HexConversion.byte2HexStr(content);
    }

    /** 将二进制数据转换为 16进制字符串 */
    public static byte[] hexToByte(String content) {
        if (content == null || content.equals("")) {
            return new byte[0];
        }
        return CommonCodeUtils.HexConversion.hexStr2Bytes(content);
    }

    /** 二进制数据转换为字符串 */
    public static String byteToString(byte[] content, String charset) {
        if (content == null || content.length == 0) {
            return null;
        }
        return new String(content, Charset.forName(charset));
    }

    /** 字符串转换为二进制数据 */
    public static byte[] stringToByte(String content, String charset) throws UnsupportedEncodingException {
        if (content == null || content.equals("")) {
            return new byte[0];
        }
        return content.getBytes(charset);
    }
}