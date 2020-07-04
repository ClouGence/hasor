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
package net.hasor.dataql.fx.encryt;
import net.hasor.dataql.UdfSourceAssembly;
import net.hasor.utils.ArrayUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Singleton;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

/**
 * 签名/编码函数库。函数库引入 <code>import 'net.hasor.dataql.fx.encryt.CodecUdfSource' as codec;</code>
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-31
 */
@Singleton
public class CodecUdfSource implements UdfSourceAssembly {
    /** 对字符串组进行 Base64编码 */
    public static String encodeString(String content) {
        if (content == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(content.getBytes());
    }

    /** 对字节数组进行 Base64编码 */
    public static String encodeBytes(List<Byte> content) {
        if (content == null) {
            return null;
        }
        if (content.size() == 0) {
            return "";
        }
        byte[] bytes = ArrayUtils.toPrimitive(content.toArray(new Byte[0]));
        return Base64.getEncoder().encodeToString(bytes);
    }

    /** 将 Base64 解码为字符串 */
    public static String decodeString(String content) {
        if (content == null) {
            return null;
        }
        return new String(Base64.getDecoder().decode(content));
    }

    /** 将 Base64 解码为字节数组 */
    public static byte[] decodeBytes(String content) {
        if (content == null) {
            return null;
        }
        return Base64.getDecoder().decode(content);
    }

    /** 对字符串进行 URL 编码 */
    public static String urlEncode(String content) throws UnsupportedEncodingException {
        return urlEncodeBy(content, "UTF-8");
    }

    /** 对字符串进行 URL 编码 */
    public static String urlEncodeBy(String content, String enc) throws UnsupportedEncodingException {
        if (content == null) {
            return null;
        }
        return URLEncoder.encode(content, enc);
    }

    /** 对字符串进行 URL 解码 */
    public static String urlDecode(String content) throws UnsupportedEncodingException {
        return urlDecodeBy(content, "UTF-8");
    }

    /** 对字符串进行 URL 解码 */
    public static String urlDecodeBy(String content, String enc) throws UnsupportedEncodingException {
        if (content == null) {
            return null;
        }
        return URLDecoder.decode(content, enc);
    }

    /** 摘要算法，使用指定方式进行摘要计算 */
    public static byte[] digestBytes(String digestString, List<Byte> content) throws NoSuchAlgorithmException {
        DigestType digestType = DigestType.formString(digestString);
        if (digestType == null) {
            throw new NoSuchAlgorithmException("'" + digestString + "' digestType not found.");
        }
        if (content == null) {
            return null;
        }
        //
        MessageDigest mdTemp = MessageDigest.getInstance(digestType.getDigestDesc());
        Byte[] bytes = content.toArray(new Byte[0]);
        mdTemp.update(ArrayUtils.toPrimitive(bytes));
        return mdTemp.digest();
    }

    /** 摘要算法，使用指定方式进行摘要计算 */
    public static byte[] digestString(String digestString, String content) throws NoSuchAlgorithmException {
        DigestType digestType = DigestType.formString(digestString);
        if (digestType == null) {
            throw new NoSuchAlgorithmException("'" + digestString + "' digestType not found.");
        }
        if (content == null) {
            return null;
        }
        //
        MessageDigest mdTemp = MessageDigest.getInstance(digestType.getDigestDesc());
        mdTemp.update(content.getBytes());
        return mdTemp.digest();
    }

    /** 指定摘要算法，对字节数组进行Hmac签名计算 */
    public static String hmacBytes(String hmacTypeString, String signKey, List<Byte> content) throws NoSuchAlgorithmException, InvalidKeyException {
        HmacType hmacType = HmacType.formString(hmacTypeString);
        if (hmacType == null) {
            throw new NoSuchAlgorithmException("'" + hmacTypeString + "' hmacType not found.");
        }
        if (content == null) {
            return null;
        }
        //
        Mac mac = Mac.getInstance(hmacType.getHmacType());//Mac 算法对象
        mac.init(new SecretKeySpec(signKey.getBytes(), HmacType.HmacMD5.getHmacType()));//初始化 Mac 对象
        //
        Byte[] bytes = content.toArray(new Byte[0]);
        byte[] rawHmac = mac.doFinal(ArrayUtils.toPrimitive(bytes));
        return Base64.getEncoder().encodeToString(rawHmac);
    }

    /** 指定摘要算法，对字符串组进行Hmac签名计算 */
    public static String hmacString(String hmacTypeString, String signKey, String content) throws NoSuchAlgorithmException, InvalidKeyException {
        HmacType hmacType = HmacType.formString(hmacTypeString);
        if (hmacType == null) {
            throw new NoSuchAlgorithmException("'" + hmacTypeString + "' hmacType not found.");
        }
        if (content == null) {
            return null;
        }
        Mac mac = Mac.getInstance(hmacType.getHmacType());//Mac 算法对象
        mac.init(new SecretKeySpec(signKey.getBytes(), HmacType.HmacMD5.getHmacType()));//初始化 Mac 对象
        //
        byte[] rawHmac = mac.doFinal(content.getBytes());
        return Base64.getEncoder().encodeToString(rawHmac);
    }
}