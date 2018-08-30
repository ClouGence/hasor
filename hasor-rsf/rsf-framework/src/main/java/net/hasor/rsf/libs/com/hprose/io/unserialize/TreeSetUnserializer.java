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
 * TreeSetUnserializer.java                               *
 *                                                        *
 * TreeSet unserializer class for Java.                   *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.TreeSet;

import static net.hasor.rsf.libs.com.hprose.io.HproseTags.TagList;
public final class TreeSetUnserializer extends BaseUnserializer<TreeSet> {
    public final static TreeSetUnserializer instance = new TreeSetUnserializer();
    @Override
    public TreeSet unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag == TagList)
            return ReferenceReader.readTreeSet(reader, type);
        return super.unserialize(reader, tag, type);
    }
    public TreeSet read(Reader reader) throws IOException {
        return read(reader, TreeSet.class);
    }
}
