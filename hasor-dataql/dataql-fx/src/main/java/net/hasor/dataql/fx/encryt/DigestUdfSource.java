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
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * 摘要算法库。函数库引入 <code>import 'net.hasor.dataql.fx.encryt.DigestUdfSource' as digest;</code>
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-31
 */
@Singleton
@Deprecated
public class DigestUdfSource implements UdfSourceAssembly {
    /** 使用指定方式进行摘要计算 */
    public static byte[] digestBytes(DigestType digestType, List<Byte> content) throws NoSuchAlgorithmException {
        return CodecUdfSource.digestBytes(digestType.name(), content);
    }

    /** 使用指定方式进行摘要计算 */
    public static byte[] digestString(DigestType digestType, String content) throws NoSuchAlgorithmException {
        return CodecUdfSource.digestString(digestType.name(), content);
    }

    /** 字节数组的 MD5 */
    public static byte[] md5_bytes(List<Byte> content) throws NoSuchAlgorithmException {
        return CodecUdfSource.digestBytes(DigestType.MD5.name(), content);
    }

    /** 字符串的 MD5 */
    public static byte[] md5_string(String content) throws NoSuchAlgorithmException {
        return CodecUdfSource.digestString(DigestType.MD5.name(), content);
    }

    /** 字节数组的 SHA */
    public static byte[] sha_bytes(List<Byte> content) throws NoSuchAlgorithmException {
        return CodecUdfSource.digestBytes(DigestType.SHA.name(), content);
    }

    /** 字符串的 SHA */
    public static byte[] sha_string(String content) throws NoSuchAlgorithmException {
        return CodecUdfSource.digestString(DigestType.SHA.name(), content);
    }

    /** 字节数组的 SHA1 */
    public static byte[] sha1_bytes(List<Byte> content) throws NoSuchAlgorithmException {
        return CodecUdfSource.digestBytes(DigestType.SHA1.name(), content);
    }

    /** 字符串的 SHA1 */
    public static byte[] sha1_string(String content) throws NoSuchAlgorithmException {
        return CodecUdfSource.digestString(DigestType.SHA1.name(), content);
    }

    /** 字节数组的 SHA-256 */
    public static byte[] sha256_bytes(List<Byte> content) throws NoSuchAlgorithmException {
        return CodecUdfSource.digestBytes(DigestType.SHA256.name(), content);
    }

    /** 字符串的 SHA-256 */
    public static byte[] sha256_string(String content) throws NoSuchAlgorithmException {
        return CodecUdfSource.digestString(DigestType.SHA256.name(), content);
    }

    /** 字节数组的 SHA-512 */
    public static byte[] sha512_bytes(List<Byte> content) throws NoSuchAlgorithmException {
        return CodecUdfSource.digestBytes(DigestType.SHA512.name(), content);
    }

    /** 字符串的 SHA-512 */
    public static byte[] sha512_string(String content) throws NoSuchAlgorithmException {
        return CodecUdfSource.digestString(DigestType.SHA512.name(), content);
    }
}