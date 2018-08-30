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
 * TimestampArrayUnserializer.java                        *
 *                                                        *
 * Timestamp array unserializer class for Java.           *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import net.hasor.rsf.libs.com.hprose.io.HproseTags;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
public final class TimestampArrayUnserializer extends BaseUnserializer<Timestamp[]> {
    public final static TimestampArrayUnserializer instance = new TimestampArrayUnserializer();
    @Override
    public Timestamp[] unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag == HproseTags.TagList)
            return ReferenceReader.readTimestampArray(reader);
        if (tag == HproseTags.TagEmpty)
            return new Timestamp[0];
        return super.unserialize(reader, tag, type);
    }
    public Timestamp[] read(Reader reader) throws IOException {
        return read(reader, Timestamp[].class);
    }
}
