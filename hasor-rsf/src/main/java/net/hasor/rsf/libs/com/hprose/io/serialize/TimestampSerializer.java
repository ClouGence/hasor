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
 * TimestampSerializer.java                               *
 *                                                        *
 * Timestamp serializer class for Java.                   *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.serialize;
import net.hasor.rsf.libs.com.hprose.io.HproseTags;
import net.hasor.rsf.libs.com.hprose.utils.DateTime;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.Calendar;
public final class TimestampSerializer extends ReferenceSerializer<Timestamp> {
    public final static TimestampSerializer instance = new TimestampSerializer();
    @Override
    public final void serialize(Writer writer, Timestamp time) throws IOException {
        super.serialize(writer, time);
        OutputStream stream = writer.stream;
        Calendar calendar = DateTime.toCalendar(time);
        ValueWriter.writeDateOfCalendar(stream, calendar);
        ValueWriter.writeTimeOfCalendar(stream, calendar, false, true);
        ValueWriter.writeNano(stream, time.getNanos());
        stream.write(HproseTags.TagSemicolon);
    }
}
