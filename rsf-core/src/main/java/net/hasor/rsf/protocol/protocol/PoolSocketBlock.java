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
package net.hasor.rsf.protocol.protocol;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.more.util.ArrayUtils;
/**
 * 池上限为 0~4095条数据，单条数据最大约16MB。
 * 下面是数据格式：<pre>
 * ---------------------------------bytes =6 ~ 8192
 * byte[2]  attrPool-size (0~4095)  池大小 0x0FFF
 *     byte[4] att-length           属性1大小
 *     byte[4] att-length           属性2大小
 *     ...
 * ---------------------------------bytes =n
 * dataBody                         数据内容
 *     bytes[...]
 * </pre>
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class PoolSocketBlock {
    public static final int NULL_MARK   = 0xFFFFFFFF; //表示NULL
    public static int       DataMaxSize = 0x00FFFFFF; //单条数据最大约16MB
    public static short     PoolMaxSize = 0x0FFF;    //池上限为 0~4095条
    private int[]           poolMap     = {};
    private ByteBuf         poolData    = null;
    //
    public PoolSocketBlock() {
        poolData = ByteBufAllocator.DEFAULT.heapBuffer();
    }
    public void fillFrom(ByteBuf formData) {
        if (formData == null) {
            return;
        }
        //
        short attrPoolSize = (short) (PoolMaxSize & formData.readShort());
        for (int i = 0; i < attrPoolSize; i++) {
            int length = formData.readInt();
            this.poolMap = ArrayUtils.add(this.poolMap, length);
        }
        this.poolData.writeBytes(formData);
    }
    public void fillTo(ByteBuf toData) {
        if (toData == null)
            return;
        //
        toData.writeShort(poolMap.length);
        for (int i = 0; i < poolMap.length; i++) {
            toData.writeInt(poolMap[i]);
        }
        toData.writeBytes(this.poolData);
    }
    //
    /**添加请求参数。*/
    public short pushData(byte[] dataArray) {
        if (this.poolMap.length >= PoolMaxSize) {
            throw new IndexOutOfBoundsException("poolMax size is " + PoolMaxSize);
        }
        //
        int datalength = (dataArray == null) ? NULL_MARK : dataArray.length;
        this.poolMap = ArrayUtils.add(this.poolMap, datalength);
        if (datalength > 0) {
            this.poolData.writeBytes(dataArray);
        }
        return (short) (this.poolMap.length - 1);
    }
    //
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
    public byte[] readPool(short attrIndex) {
        if (this.poolMap[attrIndex] == NULL_MARK) {
            return null;
        }
        //
        int rawIndex = 0;
        for (int i = 0; i < this.poolMap.length; i++) {
            if (i == attrIndex)
                break;
            if (this.poolMap[i] != NULL_MARK)
                rawIndex += this.poolMap[i];
        }
        int readLength = this.poolMap[attrIndex];//内容长度
        if (readLength == NULL_MARK)
            return null;
        //
        byte[] data = new byte[readLength];
        this.poolData.getBytes(rawIndex, data, 0, readLength);
        return data;
    }
}