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

import javax.inject.Singleton;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * 摘要算法库。函数库引入 <code>import 'net.hasor.dataql.fx.encryt.DigestUdfSource' as digest;</code>
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-31
 */
@Singleton
public class DigestUdfSource implements UdfSourceAssembly {
    private enum DigestType {
        MD5("MD5"),         //
        SHA("SHA"),         //
        SHA1("SHA1"),       //
        SHA256("SHA-256"),  //
        SHA512("SHA-512");  //
        private String digestDesc;

        private DigestType(String digestDesc) {
            this.digestDesc = digestDesc;
        }

        public String getDigestDesc() {
            return digestDesc;
        }
    }

    /** 使用指定方式进行摘要计算 */
    public static byte[] digestBytes(DigestType digestType, List<Byte> content) throws NoSuchAlgorithmException {
        if (content == null) {
            return null;
        }
        MessageDigest mdTemp = MessageDigest.getInstance(digestType.getDigestDesc());
        Byte[] bytes = content.toArray(new Byte[0]);
        mdTemp.update(ArrayUtils.toPrimitive(bytes));
        return mdTemp.digest();
    }

    /** 使用指定方式进行摘要计算 */
    public static byte[] digestString(DigestType digestType, String content) throws NoSuchAlgorithmException {
        if (content == null) {
            return null;
        }
        MessageDigest mdTemp = MessageDigest.getInstance(digestType.getDigestDesc());
        mdTemp.update(content.getBytes());
        return mdTemp.digest();
    }

    /** 字节数组的 MD5 */
    public static byte[] md5_bytes(List<Byte> content) throws NoSuchAlgorithmException {
        return digestBytes(DigestType.MD5, content);
    }

    /** 字符串的 MD5 */
    public static byte[] md5_string(String content) throws NoSuchAlgorithmException {
        return digestString(DigestType.MD5, content);
    }

    /** 字节数组的 SHA */
    public static byte[] sha_bytes(List<Byte> content) throws NoSuchAlgorithmException {
        return digestBytes(DigestType.SHA, content);
    }

    /** 字符串的 SHA */
    public static byte[] sha_string(String content) throws NoSuchAlgorithmException {
        return digestString(DigestType.SHA, content);
    }

    /** 字节数组的 SHA1 */
    public static byte[] sha1_bytes(List<Byte> content) throws NoSuchAlgorithmException {
        return digestBytes(DigestType.SHA1, content);
    }

    /** 字符串的 SHA1 */
    public static byte[] sha1_string(String content) throws NoSuchAlgorithmException {
        return digestString(DigestType.SHA1, content);
    }

    /** 字节数组的 SHA-256 */
    public static byte[] sha256_bytes(List<Byte> content) throws NoSuchAlgorithmException {
        return digestBytes(DigestType.SHA256, content);
    }

    /** 字符串的 SHA-256 */
    public static byte[] sha256_string(String content) throws NoSuchAlgorithmException {
        return digestString(DigestType.SHA256, content);
    }

    /** 字节数组的 SHA-512 */
    public static byte[] sha512_bytes(List<Byte> content) throws NoSuchAlgorithmException {
        return digestBytes(DigestType.SHA512, content);
    }

    /** 字符串的 SHA-512 */
    public static byte[] sha512_string(String content) throws NoSuchAlgorithmException {
        return digestString(DigestType.SHA512, content);
    }
}