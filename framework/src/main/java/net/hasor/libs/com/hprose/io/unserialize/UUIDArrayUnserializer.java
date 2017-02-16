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
 * UUIDArrayUnserializer.java                             *
 *                                                        *
 * UUID array unserializer class for Java.                *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.libs.com.hprose.io.unserialize;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.UUID;

import static net.hasor.libs.com.hprose.io.HproseTags.TagEmpty;
import static net.hasor.libs.com.hprose.io.HproseTags.TagList;
public final class UUIDArrayUnserializer extends BaseUnserializer<UUID[]> {
    public final static UUIDArrayUnserializer instance = new UUIDArrayUnserializer();
    @Override
    public UUID[] unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag == TagList)
            return ReferenceReader.readUUIDArray(reader);
        if (tag == TagEmpty)
            return new UUID[0];
        return super.unserialize(reader, tag, type);
    }
    public UUID[] read(Reader reader) throws IOException {
        return read(reader, UUID[].class);
    }
}
