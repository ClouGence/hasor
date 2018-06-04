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
package net.hasor.registry.storage.block;
import java.nio.ByteBuffer;
/**
 * 文件格式为：0 到多个 Block 序列。
 * 单个 Block 格式为：<blockSize 8-Byte> + <dataSize 8-Byte> + <data bytes n-Byte>
 * @version : 2018年5月7日
 * @author 赵永春 (zyc@hasor.net)
 */
public class BlockManager {
    public Block freeBlock(int dataLength) {
        return null;
    }
    public void writeData(Block atBlock, ByteBuffer writeBuffer) {
    }
}