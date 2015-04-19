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
package net.hasor.rsf.rpc.utils;
/**
 * 
 * @version : 2014年11月17日
 * @author 赵永春(zyc@hasor.net)
 */
public class NetworkUtils {
    //
    /**根据掩码长度获取掩码字符串形式.*/
    public static String maskToStringByPrefixLength(int length) {
        return ipDataToString(ipDataByInt(maskByPrefixLength(length)));
    }
    //
    /**根据掩码长度获取子网掩码值.*/
    public static int maskByPrefixLength(int length) {
        if (length > 32) {
            throw new IndexOutOfBoundsException("mask length max is 32.");
        }
        return -1 << (32 - length);
    }
    //
    /**根据IP值分解IP为字节数组.*/
    public static byte[] ipDataByInt(int ipData) {
        byte ipParts[] = new byte[4];
        for (int i = 0; i < ipParts.length; i++) {
            int pos = ipParts.length - 1 - i;
            ipParts[pos] = (byte) (ipData >> (i * 8));
        }
        return ipParts;
    }
    /**根据IP字节数据转换为int.*/
    public static int ipDataByBytes(byte[] ipData) {
        int ipParts[] = new int[4];
        ipParts[0] = 0xFFFFFFFF & (ipData[0] << 24);
        ipParts[1] = 0x00FFFFFF & (ipData[1] << 16);
        ipParts[2] = 0x0000FFFF & (ipData[2] << 8);
        ipParts[3] = 0x000000FF & (ipData[3] << 0);
        int intIP = 0;
        for (int i = 0; i < ipParts.length; i++) {
            intIP = intIP | ipParts[i];
        }
        return intIP;
    }
    //
    /**将分解的IP数据转换为字符串*/
    public static String ipDataToString(int ipData) {
        return ipDataToString(ipDataByInt(ipData));
    }
    /**将分解的IP数据转换为字符串*/
    public static String ipDataToString(byte[] ipData) {
        String result = "";
        result = result + tostr(ipData[0]);
        for (int i = 1; i < ipData.length; i++) {
            result = result + "." + tostr(ipData[i]);
        }
        return result;
    }
    private static String tostr(byte byteData) {
        return "" + ((byteData < 0) ? 256 + byteData : byteData);
    }
}