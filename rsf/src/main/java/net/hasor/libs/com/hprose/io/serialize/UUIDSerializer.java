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
 * UUIDSerializer.java                                    *
 *                                                        *
 * UUID serializer class for Java.                        *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.libs.com.hprose.io.serialize;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import static net.hasor.libs.com.hprose.io.HproseTags.*;
public final class UUIDSerializer extends ReferenceSerializer<UUID> {
    public final static UUIDSerializer instance = new UUIDSerializer();
    @Override
    public final void serialize(Writer writer, UUID uuid) throws IOException {
        super.serialize(writer, uuid);
        OutputStream stream = writer.stream;
        stream.write(TagGuid);
        stream.write(TagOpenbrace);
        stream.write(ValueWriter.getAscii(uuid.toString()));
        stream.write(TagClosebrace);
    }
}
