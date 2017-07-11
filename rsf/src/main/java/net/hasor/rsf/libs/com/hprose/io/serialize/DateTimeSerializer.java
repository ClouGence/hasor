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
 * DateTimeSerializer.java                                *
 *                                                        *
 * DateTime serializer class for Java.                    *
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
import java.util.Calendar;
import java.util.Date;
public final class DateTimeSerializer extends ReferenceSerializer<Date> {
    public final static DateTimeSerializer instance = new DateTimeSerializer();
    @Override
    public final void serialize(Writer writer, Date date) throws IOException {
        super.serialize(writer, date);
        OutputStream stream = writer.stream;
        Calendar calendar = DateTime.toCalendar(date);
        ValueWriter.writeDateOfCalendar(stream, calendar);
        ValueWriter.writeTimeOfCalendar(stream, calendar, true, false);
        stream.write(HproseTags.TagSemicolon);
    }
}
