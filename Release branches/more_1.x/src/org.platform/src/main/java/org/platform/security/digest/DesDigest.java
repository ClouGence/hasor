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
package org.platform.security.digest;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import org.more.util.CommonCodeUtil.Base64;
import org.platform.security.CodeDigest;
/**
 * DES加密。
 * @version : 2013-4-24
 * @author 赵永春 (zyc@byshell.org)
 */
public final class DesDigest implements CodeDigest {
    private static final String DES = "DES";
    /**编码 */
    public String encode(String strValue, String generateKey) throws Exception {
        SecureRandom sr = new SecureRandom(); //DES算法要求有一个可信任的随机数源
        DESKeySpec dks = new DESKeySpec(generateKey.getBytes()); // 从原始密匙数据创建DESKeySpec对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES); // 创建一个密匙工厂，然后用它把DESKeySpec转换成,一个SecretKey对象
        SecretKey securekey = keyFactory.generateSecret(dks); // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance(DES); // 用密匙初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);// 现在，获取数据并加密
        byte[] bytesData = cipher.doFinal(strValue.getBytes("utf-8"));// 正式执行加密操作
        return Base64.base64EncodeFoArray(bytesData);
    };
    /**解码*/
    public String decode(String strValue, String generateKey) throws Exception {
        byte[] bytesData = Base64.base64DecodeToArray(strValue);
        SecureRandom sr = new SecureRandom(); //DES算法要求有一个可信任的随机数源
        DESKeySpec dks = new DESKeySpec(generateKey.getBytes()); // 从原始密匙数据创建DESKeySpec对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES); // 创建一个密匙工厂，然后用它把DESKeySpec转换成,一个SecretKey对象
        SecretKey securekey = keyFactory.generateSecret(dks); // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance(DES); // 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);// 现在，获取数据并加密
        bytesData = cipher.doFinal(bytesData);// 正式执行解密操作
        return new String(bytesData, "utf-8");
    };
}