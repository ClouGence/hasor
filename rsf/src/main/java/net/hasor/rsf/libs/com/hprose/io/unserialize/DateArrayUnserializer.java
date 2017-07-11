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
 * DateArrayUnserializer.java                             *
 *                                                        *
 * Date array unserializer class for Java.                *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import net.hasor.rsf.libs.com.hprose.io.HproseTags;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Date;
public final class DateArrayUnserializer extends BaseUnserializer<Date[]> {
    public final static DateArrayUnserializer instance = new DateArrayUnserializer();
    @Override
    public Date[] unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag == HproseTags.TagList)
            return ReferenceReader.readDateArray(reader);
        if (tag == HproseTags.TagEmpty)
            return new Date[0];
        return super.unserialize(reader, tag, type);
    }
    public Date[] read(Reader reader) throws IOException {
        return read(reader, Date[].class);
    }
}
