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
 * OtherTypeUnserializer.java                             *
 *                                                        *
 * other type unserializer class for Java.                *
 *                                                        *
 * LastModified: Aug 4, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import net.hasor.rsf.libs.com.hprose.io.HproseTags;

import java.io.IOException;
import java.lang.reflect.Type;
public class ObjectUnserializer extends BaseUnserializer {
    public final static ObjectUnserializer instance = new ObjectUnserializer();
    @Override
    public Object unserialize(Reader reader, int tag, Type type) throws IOException {
        switch (tag) {
        case HproseTags.TagEmpty:
            return null;
        case HproseTags.TagMap:
            return ReferenceReader.readMapAsObject(reader, type);
        case HproseTags.TagObject:
            return ReferenceReader.readObject(reader, type);
        }
        return super.unserialize(reader, tag, type);
    }
    public Object read(Reader reader) throws IOException {
        return read(reader, Object.class);
    }
}
