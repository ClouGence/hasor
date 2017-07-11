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
 * CalendarSerializer.java                                *
 *                                                        *
 * Calendar serializer class for Java.                    *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.serialize;
import net.hasor.rsf.libs.com.hprose.io.HproseTags;
import net.hasor.rsf.libs.com.hprose.utils.TimeZoneUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.TimeZone;
public final class CalendarSerializer extends ReferenceSerializer<Calendar> {
    public final static CalendarSerializer instance = new CalendarSerializer();
    @Override
    public final void serialize(Writer writer, Calendar calendar) throws IOException {
        super.serialize(writer, calendar);
        TimeZone tz = calendar.getTimeZone();
        if (!(tz.hasSameRules(TimeZoneUtil.DefaultTZ) || tz.hasSameRules(TimeZoneUtil.UTC))) {
            tz = TimeZoneUtil.UTC;
            Calendar c = (Calendar) calendar.clone();
            c.setTimeZone(tz);
            calendar = c;
        }
        OutputStream stream = writer.stream;
        ValueWriter.writeDateOfCalendar(stream, calendar);
        ValueWriter.writeTimeOfCalendar(stream, calendar, true, false);
        stream.write(tz.hasSameRules(TimeZoneUtil.UTC) ? HproseTags.TagUTC : HproseTags.TagSemicolon);
    }
}
