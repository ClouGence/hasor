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
import net.hasor.utils.io.IOUtils;
import sun.misc.Cleaner;
import sun.nio.ch.DirectBuffer;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicLong;
/**
 * 文件格式为：0 到多个 Block 序列。读写支持 NIO，堆外内存和流两种方式。
 * 单个 Block 格式为：<blockSize 8-Byte> + <dataSize 8-Byte> + <data bytes n-Byte>
 * @version : 2018年5月7日
 * @author 赵永春 (zyc@hasor.net)
 */
public class BlockFileAdapter {
    private static final long             DEL_MASK       = 0x8000000000000000L;
    private static final long             NORMAL_MASK    = 0x7FFFFFFFFFFFFFFFL;
    private              File             blockFileName  = null;
    private              RandomAccessFile ioAccessFile   = null;
    private              FileChannel      isFileChannel  = null;
    private              AtomicLong       curentStreamID = null;
    private              Block            curentBlock    = null;
    //
    //
    public BlockFileAdapter(File blockFileName) throws IOException {
        this(blockFileName, 0);
    }
    public BlockFileAdapter(File blockFileName, int bufferSize) throws IOException {
        this.blockFileName = blockFileName;
        this.ioAccessFile = new RandomAccessFile(blockFileName, "rw");
        this.isFileChannel = this.ioAccessFile.getChannel();
        this.curentStreamID = new AtomicLong(0);
        this.curentBlock = null;
    }
    //
    /** 存储的文件 */
    public File getFile() {
        return this.blockFileName;
    }
    //
    /** 关闭 */
    public void close() throws IOException {
        this.ioClose();
    }
    //
    /** 文件大小 */
    public final long fileSize() throws IOException {
        return this.ioFileSize();
    }
    //
    /** 重置读取指针，curentBlock 会被置空。同时 nextBlock 会重头开始读文件 */
    public void reset() throws IOException {
        this.curentStreamID = new AtomicLong(0);
        this.curentBlock = null;
        ioSeekTo(0);
    }
    //
    /** 释放任何可能存在的，读写流操作 */
    public boolean releaseStream() {
        return this.releaseStream(this.curentStreamID.get());
    }
    private boolean releaseStream(long streamID) {
        if (streamID <= 0 || this.curentStreamID.get() != streamID) {
            return false;
        }
        return curentStreamID.compareAndSet(streamID, 0);
    }
    //
    /** 获取第一个Block */
    public Block firstBlock() throws IOException {
        this.ioSeekTo(0);
        this.curentBlock = null;
        return this.nextBlock();
    }
    //
    /** 获取用于表示文件尾的Block，基于这个 Block 进行写会以追加模式写入 */
    public Block eofBlock() throws IOException {
        long endPosition = this.fileSize();
        return new Block(endPosition, 0, -1, true, true);
    }
    //
    /** 基于 curentBlock 查找下一个最近的自由空间 */
    public Block findFreeSpace() throws IOException {
        return findFreeSpace(null);
    }
    //
    /** 基于 atBlock 查找下一个最近的自由空间 */
    public Block findFreeSpace(long blockPosition) throws IOException {
        //
        ioSeekTo(blockPosition);
        Block readBlock = readBlock();
        if (readBlock == null) {
            return null;
        }
        return findFreeSpace(readBlock);
    }
    //
    /** 基于 atBlock 查找下一个最近的自由空间 */
    public Block findFreeSpace(Block atBlock) throws IOException {
        // .curent Block is Free ?
        Block foundBlock = this.curentBlock();
        if (foundBlock != null && foundBlock.isInvalid()) {
            return foundBlock;
        }
        // .Looking for the next free Block until eof.
        while ((foundBlock = this.nextBlock(atBlock)) != null) {
            if (foundBlock.isInvalid()) {
                return foundBlock;
            }
        }
        // .eof as Free Block
        return this.eofBlock();
    }
    //
    /** 当前 Block 每一次调用 nextBlock 都会重置 curentBlock 为最新的 */
    public Block curentBlock() {
        return this.curentBlock;
    }
    //
    /** 基于 curentBlock 查找下一个Block */
    public Block nextBlock() throws IOException {
        return nextBlock(null);
    }
    /**  查找 blockPosition 位置之后的一个 Block */
    public Block nextBlock(long blockPosition) throws IOException {
        ioSeekTo(blockPosition);
        return nextBlock();
    }
    //
    /**  查找 atBlock 之后的一个 Block */
    public Block nextBlock(Block atBlock) throws IOException {
        // .release any curent stream
        this.releaseStream();
        //
        if (!hasNext()) {
            return null;
        }
        // . move pointer to ready.
        Block curentBlock = (atBlock != null) ? atBlock : this.curentBlock;
        if (curentBlock == null) {
            ioSeekTo(0);
        } else {
            long nextPoint = curentBlock.getPosition() + curentBlock.stiffBlockSize();
            ioSeekTo(nextPoint);
        }
        //
        // .read Block Header
        this.curentBlock = readBlock();
        return this.curentBlock();
    }
    private Block readBlock() throws IOException {
        if (this.ioFilePointer() == this.ioFileSize()) {
            return null;
        }
        //
        long blockPosition = this.ioFilePointer();
        long blockSize = this.ioReadLong();  // BlockSize（预读8个字节）
        long dataSize = this.ioReadLong();   // DataSize （预读8个字节）
        ioSeekTo(blockPosition);             // 回退预读的字节的指针
        //
        // .create structure of the Block
        boolean invalid = blockSize < 0;
        blockSize = blockSize & NORMAL_MASK;    // 去掉第一个二进制位变为真实长度
        dataSize = dataSize & NORMAL_MASK;      // 保证 dataSize 行为和 blockSize 一致
        return new Block(blockPosition, dataSize, blockSize, invalid, false);
    }
    //
    /**  查找 blockPosition 位置之后的一个 Block */
    public Block findBlock(long blockPosition) throws IOException {
        ioSeekTo(blockPosition);
        Block readBlock = readBlock();
        if (readBlock == null) {
            return null;
        }
        return readBlock();
    }
    /** 基于 refBlock 的信息，在当前 File 中查找对应的 Block */
    public Block findBlock(Block refBlock) throws IOException {
        long position = refBlock.getPosition();
        if (position + Block.HEAD_LENGTH > this.fileSize()) {
            return null; // 目标 Block 的 position 在 this file 中根本不成立
        }
        //
        ioSeekTo(position);
        Block readBlock = readBlock();
        if (readBlock == null) {
            return null;
        }
        if (readBlock.getBlockSize() == refBlock.getBlockSize() || refBlock.isEof()) {
            return readBlock; // 正常都要检测一下 BlockSize 是否一致已确定 Block 是同一个，eofBlock 除外。
        }
        return null;
    }
    //
    /** 判断是否具有下一个可以读取的 Block */
    public boolean hasNext() throws IOException {
        if (this.curentBlock != null) {
            return (this.curentBlock.getPosition() + this.curentBlock.stiffBlockSize()) != this.fileSize();
        }
        long filePointer = this.ioFilePointer();
        if (this.fileSize() == filePointer) {
            return false;
        }
        if (filePointer + Block.HEAD_LENGTH > this.fileSize()) {
            long errorSize = this.fileSize() - filePointer;
            throw new IOException("End-of-file has " + errorSize + " unknown bytes.");
        }
        return true;
    }
    //
    /** 获取 Block 的读入流（同一个时间内只能有一个流进行读写操作）*/
    public InputStream getInputStream(Block block) throws IOException {
        long streamID = System.currentTimeMillis();
        if (this.curentStreamID.compareAndSet(0, streamID)) {
            BlockInputStream inputStream = new BlockInputStream(streamID, block);
            inputStream.reset();
            return inputStream;
        }
        return null;
    }
    //
    /** 读取 Block 数据到 outStream（同一个时间内只能有一个流进行读写操作）*/
    public void readToStream(Block block, OutputStream outStream) throws IOException {
        InputStream inStream = this.getInputStream(block);
        if (inStream != null) {
            IOUtils.copyLarge(inStream, outStream);
            inStream.close();
            return;
        }
        throw new IOException("getInputStream failed.");
    }
    /** 删除 Block */
    public boolean deleteBlock(Block block) throws IOException {
        if (block == null) {
            return false;
        }
        // .重定向到目标Block
        ioSeekTo(block.getPosition());
        //
        // .原本要读取一个 long，但是由于第一位就表示是否删除，因此简化为只读第一个字节不用在读取整个length
        long blockSize = this.ioReadLong();  // BlockSize（预读8个字节）
        long dataSize = this.ioReadLong();   // DataSize （预读8个字节）
        if (blockSize < 0) {
            return false;
        }
        // .回退然后写入删除
        ioSeekTo(block.getPosition());
        ioWriteHeader(blockSize | DEL_MASK, 0);
        //
        ioSyncBuffer();
        return true;
    }
    //
    /** 拆分 originBlock 为两个部分，被拆分的 Block 一定是被删除的 */
    public Block[] splitBlock(Block originBlock, long splitPosition) throws IOException {
        if (splitPosition < 0) {
            throw new IOException("split position must be greater than zero.");
        }
        //
        Block block = this.findBlock(originBlock);
        if (block == null || !block.isInvalid()) {
            return null;
        }
        //
        long bSize = block.getBlockSize() - splitPosition - Block.HEAD_LENGTH;
        if (bSize < 0) {
            throw new IOException("split allowance is too small, must be greater than " + Block.HEAD_LENGTH);
        }
        //
        Block[] result = new Block[2];
        result[0] = new Block(block.getPosition(), 0, splitPosition, true, false);
        long bPosition = result[0].getPosition() + result[0].stiffBlockSize();
        result[1] = new Block(bPosition, 0, bSize, true, false);
        //
        // - 先写分裂的新 Block。如果此时写失败或程序中断原始分裂前的 Block 信息至少是完整的。
        ioSeekTo(result[1].getPosition());
        ioWriteHeader(result[1].getBlockSize() | DEL_MASK,//
                result[1].getDataSize() | DEL_MASK//
        );
        //
        ioSeekTo(result[0].getPosition());
        ioWriteHeader(result[0].getBlockSize() | DEL_MASK,//
                result[0].getDataSize() | DEL_MASK//
        );
        //
        ioSyncBuffer();
        return result;
    }
    //
    /** 安全的合并两个 Block ，被合并的 extendBlock 必须是删除状态 */
    public Block mergeBlock(Block block, Block extendBlock) throws IOException {
        block = this.findBlock(block);
        extendBlock = this.findBlock(extendBlock);
        if (block == null || extendBlock == null) {
            throw new IOException("bad block or extend Block.");
        }
        if (block.stiffBlockSize() + block.getPosition() != extendBlock.getPosition()) {
            throw new IOException("merge blocks must be continuous.");
        }
        if (block.isEof() || extendBlock.isEof()) {
            throw new IOException("merge blocks must not eof.");
        }
        if (!extendBlock.isInvalid()) {
            throw new IOException("extend Block is a valid data.");
        }
        //
        long dataSize = block.getDataSize();
        long blockSize = block.getBlockSize() + extendBlock.stiffBlockSize();
        boolean invalid = block.isInvalid();
        Block newBlock = new Block(block.getPosition(), dataSize, blockSize, invalid, false);
        //
        ioSeekTo(newBlock.getPosition());
        if (invalid) {
            ioWriteHeader(newBlock.getBlockSize() | DEL_MASK, 0);
        } else {
            ioWriteHeader(newBlock.getBlockSize() & NORMAL_MASK, dataSize);
        }
        //
        ioSyncBuffer();
        return newBlock;
    }
    //
    /** 半安全的批量合并操作。半安全是指该方法当合并多个 Block 过程中如果遇到意外，不会回滚整个操作。
     * 但是已经合并的 Block 都是安全的。*/
    public Block halfSafeMergeBlock(Block[] blockArrays) throws IOException {
        Block block = blockArrays[0];
        for (int i = 1; i < blockArrays.length; i++) {
            block = mergeBlock(block, blockArrays[i]);
        }
        return block;
    }
    //
    /** 获取某个 Block 的输出流，数据写完之后一定要 close，否则会产生数据丢失。 */
    public OutputStream getOutputStream(Block block) throws IOException {
        long streamID = System.currentTimeMillis();
        if (this.curentStreamID.compareAndSet(0, streamID)) {
            BlockOutputStream outStream = new BlockOutputStream(streamID, block);
            outStream.reset();
            return outStream;
        }
        return null;
    }
    //
    /** 输出数据到某个输出流上。输出完毕之后一定要 close，否则会产生数据丢失。 */
    public void writeFromStream(Block block, InputStream inStream) throws IOException {
        OutputStream outStream = this.getOutputStream(block);
        if (outStream != null) {
            IOUtils.copyLarge(inStream, outStream);
            outStream.flush();
            outStream.close();
            return;
        }
        throw new IOException("getOutputStream failed.");
    }
    //
    /** 获取一个nio的输入输出管道。 */
    public BlockChannel getBlockChannel(Block block) throws IOException {
        long streamID = System.currentTimeMillis();
        if (this.curentStreamID.compareAndSet(0, streamID)) {
            NioBlockChannel outStream = new NioBlockChannel(streamID, block);
            outStream.reset();
            return outStream;
        }
        throw new IOException("getOutputStream failed.");
    }
    //
    //
    /** -- */
    private class BlockOutputStream extends OutputStream {
        private long    streamID      = 0;
        private boolean isClose       = false;
        private Block   block         = null;
        private long    writePosition = 0;
        //
        public BlockOutputStream(long streamID, Block block) {
            this.streamID = streamID;
            this.block = block;
        }
        //
        public void reset() throws IOException {
            checkStreamClose(isClose, streamID);
            ioSeekTo(this.block.getPosition());
            if (block.isEof()) {
                ioExtendSize(Block.HEAD_LENGTH);
                ioWriteHeader(0, 0);
            } else {
                ioSkipBytes(Block.HEAD_LENGTH);//额外补偿 Block Head
            }
        }
        private void appendSize(int realSize) throws IOException {
            if (block.getBlockSize() >= 0) {
                long afterDataSize = this.writePosition + realSize;
                if (afterDataSize > block.getBlockSize()) {
                    throw new ArrayIndexOutOfBoundsException("write data size " + afterDataSize + " out of limit " + block.getBlockSize());
                }
            }
            if (block.isEof()) {
                ioExtendSize(realSize);
            }
            this.writePosition += realSize;
        }
        @Override
        public void write(int b) throws IOException {
            checkStreamClose(isClose, streamID);
            appendSize(4);
            write(new byte[] {                  //
                    (byte) ((b >>> 24) & 0xFF), //
                    (byte) ((b >>> 16) & 0xFF), //
                    (byte) ((b >>> 8) & 0xFF),  //
                    (byte) ((b >>> 0) & 0xFF)   //
            });
        }
        @Override
        public void write(byte[] b) throws IOException {
            this.write(b, 0, b.length);
        }
        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            checkStreamClose(isClose, streamID);
            appendSize(len);
            ioReadWrite(b, off, len);
        }
        @Override
        public void flush() throws IOException {
            ioSyncBuffer();
        }
        @Override
        public void close() throws IOException {
            checkStreamClose(isClose, streamID);
            /*--*/
            ioSeekTo(this.block.getPosition());
            long blockSize = this.block.getBlockSize();
            if (this.block.isEof()) {
                blockSize = this.writePosition;
            }
            ioWriteHeader(blockSize, this.writePosition);
            this.flush();
            /*--*/
            this.isClose = true;
            releaseStream(this.streamID);
        }
    }
    /** -- */
    private class BlockInputStream extends InputStream {
        private long    streamID      = 0;
        private boolean isClose       = false;
        private Block   block         = null;
        private long    position      = 0;
        private long    resetPosition = 0;
        //
        public BlockInputStream(long streamID, Block block) {
            this.streamID = streamID;
            this.block = block;
        }
        //
        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            checkStreamClose(isClose, streamID);
            if ((this.position + len) > this.block.getDataSize()) {
                len = (int) (this.block.getDataSize() - this.position);
            }
            if (len == 0) {
                return -1;
            }
            int readLength = ioReadBytes(b, off, len);
            this.position += readLength;
            return readLength;
        }
        @Override
        public int read() throws IOException {
            checkStreamClose(isClose, streamID);
            if (this.position >= this.block.getDataSize()) {
                return -1;
            }
            int data = ioReadByte();
            this.position++;
            return data;
        }
        @Override
        public long skip(long n) throws IOException {
            checkStreamClose(isClose, streamID);
            if ((this.position + n) > this.block.getDataSize()) {
                n = (int) (this.block.getDataSize() - this.position);
            }
            return skipBytes(n);
        }
        @Override
        public int available() throws IOException {
            checkStreamClose(isClose, streamID);
            long available = this.block.getDataSize() - position;
            if (available > Integer.MAX_VALUE) {
                // 为了避免 long 截断问题，返回最大不过 Integer.MAX_VALUE 的值
                return Integer.MAX_VALUE;
            }
            return (int) available;
        }
        @Override
        public void close() {
            this.isClose = true;
            releaseStream(this.streamID);
        }
        @Override
        public void reset() throws IOException {
            checkStreamClose(isClose, streamID);
            this.position = resetPosition;
            ioSeekTo(this.block.getPosition() + this.position);
            ioSkipBytes(Block.HEAD_LENGTH); //跳过 Block Head
        }
        @Override
        public void mark(int readlimit) {
            if ((this.position + readlimit) > this.block.getDataSize()) {
                readlimit = (int) (this.block.getDataSize() - this.position);
            }
            this.resetPosition = readlimit;
        }
        @Override
        public boolean markSupported() {
            return true;
        }
    }
    /** --*/
    private class NioBlockChannel extends BlockChannel {
        private long    streamID    = 0;
        private boolean isClose     = false;
        private Block   block       = null;
        private long    readerIndex = 0;
        private long    writerIndex = 0;
        public NioBlockChannel(long streamID, Block block) {
            this.streamID = streamID;
            this.block = block;
        }
        @Override
        public int read(ByteBuffer dst) throws IOException {
            checkStreamClose(isClose, streamID);
            //
            // .还可以读数据数
            long canReadSize = this.block.getBlockSize() - readerIndex;
            // .期待 Buffer 的 Limit 值
            long expectLimit = dst.position() + canReadSize;
            // .Buffer 真实还可以读取的字节数
            int realMaxReadSize = dst.capacity() - dst.position();
            //
            if (expectLimit >= realMaxReadSize) {
                expectLimit = realMaxReadSize + dst.position();
            }
            //
            // .调整预期读取最大字节数
            dst.limit((int) expectLimit);
            //
            int read = ioReadToByteBuffer(dst);
            this.readerIndex += read;
            return read;
        }
        @Override
        public int write(ByteBuffer src) throws IOException {
            checkStreamClose(isClose, streamID);
            //
            // .如果写入的数据过多那么抛异常（eof类型block 无上限）
            int expectWrite = src.capacity() - src.position();
            if (!this.block.isEof() && expectWrite > (this.block.getBlockSize() - writerIndex)) {
                throw new IndexOutOfBoundsException("Too much data can be written to.");
            }
            //
            try {
                int write = ioWriteToByteBuffer(src);
                writerIndex += write;
                return write;
            } finally {
                if (src.isDirect() && src instanceof DirectBuffer) {
                    Cleaner cleaner = ((DirectBuffer) src).cleaner();
                    if (cleaner != null) {
                        cleaner.clean();
                    }
                }
            }
        }
        public void reset() throws IOException {
            checkStreamClose(isClose, streamID);
            isFileChannel.position(block.getPosition() + Block.HEAD_LENGTH); //跳过 Block Head
        }
        @Override
        public boolean isOpen() {
            return isFileChannel.isOpen();
        }
        @Override
        public void close() throws IOException {
            checkStreamClose(isClose, streamID);
            /*--*/
            if (this.writerIndex > 0) {
                isFileChannel.position(this.block.getPosition());
                long blockSize = this.block.getBlockSize();
                if (this.block.isEof()) {
                    blockSize = this.writerIndex;
                }
                ioWriteHeader(blockSize, this.writerIndex);
            }
            /*--*/
            this.isClose = true;
            releaseStream(this.streamID);
        }
    }
    //
    private void checkStreamClose(boolean isClose, long streamID) throws IOException {
        // .关闭状态
        if (isClose) {
            throw new IOException("stream is closed.");
        }
        // .过期状态（在未调用close的情况下由 Adapter 触发的 releaseStream）
        //   - 处于过期状态下读取的数据完全是错误的，因为已经有一个新的流在占用 RandomAccessFile 了。
        if (streamID != curentStreamID.get()) {
            throw new IOException("stream has expired.");
        }
    }
    private long skipBytes(long readSize) throws IOException {
        long skipLength = 0;
        while (readSize > Integer.MAX_VALUE) {
            long realSkipBytes = this.ioSkipBytes((int) readSize);
            if (realSkipBytes == 0) {
                break;
            }
            skipLength += realSkipBytes;
            readSize = readSize - Integer.MAX_VALUE;
        }
        skipLength += this.ioSkipBytes((int) readSize);
        return skipLength;
    }
    //
    private int ioSkipBytes(int readSize) throws IOException {
        return this.ioAccessFile.skipBytes(readSize);
    }
    private void ioSeekTo(long atPosition) throws IOException {
        long nowPosition = this.ioAccessFile.getFilePointer();
        if (nowPosition == atPosition) {
            return;
        }
        this.ioAccessFile.seek(atPosition);
    }
    private long ioFileSize() throws IOException {
        return this.ioAccessFile.length();
    }
    private long ioFilePointer() throws IOException {
        return this.ioAccessFile.getFilePointer();
    }
    private long ioReadLong() throws IOException {
        return ioAccessFile.readLong();
    }
    private int ioReadByte() throws IOException {
        return ioAccessFile.readByte();
    }
    private int ioReadBytes(byte b[], int off, int len) throws IOException {
        return ioAccessFile.read(b, off, len);
    }
    private void ioReadWrite(byte b[], int off, int len) throws IOException {
        ioAccessFile.write(b, off, len);
    }
    private int ioReadToByteBuffer(ByteBuffer dst) throws IOException {
        return this.isFileChannel.read(dst);
    }
    private void ioWriteHeader(long blockSize, long dataSize) throws IOException {
        ioAccessFile.writeLong(blockSize);
        ioAccessFile.writeLong(dataSize);
    }
    private int ioWriteToByteBuffer(ByteBuffer dst) throws IOException {
        return this.isFileChannel.write(dst);
    }
    private void ioExtendSize(long addSize) throws IOException {
        ioAccessFile.setLength(ioAccessFile.length() + addSize);
    }
    private void ioClose() throws IOException {
        this.ioAccessFile.close();
    }
    private void ioSyncBuffer() throws IOException {
        //
    }
}