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
 * Telnet指令执行器。
 * @version : 20169年09月20日
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
        int waitLength = waitString.length();
        String dat = null;
        while (this.byteBuf.readableBytes() > 0) {
            dat = this.byteBuf.readCharSequence(waitLength, StandardCharsets.UTF_8).toString();
            if (dat.equals("")) {
                break;
            }
            if (dat.equals(waitString)) {
                this.lastExpectLength = waitLength;
                return true;
            }
        }
        this.lastExpectLength = 0;
        return false;
    }

    // 读取数据
    public String removeReadData() {
        int readCount = this.lastExpectLength;
        if (readCount > 0) {
            String dat = this.byteBuf.getCharSequence(0, this.byteBuf.readerIndex(), StandardCharsets.UTF_8).toString();
            this.byteBuf.discardReadBytes();// 释放已读的Buffer区域
            this.lastExpectLength = 0;
            return dat.substring(0, dat.length() - readCount);
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