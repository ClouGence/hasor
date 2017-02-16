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
 * HashMapUnserializer.java                               *
 *                                                        *
 * HashMap unserializer class for Java.                   *
 *                                                        *
 * LastModified: Aug 4, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.libs.com.hprose.io.unserialize;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;

import static net.hasor.libs.com.hprose.io.HproseTags.*;
public final class HashMapUnserializer extends BaseUnserializer<HashMap> {
    public final static HashMapUnserializer instance = new HashMapUnserializer();
    @Override
    public HashMap unserialize(Reader reader, int tag, Type type) throws IOException {
        switch (tag) {
        case TagList:
            return ReferenceReader.readListAsHashMap(reader, type);
        case TagMap:
            return ReferenceReader.readHashMap(reader, type);
        case TagObject:
            return ReferenceReader.readObjectAsHashMap(reader, type);
        }
        return super.unserialize(reader, tag, type);
    }
    public HashMap read(Reader reader) throws IOException {
        return super.read(reader, HashMap.class);
    }
}
