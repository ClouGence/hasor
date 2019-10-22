package net.hasor.tconsole.launcher;
import io.netty.buffer.ByteBuf;
import net.hasor.tconsole.TelAttribute;
import net.hasor.utils.convert.ConverterUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

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
