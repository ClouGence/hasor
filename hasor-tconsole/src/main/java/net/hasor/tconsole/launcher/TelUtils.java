package net.hasor.tconsole.launcher;
import net.hasor.tconsole.TelAttribute;
import net.hasor.utils.convert.ConverterUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

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
}
