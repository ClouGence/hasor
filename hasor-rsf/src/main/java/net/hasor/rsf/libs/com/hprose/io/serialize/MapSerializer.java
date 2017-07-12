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
 * MapSerializer.java                                     *
 *                                                        *
 * Map serializer class for Java.                         *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.serialize;
import net.hasor.rsf.libs.com.hprose.io.HproseTags;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
public final class MapSerializer<K, V> extends ReferenceSerializer<Map<K, V>> {
    public final static MapSerializer instance = new MapSerializer();
    @Override
    public final void serialize(Writer writer, Map<K, V> map) throws IOException {
        super.serialize(writer, map);
        OutputStream stream = writer.stream;
        int count = map.size();
        stream.write(HproseTags.TagMap);
        if (count > 0) {
            ValueWriter.writeInt(stream, count);
        }
        stream.write(HproseTags.TagOpenbrace);
        Iterator<Map.Entry<K, V>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<K, V> entry = it.next();
            writer.serialize(entry.getKey());
            writer.serialize(entry.getValue());
        }
        stream.write(HproseTags.TagClosebrace);
    }
}
