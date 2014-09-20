/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.rsf.protocol;
import io.netty.buffer.ByteBuf;
import net.hasor.rsf.serialize.Decoder;
import net.hasor.rsf.serialize.Encoder;
import net.hasor.rsf.serialize.SerializeFactory;
import org.more.util.ByteUtils;
/**
 * RSF数据字段
 * @version : 2014年9月20日
 * @author 赵永春(zyc@hasor.net)
 */
public class ParamField implements ProtocolCode {
    private int    index         = 0;
    private String serializeType = null;
    private byte[] oriData       = new byte[0];
    //
    //
    public ParamField(int index, String serializeType) {
        this.index = index;
        this.serializeType = serializeType;
    }
    /**获取参数位置*/
    public int getIndex() {
        return this.index;
    }
    /**获取参数使用的序列化方式*/
    public String getSerializeType() {
        return this.serializeType;
    }
    /**读取参数字段的对象*/
    public Object readObject(SerializeFactory factory) throws Throwable {
        Decoder decoder = factory.getDecoder(this.serializeType);
        return decoder.decode(this.oriData);
    }
    /**写入参数字段的对象*/
    public void writeObject(Object object, SerializeFactory factory) throws Throwable {
        Encoder encoder = factory.getEncoder(this.serializeType);
        byte[] newData = encoder.encode(object);
        if (newData.length > 0xFFFFFFFF) {
            throw new IndexOutOfBoundsException();
        }
        this.oriData = newData;
    }
    //
    //
    public void decode(ByteBuf buf) throws Throwable {
        int dataLength = buf.readBytes(4).readInt();
        if (dataLength == 0) {
            this.oriData = new byte[0];
            return;
        }
        this.oriData = buf.readBytes(dataLength).array();
    }
    public void encode(ByteBuf buf) throws Throwable {
        int dataLength = this.oriData.length;
        /*写数据*/
        byte[] lengByte = ByteUtils.toByteArray(dataLength, 4);
        buf.writeBytes(lengByte);
        buf.writeBytes(this.oriData);
    }
}