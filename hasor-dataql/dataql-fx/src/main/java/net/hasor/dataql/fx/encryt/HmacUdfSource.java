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

import javax.inject.Singleton;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * 签名函数库，函数库引入 <code>import 'net.hasor.dataql.fx.encryt.SignUdfSource' as hmac;</code>
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2019-12-12
 */
@Singleton
@Deprecated
public class HmacUdfSource implements UdfSourceAssembly {
    /** 使用 HMAC-MD5 签名方法对 content 进行签名 */
    public static String hmacMD5_string(String signKey, String content) throws NoSuchAlgorithmException, InvalidKeyException {
        return CodecUdfSource.hmacString(HmacType.HmacMD5.getHmacType(), signKey, content);
    }

    /** 使用 HMAC-MD5 签名方法对 content 进行签名 */
    public static String hmacMD5_bytes(String signKey, List<Byte> content) throws NoSuchAlgorithmException, InvalidKeyException {
        return CodecUdfSource.hmacBytes(HmacType.HmacMD5.getHmacType(), signKey, content);
    }

    /** 使用 HMAC-SHA1 签名方法对 content 进行签名 */
    public static String hmacSHA1_string(String signKey, String content) throws NoSuchAlgorithmException, InvalidKeyException {
        return CodecUdfSource.hmacString(HmacType.HmacSHA1.getHmacType(), signKey, content);
    }

    /** 使用 HMAC-SHA1 签名方法对 content 进行签名 */
    public static String hmacSHA1_bytes(String signKey, List<Byte> content) throws NoSuchAlgorithmException, InvalidKeyException {
        return CodecUdfSource.hmacBytes(HmacType.HmacSHA1.getHmacType(), signKey, content);
    }

    /** 使用 HMAC-SHA256 签名方法对 content 进行签名 */
    public static String hmacSHA256_string(String signKey, String content) throws NoSuchAlgorithmException, InvalidKeyException {
        return CodecUdfSource.hmacString(HmacType.HmacSHA256.getHmacType(), signKey, content);
    }

    /** 使用 HMAC-SHA256 签名方法对 content 进行签名 */
    public static String hmacSHA256_bytes(String signKey, List<Byte> content) throws NoSuchAlgorithmException, InvalidKeyException {
        return CodecUdfSource.hmacBytes(HmacType.HmacSHA256.getHmacType(), signKey, content);
    }

    /** 使用 HMAC-SHA512 签名方法对 content 进行签名 */
    public static String hmacSHA512_string(String signKey, String content) throws NoSuchAlgorithmException, InvalidKeyException {
        return CodecUdfSource.hmacString(HmacType.HmacSHA512.getHmacType(), signKey, content);
    }

    /** 使用 HMAC-SHA512 签名方法对 content 进行签名 */
    public static String hmacSHA512_bytes(String signKey, List<Byte> content) throws NoSuchAlgorithmException, InvalidKeyException {
        return CodecUdfSource.hmacBytes(HmacType.HmacSHA512.getHmacType(), signKey, content);
    }
}