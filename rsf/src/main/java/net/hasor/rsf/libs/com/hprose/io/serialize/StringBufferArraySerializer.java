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
 * StringBufferArraySerializer.java                       *
 *                                                        *
 * StringBuffer array serializer class for Java.          *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.serialize;
import net.hasor.rsf.libs.com.hprose.io.HproseTags;

import java.io.IOException;
import java.io.OutputStream;
public final class StringBufferArraySerializer extends ReferenceSerializer<StringBuffer[]> {
    public final static StringBufferArraySerializer instance = new StringBufferArraySerializer();
    @Override
    public final void serialize(Writer writer, StringBuffer[] array) throws IOException {
        super.serialize(writer, array);
        OutputStream stream = writer.stream;
        stream.write(HproseTags.TagList);
        int length = array.length;
        if (length > 0) {
            ValueWriter.writeInt(stream, length);
        }
        stream.write(HproseTags.TagOpenbrace);
        for (int i = 0; i < length; ++i) {
            StringBuffer e = array[i];
            if (e == null) {
                stream.write(HproseTags.TagNull);
            } else {
                StringBufferSerializer.instance.write(writer, e);
            }
        }
        stream.write(HproseTags.TagClosebrace);
    }
}
