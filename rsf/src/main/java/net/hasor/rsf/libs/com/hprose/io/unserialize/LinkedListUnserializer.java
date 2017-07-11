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
 * LinkedListUnserializer.java                            *
 *                                                        *
 * LinkedList unserializer class for Java.                *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedList;

import static net.hasor.rsf.libs.com.hprose.io.HproseTags.TagList;
public final class LinkedListUnserializer extends BaseUnserializer<LinkedList> {
    public final static LinkedListUnserializer instance = new LinkedListUnserializer();
    @Override
    public LinkedList unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag == TagList)
            return ReferenceReader.readLinkedList(reader, type);
        return super.unserialize(reader, tag, type);
    }
    public LinkedList read(Reader reader) throws IOException {
        return read(reader, LinkedList.class);
    }
}
