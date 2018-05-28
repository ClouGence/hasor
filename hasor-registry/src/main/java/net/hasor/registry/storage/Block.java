/*
 * Copyright 2008-2009 the original author or authors.
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
package net.hasor.registry.storage;
/**
 * 表示文件上的一个数据块
 * @version : 2015年8月19日
 * @author 赵永春 (zyc@hasor.net)
 */
public class Block {
    final static byte HEAD_LENGTH = 16;
    private long    position;   // 位置
    private long    dataSize;   // Data大小
    private long    blockSize;  // Block大小
    private boolean invalid;    // 是否失效
    private boolean atEof;      // 尾块
    //
    Block(long position, long dataSize, long blockSize, boolean invalid, boolean atEof) {
        this.position = position;
        this.dataSize = dataSize;
        this.blockSize = blockSize;
        this.invalid = invalid;
        this.atEof = atEof;
    }
    //
    @Override
    public String toString() {
        return "Block{position=" + this.position +//
                ", dataSize=" + this.dataSize + //
                ", blockSize=" + this.blockSize + //
                ", invalid=" + this.invalid + //
                ", eof=" + this.atEof + //
                '}';
    }
    public long stiffBlockSize() {
        return getBlockSize() + HEAD_LENGTH;
    }
    //
    public long getPosition() {
        return position;
    }
    public long getDataSize() {
        return dataSize;
    }
    public long getBlockSize() {
        return blockSize;
    }
    public boolean isInvalid() {
        return invalid;
    }
    public boolean isEof() {
        return atEof;
    }
}