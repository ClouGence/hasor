package net.hasor.registry.storage;
/** 表示一个数据块 */
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