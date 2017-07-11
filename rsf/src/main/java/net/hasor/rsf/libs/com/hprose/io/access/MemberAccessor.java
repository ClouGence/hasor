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
 * MemberAccessor.java                                    *
 *                                                        *
 * MemberAccessor interface for Java.                     *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.access;
import net.hasor.rsf.libs.com.hprose.io.serialize.Writer;
import net.hasor.rsf.libs.com.hprose.io.unserialize.Reader;

import java.io.IOException;
public interface MemberAccessor {
    void serialize(Writer writer, Object obj) throws IOException;

    void unserialize(Reader reader, Object obj) throws IOException;
}