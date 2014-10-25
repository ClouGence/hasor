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
import net.hasor.rsf.protocol.AbstractBlock;
import net.hasor.rsf.protocol.field.DataField;
import net.hasor.rsf.protocol.field.ParamField;
/**
 * 响应信息体
 * @version : 2014年9月20日
 * @author 赵永春(zyc@hasor.net)
 * @see net.hasor.rsf.general.ProtocolType#Response
 */
public class ResBodyBlock extends AbstractBlock {
    private DataField  returnType = new DataField();
    private ParamField returnData = new ParamField();
    //
    /**获取返回值类型*/
    public String getReturnType() {
        return this.returnType.getValue();
    }
    /**设置返回值类型*/
    public void setReturnType(String returnType) {
        this.returnType.setValue(returnType);
    }
    /**获取要返回的值*/
    public byte[] getReturnData() throws Throwable {
        if (this.returnData == null) {
            return null;
        }
        return this.returnData.getRawData();
    }
    /**设置要返回的值*/
    public void setReturnData(byte[] rawData) throws Throwable {
        if (this.returnData == null) {
            this.returnData = new ParamField();
        }
        this.returnData.setRawData(rawData);;
    }
    //
    public void decode(ByteBuf buf) {
        this.returnData = new ParamField();
        this.returnData.decode(buf);
    }
    public void encode(ByteBuf buf) {
        if (this.returnData == null) {
            this.returnData = new ParamField();
        }
        this.returnData.encode(buf);
    }
    public int size() {
        int finalSize = this.returnData.size();
        return finalSize;
    }
}