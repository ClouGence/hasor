package net.hasor.registry.storage;
import net.hasor.utils.IOUtils;

import java.io.*;
import java.util.concurrent.atomic.AtomicLong;
//
//
public class BlockFileAdapter {
    private RandomAccessFile randomAccessFile = null;
    private AtomicLong       curentStreamID   = null;
    private Block            curentBlock      = null;
    //
    //
    public BlockFileAdapter(File fileName) throws IOException {
        this.randomAccessFile = new RandomAccessFile(fileName, "rw");
        this.curentStreamID = new AtomicLong(0);
        this.curentBlock = null;
    }
    //
    public void close() throws IOException {
        this.randomAccessFile.close();
    }
    //
    public void reset() throws IOException {
        this.curentStreamID = new AtomicLong(0);
        this.curentBlock = null;
        seekTo(0, this.randomAccessFile);
    }
    //
    public final long fileSize() throws IOException {
        return this.randomAccessFile.length();
    }
    private final long getFilePointer() throws IOException {
        return this.randomAccessFile.getFilePointer();
    }
    //
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
    public Block endBlock() throws IOException {
        long endPosition = this.fileSize();
        return new Block(endPosition, 0, -1, true, true);
    }
    //
    public Block findFreeSpace() throws IOException {
        return findFreeSpace(null);
    }
    //
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
        return this.endBlock();
    }
    //
    public Block curentBlock() {
        return this.curentBlock;
    }
    //
    public Block nextBlock() throws IOException {
        return nextBlock(null);
    }
    //
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
            seekTo(0, this.randomAccessFile);
        } else {
            long nextPoint = curentBlock.getPosition() + curentBlock.stiffBlockSize();
            seekTo(nextPoint, this.randomAccessFile);
        }
        //
        // .read Block Header
        this.curentBlock = readBlock();
        return this.curentBlock();
    }
    private Block readBlock() throws IOException {
        long blockPosition = this.getFilePointer();
        long blockSize = this.randomAccessFile.readLong();  // BlockSize（预读8个字节）
        long dataSize = this.randomAccessFile.readLong();   // DataSize （预读8个字节）
        seekTo(blockPosition, this.randomAccessFile);       // 回退预读的字节的指针
        //
        // .create structure of the Block
        boolean invalid = blockSize < 0;
        blockSize = blockSize & 0x7FFFFFFFFFFFFFFFL;    // 去掉第一个二进制位变为真实长度
        dataSize = dataSize & 0x7FFFFFFFFFFFFFFFL;      // 保证 dataSize 行为和 blockSize 一致
        return new Block(blockPosition, dataSize, blockSize, invalid, false);
    }
    //
    public Block findBlock(Block refBlock) throws IOException {
        long position = refBlock.getPosition();
        if (position + Block.HEAD_LENGTH > this.fileSize()) {
            return null; // 目标 Block 的 position 在 this file 中根本不成立
        }
        //
        seekTo(position, this.randomAccessFile);
        Block readBlock = readBlock();
        if (readBlock.getBlockSize() == refBlock.getBlockSize() || refBlock.isEof()) {
            return readBlock; // 正常都要检测一下 BlockSize 是否一致已确定 Block 是同一个，eofBlock 除外。
        }
        return null;
    }
    //
    public boolean hasNext() throws IOException {
        if (this.curentBlock != null) {
            return (this.curentBlock.getPosition() + this.curentBlock.stiffBlockSize()) != this.fileSize();
        }
        long filePointer = this.getFilePointer();
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
    public void readToStream(Block block, OutputStream outStream) throws IOException {
        InputStream inStream = this.getInputStream(block);
        if (inStream != null) {
            IOUtils.copyLarge(inStream, outStream);
            inStream.close();
            return;
        }
        throw new IOException("getInputStream failed.");
    }
    //
    public boolean deleteBlock(Block block) throws IOException {
        if (block == null) {
            return false;
        }
        // .重定向到目标Block
        seekTo(block.getPosition(), this.randomAccessFile);
        //
        // .原本要读取一个 long，但是由于第一位就表示是否删除，因此简化为只读第一个字节不用在读取整个length
        byte flagBit = this.randomAccessFile.readByte();
        if (flagBit < 0) {
            return false;
        }
        // .回退一个字节然后写入删除标记
        seekTo(block.getPosition(), this.randomAccessFile);
        this.randomAccessFile.writeByte(flagBit | (byte) 0x80);
        //
        return true;
    }
    //
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
    //
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
            checkClose(isClose, streamID);
            randomAccessFile.seek(this.block.getPosition());
            if (block.isEof()) {
                randomAccessFile.setLength(randomAccessFile.length() + Block.HEAD_LENGTH);
                randomAccessFile.writeLong(0);
                randomAccessFile.writeLong(0);
            } else {
                randomAccessFile.skipBytes(Block.HEAD_LENGTH); //额外补偿 Block Head
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
                randomAccessFile.setLength(randomAccessFile.length() + realSize);
            }
            this.writePosition += realSize;
        }
        @Override
        public void write(int b) throws IOException {
            checkClose(isClose, streamID);
            appendSize(4);
            randomAccessFile.writeInt(b);
        }
        @Override
        public void write(byte[] b) throws IOException {
            this.write(b, 0, b.length);
        }
        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            checkClose(isClose, streamID);
            appendSize(len);
            randomAccessFile.write(b, off, len);
        }
        @Override
        public void flush() throws IOException {
            //
        }
        @Override
        public void close() throws IOException {
            checkClose(isClose, streamID);
            this.flush();
            /*--*/
            randomAccessFile.seek(this.block.getPosition());
            long blockSize = this.block.getBlockSize();
            if (this.block.isEof()) {
                blockSize = this.writePosition;
            }
            randomAccessFile.writeLong(blockSize);          // BlockSize
            randomAccessFile.writeLong(this.writePosition); // DataSize
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
            checkClose(isClose, streamID);
            if ((this.position + len) > this.block.getDataSize()) {
                len = (int) (this.block.getDataSize() - this.position);
            }
            if (len == 0) {
                return -1;
            }
            int readLength = randomAccessFile.read(b, off, len);
            this.position += readLength;
            return readLength;
        }
        @Override
        public int read() throws IOException {
            checkClose(isClose, streamID);
            if (this.position >= this.block.getDataSize()) {
                return -1;
            }
            int data = randomAccessFile.readByte();
            this.position++;
            return data;
        }
        @Override
        public long skip(long n) throws IOException {
            checkClose(isClose, streamID);
            if ((this.position + n) > this.block.getDataSize()) {
                n = (int) (this.block.getDataSize() - this.position);
            }
            return skipBytes(n, randomAccessFile);
        }
        @Override
        public int available() throws IOException {
            checkClose(isClose, streamID);
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
            checkClose(isClose, streamID);
            this.position = resetPosition;
            randomAccessFile.seek(this.block.getPosition() + this.position);
            randomAccessFile.skipBytes(Block.HEAD_LENGTH); //跳过 Block Head
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
    //
    //
    private void checkClose(boolean isClose, long streamID) throws IOException {
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
    private static long skipBytes(long readSize, RandomAccessFile fis) throws IOException {
        long skipLength = 0;
        while (readSize > Integer.MAX_VALUE) {
            long realSkipBytes = fis.skipBytes((int) readSize);
            if (realSkipBytes == 0) {
                break;
            }
            skipLength += realSkipBytes;
            readSize = readSize - Integer.MAX_VALUE;
        }
        skipLength += fis.skipBytes((int) readSize);
        return skipLength;
    }
    private static void seekTo(long atPosition, RandomAccessFile fis) throws IOException {
        long nowPosition = fis.getFilePointer();
        if (nowPosition == atPosition) {
            return;
        }
        fis.seek(atPosition);
    }
}