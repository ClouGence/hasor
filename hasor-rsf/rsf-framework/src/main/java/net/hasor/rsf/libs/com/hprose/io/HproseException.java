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
 * HproseException.java                                   *
 *                                                        *
 * hprose exception for Java.                             *
 *                                                        *
 * LastModified: Apr 26, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io;
import java.io.IOException;
public class HproseException extends IOException {
    private final static long serialVersionUID = -6146544906159301857L;
    public HproseException() {
        super();
    }
    public HproseException(String msg) {
        super(msg);
    }
    public HproseException(Throwable e) {
        super(e.getMessage());
        setStackTrace(e.getStackTrace());
    }
    public HproseException(String msg, Throwable e) {
        super(msg);
        setStackTrace(e.getStackTrace());
    }
}