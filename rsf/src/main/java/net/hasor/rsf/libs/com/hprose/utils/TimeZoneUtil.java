/**********************************************************\
 |                                                          |
 |                          hprose                          |
 |                                                          |
 | Official WebSite: http://www.hprose.com/                 |
 |                   http://www.hprose.org/                 |
 |                                                          |
 \**********************************************************/
/**********************************************************\
 *                                                        *
 * TimeZoneUtil.java                                      *
 *                                                        *
 * TimeZone Util class for Java.                          *
 *                                                        *
 * LastModified: Apr 27, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.utils;
import java.util.TimeZone;
public final class TimeZoneUtil {
    public final static TimeZone UTC       = TimeZone.getTimeZone("UTC");
    public final static TimeZone DefaultTZ = TimeZone.getDefault();
}
