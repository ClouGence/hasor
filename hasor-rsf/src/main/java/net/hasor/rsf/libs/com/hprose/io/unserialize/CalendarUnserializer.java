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
 * CalendarUnserializer.java                              *
 *                                                        *
 * Calendar unserializer class for Java.                  *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import net.hasor.rsf.libs.com.hprose.io.convert.CalendarConverter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Calendar;

import static net.hasor.rsf.libs.com.hprose.io.HproseTags.*;
public final class CalendarUnserializer extends BaseUnserializer<Calendar> {
    public final static CalendarUnserializer instance = new CalendarUnserializer();
    @Override
    public Calendar unserialize(Reader reader, int tag, Type type) throws IOException {
        CalendarConverter converter = CalendarConverter.instance;
        switch (tag) {
        case TagDate:
            return ReferenceReader.readDateTime(reader).toCalendar();
        case TagTime:
            return ReferenceReader.readTime(reader).toCalendar();
        case TagEmpty:
            return null;
        case TagInteger:
        case TagLong:
            return converter.convertTo(ValueReader.readLong(reader));
        case TagDouble:
            return converter.convertTo(ValueReader.readDouble(reader));
        }
        if (tag >= '0' && tag <= '9')
            return converter.convertTo((long) (tag - '0'));
        return super.unserialize(reader, tag, type);
    }
    public Calendar read(Reader reader) throws IOException {
        return read(reader, Calendar.class);
    }
}