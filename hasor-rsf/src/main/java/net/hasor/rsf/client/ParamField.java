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
package net.hasor.rsf.client;
import io.netty.buffer.ByteBuf;
import net.hasor.rsf.protocol.BlockSize;
/**
 * RSF数据字段
 * @version : 2014年9月20日
 * @author 赵永春(zyc@hasor.net)
 */
public class ParamField implements ProtocolCoder, BlockSize {
    private byte[] rawData = null;
    /**获取对象原始数据*/
    public byte[] getRawData() {
        return rawData;
    }
    /**写入对象原始数据*/
    public void setRawData(byte[] rawData) {
        if (rawData != null && rawData.length > 0x7FFFFFFF) {
            throw new IndexOutOfBoundsException();
        }
        this.rawData = rawData;
    }
    //
    //
    public void decode(ByteBuf buf) {
        int length = buf.readInt();
        boolean isNull = (length | NULL_Int) == length;
        //
        if (isNull == true) {
            this.rawData = null;
        } else {
            this.rawData = buf.readBytes(length).array();
        }
    }
    public void encode(ByteBuf buf) {
        if (this.rawData == null) {
            buf.writeInt(NULL_Int);
            return;
        }
        buf.writeInt(this.rawData.length);
        buf.writeBytes(this.rawData);
    }
    public int size() {
        int dataLength = (this.rawData == null) ? 0 : (this.rawData.length);
        return 4 + dataLength;
    }
}