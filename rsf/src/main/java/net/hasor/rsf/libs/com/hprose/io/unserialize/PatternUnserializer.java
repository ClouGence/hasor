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
 * PatternUnserializer.java                               *
 *                                                        *
 * Pattern unserializer class for Java.                   *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.regex.Pattern;

import static net.hasor.rsf.libs.com.hprose.io.HproseTags.TagEmpty;
import static net.hasor.rsf.libs.com.hprose.io.HproseTags.TagString;
public final class PatternUnserializer extends BaseUnserializer<Pattern> {
    public final static PatternUnserializer instance = new PatternUnserializer();
    @Override
    public Pattern unserialize(Reader reader, int tag, Type type) throws IOException {
        switch (tag) {
        case TagString:
            return Pattern.compile(ReferenceReader.readString(reader));
        case TagEmpty:
            return null;
        }
        return super.unserialize(reader, tag, type);
    }
    public Pattern read(Reader reader) throws IOException {
        return read(reader, Pattern.class);
    }
}
