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
 * HproseDateTimeSerializer.java                          *
 *                                                        *
 * Hprose DateTime serializer class for Java.             *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.serialize;
import net.hasor.rsf.libs.com.hprose.utils.DateTime;

import java.io.IOException;
import java.io.OutputStream;

import static net.hasor.rsf.libs.com.hprose.io.HproseTags.TagSemicolon;
import static net.hasor.rsf.libs.com.hprose.io.HproseTags.TagUTC;
public final class HproseDateTimeSerializer extends ReferenceSerializer<DateTime> {
    public final static HproseDateTimeSerializer instance = new HproseDateTimeSerializer();
    @Override
    public final void serialize(Writer writer, DateTime dt) throws IOException {
        super.serialize(writer, dt);
        OutputStream stream = writer.stream;
        if (dt.year == 1970 && dt.month == 1 && dt.day == 1) {
            ValueWriter.writeTime(stream, dt.hour, dt.minute, dt.second, 0, false, true);
            ValueWriter.writeNano(stream, dt.nanosecond);
        } else {
            ValueWriter.writeDate(stream, dt.year, dt.month, dt.day);
            if (dt.nanosecond == 0) {
                ValueWriter.writeTime(stream, dt.hour, dt.minute, dt.second, 0, true, true);
            } else {
                ValueWriter.writeTime(stream, dt.hour, dt.minute, dt.second, 0, false, true);
                ValueWriter.writeNano(stream, dt.nanosecond);
            }
        }
        stream.write(dt.utc ? TagUTC : TagSemicolon);
    }
}
