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
 * ReferenceSerializer.java                               *
 *                                                        *
 * hprose Reference Serializer class for Java.            *
 *                                                        *
 * LastModified: Jul 31, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.serialize;
import java.io.IOException;
public abstract class ReferenceSerializer<T> implements Serializer<T> {
    public void serialize(Writer writer, T obj) throws IOException {
        writer.setRef(obj);
        // write your actual serialization code in sub class
    }
    public void write(Writer writer, T obj) throws IOException {
        if (!writer.writeRef(obj)) {
            serialize(writer, obj);
        }
    }
}
