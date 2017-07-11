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
 * HashSetUnserializer.java                               *
 *                                                        *
 * HashSet unserializer class for Java.                   *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashSet;

import static net.hasor.rsf.libs.com.hprose.io.HproseTags.TagList;
public final class HashSetUnserializer extends BaseUnserializer<HashSet> {
    public final static HashSetUnserializer instance = new HashSetUnserializer();
    @Override
    public HashSet unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag == TagList)
            return ReferenceReader.readHashSet(reader, type);
        return super.unserialize(reader, tag, type);
    }
    public HashSet read(Reader reader) throws IOException {
        return super.read(reader, HashSet.class);
    }
}
