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
 * Unserializer.java                                      *
 *                                                        *
 * hprose unserializer interface for Java.                *
 *                                                        *
 * LastModified: Aug 1, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import java.io.IOException;
import java.lang.reflect.Type;
public interface Unserializer<T> {
    T read(Reader reader, int tag, Type type) throws IOException;
}
