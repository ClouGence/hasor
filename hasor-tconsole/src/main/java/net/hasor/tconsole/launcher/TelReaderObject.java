/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.tconsole.launcher;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import net.hasor.tconsole.TelReader;

import java.nio.charset.StandardCharsets;

/**
 * TelReader 的接口实现
 * @version : 2016年09月20日
 * @author 赵永春 (zyc@hasor.net)
 */
public class TelReaderObject implements TelReader {
    private ByteBuf byteBuf;            // 处理数据的 buffpool
    private ByteBuf dataReader;         // 数据源
    private int     lastExpectLength;

    public TelReaderObject(ByteBufAllocator byteBufAllocator, ByteBuf dataReader) {
        this.byteBuf = byteBufAllocator.compositeDirectBuffer();
        this.dataReader = dataReader;
    }

    @Override
    public boolean expectString(String waitString) {
        if (waitString.length() == 0) {
            return false;
        }
        this.lastExpectLength = TelUtils.waitString(this.byteBuf, waitString);
        if (this.lastExpectLength > -1) {
            this.byteBuf.skipBytes(this.lastExpectLength + waitString.length());
            return true;
        }
        return false;
    }

    // 读取数据
    public String removeReadData() {
        if (this.lastExpectLength > -1) {
            String dat = this.byteBuf.getCharSequence(0, this.lastExpectLength, StandardCharsets.UTF_8).toString();
            this.byteBuf.discardReadBytes();// 释放已读的Buffer区域
            this.lastExpectLength = -1;
            return dat;
        }
        return null;
    }

    // 把 dataReader copy 到 byteBuf，并重置 byteBuf 的读取索引
    public void update() {
        this.byteBuf.writeBytes(this.dataReader);
    }

    public void reset() {
        this.byteBuf.resetReaderIndex();
    }

    public void clear() {
        this.byteBuf.skipBytes(this.byteBuf.readableBytes());
        this.byteBuf.discardReadBytes();// 释放已读的Buffer区域。
    }

    public boolean isEof() {
        return this.byteBuf.readerIndex() == this.byteBuf.writerIndex();
    }

    public int getBuffSize() {
        return this.byteBuf.readableBytes();
    }
}