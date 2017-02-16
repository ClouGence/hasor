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
 * TimeArrayUnserializer.java                             *
 *                                                        *
 * Time array unserializer class for Java.                *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.libs.com.hprose.io.unserialize;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Time;

import static net.hasor.libs.com.hprose.io.HproseTags.TagEmpty;
import static net.hasor.libs.com.hprose.io.HproseTags.TagList;
public final class TimeArrayUnserializer extends BaseUnserializer<Time[]> {
    public final static TimeArrayUnserializer instance = new TimeArrayUnserializer();
    @Override
    public Time[] unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag == TagList)
            return ReferenceReader.readTimeArray(reader);
        if (tag == TagEmpty)
            return new Time[0];
        return super.unserialize(reader, tag, type);
    }
    public Time[] read(Reader reader) throws IOException {
        return read(reader, Time[].class);
    }
}
