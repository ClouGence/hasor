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
 * URIUnserializer.java                                   *
 *                                                        *
 * URI unserializer class for Java.                       *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.libs.com.hprose.io.unserialize;
import net.hasor.libs.com.hprose.io.convert.URIConverter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;

import static net.hasor.libs.com.hprose.io.HproseTags.TagEmpty;
import static net.hasor.libs.com.hprose.io.HproseTags.TagString;
public final class URIUnserializer extends BaseUnserializer<URI> {
    public final static URIUnserializer instance = new URIUnserializer();
    @Override
    public URI unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag == TagString) {
            String str = ReferenceReader.readString(reader);
            return URIConverter.instance.convertTo(str, URI.class);
        }
        if (tag == TagEmpty)
            return null;
        return super.unserialize(reader, tag, type);
    }
    public URI read(Reader reader) throws IOException {
        return read(reader, URI.class);
    }
}
