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
 * CharSerializer.java                                    *
 *                                                        *
 * character serializer class for Java.                   *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.serialize;
import java.io.IOException;
public final class CharSerializer implements Serializer<Character> {
    public final static CharSerializer instance = new CharSerializer();
    public final void write(Writer writer, Character obj) throws IOException {
        ValueWriter.write(writer.stream, obj);
    }
}
