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
 * LinkedHashMapUnserializer.java                         *
 *                                                        *
 * LinkedHashMap unserializer class for Java.             *
 *                                                        *
 * LastModified: Aug 4, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;

import static net.hasor.rsf.libs.com.hprose.io.HproseTags.*;
public final class LinkedHashMapUnserializer extends BaseUnserializer<LinkedHashMap> {
    public final static LinkedHashMapUnserializer instance = new LinkedHashMapUnserializer();
    @Override
    public LinkedHashMap unserialize(Reader reader, int tag, Type type) throws IOException {
        switch (tag) {
        case TagList:
            return ReferenceReader.readListAsLinkedHashMap(reader, type);
        case TagMap:
            return ReferenceReader.readLinkedHashMap(reader, type);
        case TagObject:
            return ReferenceReader.readObjectAsLinkedHashMap(reader, type);
        }
        return super.unserialize(reader, tag, type);
    }
    public LinkedHashMap read(Reader reader) throws IOException {
        return read(reader, LinkedHashMap.class);
    }
}
