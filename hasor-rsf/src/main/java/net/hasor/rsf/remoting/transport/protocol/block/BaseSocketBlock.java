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
package net.hasor.rsf.remoting.transport.protocol.block;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.more.util.ArrayUtils;
/**
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class BaseSocketBlock {
    public static final int NULL_Mark = 0x80000000;                           //
    public static int       MaxSize   = 16777215;                             //0x00FFFFFF
    private int[]           poolMap   = {};
    private ByteBuf         poolData  = ByteBufAllocator.DEFAULT.heapBuffer();
    //
    public void fillFrom(ByteBuf formData) {
        if (formData == null)
            return;
        this.poolData.writeBytes(formData);
    }
    public void fillTo(ByteBuf toData) {
        if (toData == null)
            return;
        toData.writeBytes(this.poolData);
    }
    /**添加请求参数。*/
    public short pushData(byte[] dataArray) {
        if (this.poolMap.length == MaxSize)
            throw new IndexOutOfBoundsException("max size is " + MaxSize);
        //
        int datalength = (dataArray == null) ? NULL_Mark : dataArray.length;
        this.poolMap = ArrayUtils.add(this.poolMap, datalength);
        //
        if (datalength > 0)
            this.poolData.writeBytes(dataArray);
        return (short) (this.poolMap.length - 1);
    }
    //
    /**添加池数据。*/
    public void addPoolData(int poolMapping) {
        this.poolMap = ArrayUtils.add(this.poolMap, poolMapping);
    }
    /**池长度*/
    public int getPoolLength() {
        return this.poolMap.length;
    }
    /**池大小*/
    public int getPoolSize() {
        int rawSize = 0;
        for (int i = 0; i < this.poolMap.length; i++) {
            int atPoolData = this.poolMap[i];
            if (atPoolData <= 0)
                continue;
            rawSize += this.poolMap[i];
        }
        return rawSize;
    }
    /**池数据*/
    public int[] getPoolData() {
        return this.poolMap;
    }
    /**内容所处起始位置*/
    public byte[] readPool(int attrIndex) {
        if (this.poolMap[attrIndex] == NULL_Mark) {
            return null;
        }
        //
        int rawIndex = 0;
        for (int i = 0; i < this.poolMap.length; i++) {
            if (i == attrIndex)
                break;
            if (this.poolMap[i] != NULL_Mark)
                rawIndex += this.poolMap[i];
        }
        int readLength = this.poolMap[attrIndex];//内容长度
        if (readLength == NULL_Mark)
            return null;
        //
        byte[] data = new byte[readLength];
        this.poolData.getBytes(rawIndex, data, 0, readLength);
        return data;
    }
}