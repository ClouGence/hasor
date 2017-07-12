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
 * OtherTypeSerializer.java                               *
 *                                                        *
 * other type serializer class for Java.                  *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io.serialize;
import net.hasor.rsf.libs.com.hprose.io.HproseMode;
import net.hasor.rsf.libs.com.hprose.io.access.Accessors;
import net.hasor.rsf.libs.com.hprose.io.access.MemberAccessor;
import net.hasor.rsf.libs.com.hprose.utils.ClassUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.hasor.rsf.libs.com.hprose.io.HproseTags.*;
public final class OtherTypeSerializer extends ReferenceSerializer {
    public final static  OtherTypeSerializer                                              instance    = new OtherTypeSerializer();
    private final static EnumMap<HproseMode, ConcurrentHashMap<Class<?>, SerializeCache>> memberCache = new EnumMap<HproseMode, ConcurrentHashMap<Class<?>, SerializeCache>>(HproseMode.class);

    static {
        memberCache.put(HproseMode.FieldMode, new ConcurrentHashMap<Class<?>, SerializeCache>());
        memberCache.put(HproseMode.PropertyMode, new ConcurrentHashMap<Class<?>, SerializeCache>());
        memberCache.put(HproseMode.MemberMode, new ConcurrentHashMap<Class<?>, SerializeCache>());
    }
    final static class SerializeCache {
        byte[] data;
        int    refcount;
    }
    private static void writeObject(Writer writer, Object object, Class<?> type) throws IOException {
        Map<String, MemberAccessor> members = Accessors.getMembers(type, writer.mode);
        for (Map.Entry<String, MemberAccessor> entry : members.entrySet()) {
            MemberAccessor member = entry.getValue();
            member.serialize(writer, object);
        }
    }
    private static int writeClass(Writer writer, Class<?> type) throws IOException {
        SerializeCache cache = memberCache.get(writer.mode).get(type);
        if (cache == null) {
            cache = new SerializeCache();
            ByteArrayOutputStream cachestream = new ByteArrayOutputStream();
            Map<String, MemberAccessor> members = Accessors.getMembers(type, writer.mode);
            int count = members.size();
            cachestream.write(TagClass);
            ValueWriter.write(cachestream, ClassUtil.getClassAlias(type));
            if (count > 0) {
                ValueWriter.writeInt(cachestream, count);
            }
            cachestream.write(TagOpenbrace);
            for (Map.Entry<String, MemberAccessor> member : members.entrySet()) {
                cachestream.write(TagString);
                ValueWriter.write(cachestream, member.getKey());
                ++cache.refcount;
            }
            cachestream.write(TagClosebrace);
            cache.data = cachestream.toByteArray();
            memberCache.get(writer.mode).put(type, cache);
        }
        writer.stream.write(cache.data);
        if (writer.refer != null) {
            writer.refer.addCount(cache.refcount);
        }
        int cr = writer.lastclassref++;
        writer.classref.put(type, cr);
        return cr;
    }
    @Override
    @SuppressWarnings({ "unchecked" })
    public final void serialize(Writer writer, Object object) throws IOException {
        OutputStream stream = writer.stream;
        Class<?> type = object.getClass();
        Integer cr = writer.classref.get(type);
        if (cr == null) {
            cr = writeClass(writer, type);
        }
        super.serialize(writer, object);
        stream.write(TagObject);
        ValueWriter.writeInt(stream, cr);
        stream.write(TagOpenbrace);
        writeObject(writer, object, type);
        stream.write(TagClosebrace);
    }
}
