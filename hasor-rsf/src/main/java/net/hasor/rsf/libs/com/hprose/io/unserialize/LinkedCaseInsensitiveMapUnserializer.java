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
 * LinkedCaseInsensitiveMapUnserializer.java              *
 *                                                        *
 * LinkedCaseInsensitiveMap unserializer class for Java.  *
 *                                                        *
 * LastModified: Aug 4, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import net.hasor.rsf.libs.com.hprose.utils.LinkedCaseInsensitiveMap;

import java.io.IOException;
import java.lang.reflect.Type;

import static net.hasor.rsf.libs.com.hprose.io.HproseTags.*;
public final class LinkedCaseInsensitiveMapUnserializer extends BaseUnserializer<LinkedCaseInsensitiveMap> {
    public final static LinkedCaseInsensitiveMapUnserializer instance = new LinkedCaseInsensitiveMapUnserializer();
    @Override
    public LinkedCaseInsensitiveMap unserialize(Reader reader, int tag, Type type) throws IOException {
        switch (tag) {
        case TagList:
            return ReferenceReader.readListAsLinkedCaseInsensitiveMap(reader, type);
        case TagMap:
            return ReferenceReader.readLinkedCaseInsensitiveMap(reader, type);
        case TagObject:
            return ReferenceReader.readObjectAsLinkedCaseInsensitiveMap(reader, type);
        }
        return super.unserialize(reader, tag, type);
    }
    public LinkedCaseInsensitiveMap read(Reader reader) throws IOException {
        return read(reader, LinkedCaseInsensitiveMap.class);
    }
}
