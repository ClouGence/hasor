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
package net.hasor.rsf.protocol.block;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import net.hasor.rsf.protocol.AbstractBlock;
import net.hasor.rsf.protocol.field.ParamField;
/**
 * 调用请求体
 * @version : 2014年9月20日
 * @author 赵永春(zyc@hasor.net)
 * @see net.hasor.rsf.general.ProtocolType#Request
 */
public class ReqBodyBlock extends AbstractBlock {
    private List<ParamField> paramObjectList = new ArrayList<ParamField>();
    //
    /**添加参数*/
    public int addParameter(byte[] paramData) {
        ParamField paramObject = new ParamField();
        paramObject.setRawData(paramData);
        this.paramObjectList.add(paramObject);
        return this.paramObjectList.indexOf(paramObject);
    }
    /**设置参数值*/
    public void addParameter(int index, byte[] paramData) {
        if (index > 0 && index < this.paramObjectList.size()) {
            this.paramObjectList.get(index).setRawData(paramData);
        }
    }
    public byte[][] getParameterData() {
        byte[][] rawData = new byte[paramObjectList.size()][0];
        int paramCount = this.paramObjectList.size();
        for (int i = 0; i < paramCount; i++) {
            rawData[i] = this.paramObjectList.get(i).getRawData();
        }
        return rawData;
    }
    //
    public void decode(ByteBuf buf) {
        //1.param count
        short paramCount = buf.readByte();
        //2.param ...
        for (int i = 0; i < paramCount; i++) {
            ParamField field = new ParamField();
            field.decode(buf);
            this.paramObjectList.add(field);
        }
    }
    public void encode(ByteBuf buf) {
        //1.param count
        int paramCount = this.paramObjectList.size();
        buf.writeByte(paramCount);
        //2.param ...
        for (int i = 0; i < paramCount; i++) {
            this.paramObjectList.get(i).encode(buf);
        }
    }
    public int size() {
        int finalSize = 0;
        finalSize += 1;
        for (ParamField field : paramObjectList) {
            finalSize += field.size();
        }
        return finalSize;
    }
}