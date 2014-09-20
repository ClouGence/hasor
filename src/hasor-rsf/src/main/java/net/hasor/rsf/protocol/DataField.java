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
import net.hasor.rsf.general.RSFConstants;
import org.more.util.ByteUtils;
import org.more.util.StringUtils;
/**
 * RSF数据字段
 * @version : 2014年9月20日
 * @author 赵永春(zyc@hasor.net)
 */
public class DataField implements ProtocolCode {
    private String value = null;
    /**获取Value*/
    public String getValue() {
        return StringUtils.isBlank(this.value) ? null : this.value;
    }
    /**设置数据字段的值*/
    public void setValue(String newValue) throws IndexOutOfBoundsException {
        newValue = StringUtils.isBlank(newValue) ? "" : newValue;
        int testLength = newValue.getBytes(RSFConstants.DEFAULT_CHARSET).length;
        /*协议规定最大65535个字节*/
        if (testLength >= 0xFFFF) {
            throw new IndexOutOfBoundsException();
        }
        this.value = newValue;
    }
    //
    //
    public void decode(ByteBuf buf) throws Throwable {
        int length = buf.readBytes(2).readInt();
        if (length != 0) {
            this.value = buf.readBytes(length).toString(RSFConstants.DEFAULT_CHARSET);
        }
    }
    public void encode(ByteBuf buf) throws Throwable {
        String val = this.getValue();
        if (val == null) {
            buf.writeByte(0);
            return;
        }
        /*写数据*/
        byte[] dataByte = val.getBytes(RSFConstants.DEFAULT_CHARSET);
        byte[] lengByte = ByteUtils.toByteArray(dataByte.length, 2);
        buf.writeBytes(lengByte);
        buf.writeBytes(dataByte);
    }
}