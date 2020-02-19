package net.hasor.test.dataql.udfs;
import net.hasor.dataql.DimUdfSource;
import net.hasor.dataql.sdk.DateTimeUdfSource;

@DimUdfSource("time")
public class TimeUdfSource extends DateTimeUdfSource {
    /** 格式化为：yyyy-MM-dd HH:mm:ss */
    public String ymd_hms(Object time) {
        if (time == null) {
            return null;
        }
        return format(Long.parseLong(time.toString()), "yyyy-MM-dd HH:mm:ss");
    }

    /** 格式化为：yyyy-MM-dd */
    public String ymd(Object time) {
        if (time == null) {
            return null;
        }
        return format(Long.parseLong(time.toString()), "yyyy-MM-dd");
    }

    /** 格式化为：HH:mm:ss */
    public String hms(Object time) {
        if (time == null) {
            return null;
        }
        return format(Long.parseLong(time.toString()), "HH:mm:ss");
    }
}

