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
 * UUIDUnserializer.java                                  *
 *                                                        *
 * UUID unserializer class for Java.                      *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.UUID;

import static net.hasor.rsf.libs.com.hprose.io.HproseTags.*;
public final class UUIDUnserializer extends BaseUnserializer<UUID> {
    public final static UUIDUnserializer instance = new UUIDUnserializer();
    @Override
    public UUID unserialize(Reader reader, int tag, Type type) throws IOException {
        switch (tag) {
        case TagEmpty:
            return null;
        case TagString:
            return UUID.fromString(ReferenceReader.readString(reader));
        case TagBytes:
            return UUID.nameUUIDFromBytes(ReferenceReader.readBytes(reader));
        case TagGuid:
            return ReferenceReader.readUUID(reader);
        }
        return super.unserialize(reader, tag, type);
    }
    public UUID read(Reader reader) throws IOException {
        return read(reader, UUID.class);
    }
}
