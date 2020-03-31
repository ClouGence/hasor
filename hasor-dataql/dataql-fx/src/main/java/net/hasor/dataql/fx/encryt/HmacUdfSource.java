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
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

/**
 * 签名函数库，函数库引入 <code>import 'net.hasor.dataql.fx.encryt.SignUdfSource' as hmac;</code>
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2019-12-12
 */
@Singleton
public class HmacUdfSource implements UdfSourceAssembly {
    private enum SignType {
        HmacMD5("HmacMD5"),         //
        HmacSHA1("HmacSHA1"),       //
        HmacSHA256("HmacSHA256"),   //
        HmacSHA512("HmacSHA512");   //
        private String signType;

        private SignType(String signType) {
            this.signType = signType;
        }

        public String getSignType() {
            return signType;
        }
    }

    /** 使用 HMAC-MD5 签名方法对 content 进行签名 */
    public static String hmacMD5_string(String signKey, String content) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(SignType.HmacMD5.getSignType());//Mac 算法对象
        mac.init(new SecretKeySpec(signKey.getBytes(), SignType.HmacMD5.getSignType()));//初始化 Mac 对象
        byte[] rawHmac = mac.doFinal(content.getBytes());
        return Base64.getEncoder().encodeToString(rawHmac);
    }

    /** 使用 HMAC-MD5 签名方法对 content 进行签名 */
    public static String hmacMD5_bytes(String signKey, List<Byte> content) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(SignType.HmacMD5.getSignType());//Mac 算法对象
        mac.init(new SecretKeySpec(signKey.getBytes(), SignType.HmacMD5.getSignType()));//初始化 Mac 对象
        Byte[] bytes = content.toArray(new Byte[0]);
        byte[] rawHmac = mac.doFinal(ArrayUtils.toPrimitive(bytes));
        return Base64.getEncoder().encodeToString(rawHmac);
    }

    /** 使用 HMAC-SHA1 签名方法对 content 进行签名 */
    public static String hmacSHA1_string(String signKey, String content) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(SignType.HmacSHA1.getSignType());//Mac 算法对象
        mac.init(new SecretKeySpec(signKey.getBytes(), SignType.HmacSHA1.getSignType()));//初始化 Mac 对象
        byte[] rawHmac = mac.doFinal(content.getBytes());
        return Base64.getEncoder().encodeToString(rawHmac);
    }

    /** 使用 HMAC-SHA1 签名方法对 content 进行签名 */
    public static String hmacSHA1_bytes(String signKey, List<Byte> content) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(SignType.HmacSHA1.getSignType());//Mac 算法对象
        mac.init(new SecretKeySpec(signKey.getBytes(), SignType.HmacSHA1.getSignType()));//初始化 Mac 对象
        Byte[] bytes = content.toArray(new Byte[0]);
        byte[] rawHmac = mac.doFinal(ArrayUtils.toPrimitive(bytes));
        return Base64.getEncoder().encodeToString(rawHmac);
    }

    /** 使用 HMAC-SHA256 签名方法对 content 进行签名 */
    public static String hmacSHA256_string(String signKey, String content) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(SignType.HmacSHA256.getSignType());//Mac 算法对象
        mac.init(new SecretKeySpec(signKey.getBytes(), SignType.HmacSHA256.getSignType()));//初始化 Mac 对象
        byte[] rawHmac = mac.doFinal(content.getBytes());
        return Base64.getEncoder().encodeToString(rawHmac);
    }

    /** 使用 HMAC-SHA256 签名方法对 content 进行签名 */
    public static String hmacSHA256_bytes(String signKey, List<Byte> content) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(SignType.HmacSHA256.getSignType());//Mac 算法对象
        mac.init(new SecretKeySpec(signKey.getBytes(), SignType.HmacSHA256.getSignType()));//初始化 Mac 对象
        Byte[] bytes = content.toArray(new Byte[0]);
        byte[] rawHmac = mac.doFinal(ArrayUtils.toPrimitive(bytes));
        return Base64.getEncoder().encodeToString(rawHmac);
    }

    /** 使用 HMAC-SHA512 签名方法对 content 进行签名 */
    public static String hmacSHA512_string(String signKey, String content) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(SignType.HmacSHA512.getSignType());//Mac 算法对象
        mac.init(new SecretKeySpec(signKey.getBytes(), SignType.HmacSHA512.getSignType()));//初始化 Mac 对象
        byte[] rawHmac = mac.doFinal(content.getBytes());
        return Base64.getEncoder().encodeToString(rawHmac);
    }

    /** 使用 HMAC-SHA512 签名方法对 content 进行签名 */
    public static String hmacSHA512_bytes(String signKey, List<Byte> content) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(SignType.HmacSHA512.getSignType());//Mac 算法对象
        mac.init(new SecretKeySpec(signKey.getBytes(), SignType.HmacSHA512.getSignType()));//初始化 Mac 对象
        Byte[] bytes = content.toArray(new Byte[0]);
        byte[] rawHmac = mac.doFinal(ArrayUtils.toPrimitive(bytes));
        return Base64.getEncoder().encodeToString(rawHmac);
    }
}