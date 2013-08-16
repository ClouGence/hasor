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
package org.hasor.security.digest;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import org.hasor.security.Digest;
import org.more.util.CommonCodeUtils.Base64;
/**
 * 随机加密。
 * @version : 2013-4-24
 * @author 赵永春 (zyc@byshell.org)
 */
public final class RandomDigest implements Digest {
    @Override
    public String encrypt(String strValue, String generateKey) throws Throwable {
        PBEKeySpec pbks = new PBEKeySpec(generateKey.toCharArray());
        // 由口令生成密钥
        SecretKeyFactory kf = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey k = kf.generateSecret(pbks);
        // 生成随机数盐
        byte[] salt = new byte[8];
        Random r = new Random(System.currentTimeMillis());
        r.nextBytes(salt);
        // 创建并初始化密码器
        Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
        PBEParameterSpec ps = new PBEParameterSpec(salt, 1000);
        cipher.init(Cipher.ENCRYPT_MODE, k, ps);
        // 获取明文，执行加密
        byte[] bytesData = cipher.doFinal(strValue.getBytes("utf-8"));// 正式执行加密操作
        byte[] finalBytesData = new byte[bytesData.length + salt.length];
        System.arraycopy(salt, 0, finalBytesData, 0, salt.length);
        System.arraycopy(bytesData, 0, finalBytesData, salt.length, bytesData.length);
        return Base64.base64EncodeFoArray(finalBytesData);
    }
    @Override
    public String decrypt(String strValue, String generateKey) throws Throwable {
        // 取得密文和盐
        byte[] finalBytesData = Base64.base64DecodeToArray(strValue);
        byte[] salt = new byte[8];
        byte[] bytesData = new byte[finalBytesData.length - salt.length];// 正式执行加密操作
        System.arraycopy(finalBytesData, 0, salt, 0, salt.length);
        System.arraycopy(finalBytesData, salt.length, bytesData, 0, bytesData.length);
        //
        PBEKeySpec pbks = new PBEKeySpec(generateKey.toCharArray());
        // 由口令生成密钥
        SecretKeyFactory kf = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey k = kf.generateSecret(pbks);
        // 创建并初始化密码器
        Cipher cp = Cipher.getInstance("PBEWithMD5AndDES");
        PBEParameterSpec ps = new PBEParameterSpec(salt, 1000);
        cp.init(Cipher.DECRYPT_MODE, k, ps);
        byte[] ptext = cp.doFinal(bytesData);
        return new String(ptext, "utf-8");
    };
}