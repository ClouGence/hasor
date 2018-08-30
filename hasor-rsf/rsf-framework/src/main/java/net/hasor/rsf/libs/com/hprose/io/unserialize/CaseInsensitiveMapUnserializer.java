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
 * CaseInsensitiveMapUnserializer.java                    *
 *                                                        *
 * CaseInsensitiveMap unserializer class for Java.        *
 *                                                        *
 * LastModified: Aug 4, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.unserialize;
import net.hasor.rsf.libs.com.hprose.io.HproseTags;
import net.hasor.rsf.libs.com.hprose.utils.CaseInsensitiveMap;

import java.io.IOException;
import java.lang.reflect.Type;
public final class CaseInsensitiveMapUnserializer extends BaseUnserializer<CaseInsensitiveMap> {
    public final static CaseInsensitiveMapUnserializer instance = new CaseInsensitiveMapUnserializer();
    @Override
    public CaseInsensitiveMap unserialize(Reader reader, int tag, Type type) throws IOException {
        switch (tag) {
        case HproseTags.TagList:
            return ReferenceReader.readListAsCaseInsensitiveMap(reader, type);
        case HproseTags.TagMap:
            return ReferenceReader.readCaseInsensitiveMap(reader, type);
        case HproseTags.TagObject:
            return ReferenceReader.readObjectAsCaseInsensitiveMap(reader, type);
        }
        return super.unserialize(reader, tag, type);
    }
    public CaseInsensitiveMap read(Reader reader) throws IOException {
        return read(reader, CaseInsensitiveMap.class);
    }
}
