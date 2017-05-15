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
 * WriterRefer.java                                       *
 *                                                        *
 * writer refer class for Java.                           *
 *                                                        *
 * LastModified: Aug 7, 2015                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.libs.com.hprose.io.serialize;
import java.io.IOException;
import java.io.OutputStream;
import java.util.IdentityHashMap;

import static net.hasor.libs.com.hprose.io.HproseTags.TagRef;
import static net.hasor.libs.com.hprose.io.HproseTags.TagSemicolon;
final class WriterRefer {
    private final IdentityHashMap<Object, Integer> ref     = new IdentityHashMap<Object, Integer>();
    private       int                              lastref = 0;
    public final void addCount(int count) {
        lastref += count;
    }
    public final void set(Object obj) {
        ref.put(obj, lastref++);
    }
    public final boolean write(OutputStream stream, Object obj) throws IOException {
        Integer r = ref.get(obj);
        if (r != null) {
            stream.write(TagRef);
            ValueWriter.writeInt(stream, r);
            stream.write(TagSemicolon);
            return true;
        }
        return false;
    }
    public final void reset() {
        ref.clear();
        lastref = 0;
    }
}