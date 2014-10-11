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
package net.hasor.rsf.protocol.field;
import io.netty.buffer.ByteBuf;
import net.hasor.rsf.general.RSFConstants;
import net.hasor.rsf.protocol.BlockSize;
import net.hasor.rsf.protocol.ProtocolCoder;
import org.more.util.StringUtils;
/**
 * RSF数据字段，最多可以携带（32767个字节）
 * @version : 2014年9月20日
 * @author 赵永春(zyc@hasor.net)
 */
public class DataField implements ProtocolCoder, BlockSize {
    private byte[] rawData = null;
    //
    /**获取Value*/
    public String getValue() {
        if (this.rawData == null)
            return null;
        return new String(this.rawData, RSFConstants.DEFAULT_CHARSET);
    }
    /**设置数据字段的值*/
    public void setValue(String newValue) throws IndexOutOfBoundsException {
        if (newValue == null) {
            this.rawData = null;
            return;
        }
        newValue = StringUtils.isBlank(newValue) ? "" : newValue;
        byte[] newByte = newValue.getBytes(RSFConstants.DEFAULT_CHARSET);
        /*协议规定，长度位于0~32767*/
        if (newByte.length >= 0x7FFF) {
            throw new IndexOutOfBoundsException();
        }
        this.rawData = newByte;
    }
    //
    public void decode(ByteBuf buf) {
        short length = buf.readShort();
        boolean isNull = (length | NULL_Short) == length;
        //
        if (isNull == true) {
            this.rawData = null;
        } else {
            this.rawData = buf.readBytes(length).array();
        }
    }
    public void encode(ByteBuf buf) {
        if (this.rawData == null) {
            buf.writeShort(NULL_Short);
            return;
        }
        buf.writeShort(this.rawData.length);
        buf.writeBytes(this.rawData);
    }
    public int size() {
        int dataLength = (this.rawData == null) ? 0 : (this.rawData.length);
        return 2 + dataLength;
    }
}