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
package net.hasor.tconsole.launcher;
import io.netty.buffer.ByteBuf;
import net.hasor.tconsole.TelAttribute;
import net.hasor.utils.convert.ConverterUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

/**
 * 工具集
 * @version : 2016年09月20日
 * @author 赵永春 (zyc@hasor.net)
 */
public class TelUtils {
    public static boolean aBoolean(TelAttribute telAttribute, String key) {
        Object aboolean = telAttribute.getAttribute(key);
        if (aboolean == null) {
            aboolean = false;
        }
        return (Boolean) ConverterUtils.convert(Boolean.TYPE, aboolean);
    }

    public static int aInteger(TelAttribute telAttribute, String key) {
        Object aInteger = telAttribute.getAttribute(key);
        if (aInteger == null) {
            aInteger = 0;
        }
        return (Integer) ConverterUtils.convert(Integer.TYPE, aInteger);
    }

    public static InetAddress finalBindAddress(String hostString) throws UnknownHostException {
        return "local".equalsIgnoreCase(hostString) ? InetAddress.getLocalHost() : InetAddress.getByName(hostString);
    }

    public static String aString(TelAttribute telAttribute, String key) {
        Object aInteger = telAttribute.getAttribute(key);
        if (aInteger == null) {
            aInteger = "";
        }
        return aInteger.toString();
    }

    // 滑动窗口的机制
    public static int waitString(ByteBuf byteBuf, String waitString) {
        int waitLength = waitString.length();
        if (byteBuf.readableBytes() >= waitLength) {
            int loopCount = byteBuf.readableBytes() - waitLength;
            for (int i = 0; i <= loopCount; i++) {
                String dat = byteBuf.getCharSequence(i, waitLength, StandardCharsets.UTF_8).toString();
                if (dat.equals(waitString)) {
                    return i;
                }
            }
        }
        return -1;
    }
}