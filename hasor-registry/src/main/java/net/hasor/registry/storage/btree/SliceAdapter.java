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
package net.hasor.registry.storage.btree;
import net.hasor.registry.storage.NoDataException;
import net.hasor.registry.storage.block.Block;
import net.hasor.registry.storage.block.BlockChannel;
import net.hasor.registry.storage.block.BlockFileAdapter;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/**
 * B-Tree 索引操作类
 * @version : 2018年5月28日
 * @author 赵永春 (zyc@hasor.net)
 */
public class SliceAdapter {
    protected static Logger           logger = LoggerFactory.getLogger(SliceAdapter.class);
    private          int              entryPoint;           // 入口点
    private          ByteBuffer       slicePool;            // slice 使用状态池，所有 bit状态位的总数等于 maxEchoSequence
    private          ByteBuffer       slicePositionPool;    // slice 数据块所在真实位置
    //
    private          BlockFileAdapter fileAdapter;          //
    private          int              sliceSize;            // 切块大小
    private          int              maxEchoSequence;      // slice 编号分配采用自旋序列，这个值是旋转的最大上限
    private          int              echoSequence;         // 当前自旋值
    //
    //
    //
    private SliceAdapter() {
    }
    /** 初始化一个全新的索引 */
    public static SliceAdapter initIndex(int sliceSize, int sliceAreaSize, BlockFileAdapter fileAdapter) throws IOException {
        if (sliceSize < 3 || sliceAreaSize < 1) {
            throw new IndexOutOfBoundsException("sliceSize < 3 or sliceAreaSize < 1");
        }
        if (fileAdapter.fileSize() > 0) {
            throw new IndexOutOfBoundsException("file is not empty.");
        }
        //
        // 根据参数创建 SliceAdapter
        SliceAdapter adapter = new SliceAdapter();
        adapter.sliceSize = sliceSize;
        adapter.echoSequence = 0;
        adapter.maxEchoSequence = sliceAreaSize * 8;
        int slicePositionBufferSize = adapter.maxEchoSequence * 8; // all of slice position
        logger.info("realPoolSize= " + sliceAreaSize + " ,echoSequence=" + adapter.maxEchoSequence + " ,slicePositionBufferSize=" + slicePositionBufferSize);
        adapter.slicePositionPool = ByteBuffer.allocate(slicePositionBufferSize);
        adapter.slicePool = ByteBuffer.allocate(sliceAreaSize);
        for (int i = 0; i < adapter.slicePool.capacity(); i++) {
            adapter.slicePool.put(i, (byte) -1);
        }
        adapter.entryPoint = -1;
        adapter.fileAdapter = fileAdapter;
        adapter.submit();
        return adapter;
    }
    /** 从文件中读取一个索引 */
    public static SliceAdapter loadIndex(BlockFileAdapter fileAdapter) throws IOException {
        Block headerBlock = fileAdapter.firstBlock();
        if (headerBlock == null) {
            throw new IOException("cannot load empty file.");
        }
        //
        BlockChannel blockChannel = fileAdapter.getBlockChannel(headerBlock);
        ByteBuffer headBuffer = ByteBuffer.allocate(10);
        int readSize = blockChannel.read(headBuffer);
        if (readSize != 10) {
            throw new IOException("bad index header .");
        }
        //
        headBuffer.flip();
        int sliceSize = headBuffer.getShort();              // 2 bytes - Slice 切分大小
        int entryPoint = headBuffer.getInt();               // 4 bytes - Btree 的树根 SliceID
        int splitStatusBytesLength = headBuffer.getInt();   // 4 bytes - 状态池字节大小
        //
        ByteBuffer slicePoolBuf = ByteBuffer.allocate(splitStatusBytesLength);
        blockChannel.read(slicePoolBuf);
        //
        SliceAdapter adapter = new SliceAdapter();
        adapter.entryPoint = entryPoint;
        adapter.sliceSize = sliceSize;
        adapter.slicePool = slicePoolBuf;
        adapter.maxEchoSequence = splitStatusBytesLength * 8;// 序列总数等于这些字节的总共二进制位数
        adapter.echoSequence = 0;
        //
        // slice 数据真实位置使用一个 long 存储，总长度等于 maxEchoSequence 乘 8
        adapter.slicePositionPool = ByteBuffer.allocate(adapter.maxEchoSequence * 8);
        int readSize2 = blockChannel.read(adapter.slicePositionPool);
        if (readSize2 != adapter.slicePositionPool.capacity()) {
            throw new IOException("file is damaged.");
        }
        blockChannel.close();
        //
        adapter.fileAdapter = fileAdapter;
        return adapter;
    }
    public void close() throws IOException {
        this.fileAdapter.close();
    }
    //
    //
    /**递交Header存储到磁盘上*/
    public void submit() throws IOException {
        //
        // 判断 headerVersion 来确定，主次版本
        Block block = this.fileAdapter.firstBlock();
        if (block == null) {
            block = this.fileAdapter.eofBlock();
        }
        BlockChannel channel = this.fileAdapter.getBlockChannel(block);
        //
        ByteBuffer submitData = ByteBuffer.allocate(10);
        submitData.putShort((short) this.sliceSize);    // 2 bytes - Slice 切分大小
        submitData.putInt(this.entryPoint);             // 4 bytes - Btree 的树根 SliceID
        submitData.putInt(this.slicePool.capacity());   // 4 bytes - 状态池字节大小
        submitData.flip();
        //
        ByteBuffer dupSlicePool = this.slicePool.duplicate();
        ByteBuffer dupPositionPool = this.slicePositionPool.duplicate();
        dupSlicePool.flip();
        dupSlicePool.limit(dupSlicePool.capacity());
        dupPositionPool.flip();
        dupPositionPool.limit(dupPositionPool.capacity());
        //
        channel.write(submitData);
        channel.write(dupSlicePool);
        channel.write(dupPositionPool);
        channel.close();
        //
        try {
            for (Long blockPosition : this.deleteBlock) {
                Block delBlock = this.fileAdapter.findBlock(blockPosition);
                this.fileAdapter.deleteBlock(delBlock);
            }
        } finally {
            this.deleteBlock.clear();
        }
    }
    //
    //
    //
    //
    //
    // ------------------------------------------------------------------------------------------------------
    private List<Long> deleteBlock = new ArrayList<Long>(100);
    private void deleteBlock(long position) {
        this.deleteBlock.add(position);
    }
    private ByteBuffer readBlock(long position) {
        try {
            Block block = this.fileAdapter.findBlock(position);
            if (block == null) {
                throw new NoDataException("position " + position + " has no any Block");
            }
            if (block.isInvalid()) {
                throw new NoDataException("position " + position + " data is invalid.");
            }
            //
            BlockChannel blockChannel = this.fileAdapter.getBlockChannel(block);
            ByteBuffer allocate = ByteBuffer.allocate((int) block.getDataSize());
            blockChannel.read(allocate);
            blockChannel.close();
            return allocate;
            //
        } catch (Exception e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }
    private long writeBlock(ByteBuffer dataBufer) {
        try {
            dataBufer.flip();
            Block freeSpace = this.fileAdapter.eofBlock();
            BlockChannel blockChannel = this.fileAdapter.getBlockChannel(freeSpace);
            blockChannel.write(dataBufer);
            blockChannel.close();
            //
            return freeSpace.getPosition();
        } catch (Throwable e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }
    //
    // ------------------------------------------------------------------------------------------------------
    /*分配一个 Slice ID*/
    private int requestNewSliceID() {
        int counter = 0;
        while (true) {
            if (this.echoSequence >= maxEchoSequence) {
                this.echoSequence = 0;
            }
            int curSequence = this.echoSequence++;
            int byteIndex = curSequence / 8;
            int bitIndex = curSequence % 8;
            byte byteValue = this.slicePool.get(byteIndex);
            //
            if (byteValue == (byte) (byteValue | (1 << bitIndex))) {
                this.slicePool.put(byteIndex, (byte) (byteValue & ~(1 << bitIndex)));
                return curSequence;
            }
            // .如果计数器到了上限，那么抛出异常：没有富裕的 Slice
            counter++;
            if (counter >= maxEchoSequence) {
                throw new RuntimeException("no rich Slice.");
            }
        }
    }
    /*释放一个 Slice ID*/
    private void releaseSlice(int sliceID) {
        // .释放SliceID
        int byteIndex = sliceID / 8;
        int bitIndex = sliceID % 8;
        byte byteValue = this.slicePool.get(byteIndex);
        this.slicePool.put(byteIndex, (byte) (byteValue | (1 << bitIndex)));
        // .删除区块
        long position = this.getSlicePosition(sliceID);
        this.deleteBlock(position);
    }
    private long getSlicePosition(int sliceID) {
        return this.slicePositionPool.getLong(sliceID * 8);
    }
    private void setSlicePosition(int sliceID, long slicePosition) {
        this.slicePositionPool.putLong(sliceID * 8, slicePosition);
    }
    //
    // ------------------------------------------------------------------------------------------------------
    /*获取跟 Slice */
    private Slice getEntryPoint() {
        if (entryPoint != -1) {
            return getSlice(entryPoint);
        }
        //
        Slice root = new Slice();
        root.setSliceID(requestNewSliceID());
        root.setChildrensKeys(new Node[] {});
        this.storeSlice(root);
        this.updataEntryPoint(root.getSliceID());
        //
        return root;
    }
    private void updataEntryPoint(int entryPoint) {
        this.entryPoint = entryPoint;
    }
    /* 根据 sliceID 获取 Slice */
    protected Slice getSlice(int sliceID) {
        // .根据 sliceID 计算slicePosition位置，然后取出 slice 的 Position
        long positionLong = this.getSlicePosition(sliceID);
        ByteBuffer byteBuffer = this.readBlock(positionLong);
        if (byteBuffer == null) {
            return null;
        }
        //
        byteBuffer.flip();
        int dat_sliceID = byteBuffer.getInt();
        Slice slice = new Slice();
        slice.setSliceID(dat_sliceID);
        int dataLength = byteBuffer.getShort();
        //
        Node[] dataArray = new Node[dataLength];
        for (int i = 0; i < dataLength; i++) {
            long dataKey = byteBuffer.getLong();
            long dataPosition = byteBuffer.getLong();
            if (dataPosition < 0) {
                dataArray[i] = new TreeNode(dataKey, (int) (dataPosition & 0x7FFFFFFFFFFFFFFFL));
            } else {
                dataArray[i] = new DataNode(dataKey);
                dataArray[i].setPosition(dataPosition);
            }
        }
        slice.setChildrensKeys(dataArray);
        return slice;
    }
    /* 保存 Slice 返回数据存储的位置 */
    protected long storeSlice(Slice sliceData) {
        // .计算 Slice 所需长度 (最大不过 512K)
        int dataLength = sliceData.getChildrensKeys().length * 16 + 6; // Node length is long + long = 8 + 8
        //
        // .写数据到 Buffer
        ByteBuffer writeBuffer = ByteBuffer.allocate(dataLength);
        writeBuffer.putInt(sliceData.getSliceID());
        writeBuffer.putShort((short) sliceData.getChildrensKeys().length);
        //
        for (Node node : sliceData.getChildrensKeys()) {
            long dataKey = node.getDataKey();
            long dataPosition = node.getPosition();
            if (!node.isData()) {
                dataPosition = 0x8000000000000000L | dataPosition;
            }
            writeBuffer.putLong(dataKey);
            writeBuffer.putLong(dataPosition);
        }
        // .保存到 Block ,并更新索引元信息
        long dataPosition = this.writeBlock(writeBuffer);
        this.setSlicePosition(sliceData.getSliceID(), dataPosition);
        //
        return sliceData.getSliceID();
    }
    //
    //
    // -最近的（可操作）
    public ResultSlice nearSlice(long hashKey) {
        Slice slice = this.getEntryPoint();
        return nearSlice(hashKey, slice, 0, -1);
    }
    private ResultSlice nearSlice(long hashKey, Slice atSlice, int positionOfatSlice, int parentSliceID) {
        //
        // .已经是叶子节点了，不可能在遍历了
        Node[] childrensNodes = atSlice.getChildrensKeys();
        if (childrensNodes.length == 0) {
            return new ResultSlice(parentSliceID, atSlice, positionOfatSlice);
        }
        //
        for (int i = 0; i < childrensNodes.length; i++) {
            Node itemNode = childrensNodes[i];
            // 小于itemKey，证明 hashKey 应该放在这个分支下
            if (hashKey < itemNode.getDataKey()) {
                if (!itemNode.isData()) {
                    Slice itemSlice = this.getSlice((int) itemNode.getPosition());
                    return nearSlice(hashKey, itemSlice, i, atSlice.getSliceID()); // itemSlice 没有子节点
                }
                return new ResultSlice(parentSliceID, atSlice, i);
            }
            // 等于，命中 atSlice 就是要找的
            if (itemNode.getDataKey() == hashKey) {
                return new ResultSlice(parentSliceID, atSlice, i);
            }
        }
        //
        // .最右侧的数据都比 hashKey 小，那说明只能添加到 atSlice 上
        return new ResultSlice(parentSliceID, atSlice, childrensNodes.length);
    }
    //
    public ResultSlice insertData(DataNode dataSlice) throws IOException {
        // .找到最近的 Slice
        ResultSlice resultSlice = this.nearSlice(dataSlice.getDataKey());
        Slice atSlice = resultSlice.getAtSlice();
        int atPosition = resultSlice.getAtPosition();
        //
        Node[] childrensNodes = atSlice.getChildrensKeys();
        Node[] new_childrensNodes = new Node[childrensNodes.length + 1];
        //
        new_childrensNodes[atPosition] = dataSlice;
        //
        System.arraycopy(childrensNodes, 0, new_childrensNodes, 0, atPosition);
        System.arraycopy(childrensNodes, atPosition, new_childrensNodes, atPosition + 1, childrensNodes.length - atPosition);
        atSlice.setChildrensKeys(new_childrensNodes);
        //
        return splitAndStore(resultSlice, this.sliceSize);
    }
    private void insertToParent(TreeNode treeNode, Slice atSlice, int parentSlice) {
        Node[] childrensNodes = atSlice.getChildrensKeys();
        int atPosition = -1;
        for (int i = 0; i < childrensNodes.length; i++) {
            atPosition = i;
            Node itemNode = childrensNodes[i];
            if (treeNode.getDataKey() < itemNode.getDataKey()) {
                break;
            }
        }
        //
        Node[] new_childrensNodes = new Node[childrensNodes.length + 1];
        new_childrensNodes[atPosition] = treeNode;
        //
        System.arraycopy(childrensNodes, 0, new_childrensNodes, 0, atPosition);
        System.arraycopy(childrensNodes, atPosition, new_childrensNodes, atPosition + 1, childrensNodes.length - atPosition);
        //
        atSlice.setChildrensKeys(new_childrensNodes);
        //
        splitAndStore(new ResultSlice(parentSlice, atSlice, atPosition), this.sliceSize);
    }
    // -根据分裂因子进行分裂，分裂后返回依据的父节点
    private ResultSlice splitAndStore(ResultSlice sliceResult, int maxRecord) {
        //
        // .容量还够的情况下不进行分裂
        Slice slice = sliceResult.getAtSlice();
        Node[] childrens = slice.getChildrensKeys();
        if (childrens.length <= maxRecord || maxRecord < 1) {
            this.storeSlice(sliceResult.getAtSlice());
            return sliceResult;
        }
        // .查找切分点
        int splitPoint = childrens.length / 2;
        //
        // .切分数据
        Node[] leftKeys = new Node[splitPoint];
        System.arraycopy(childrens, 0, leftKeys, 0, leftKeys.length);
        Slice leftSlice = new Slice();
        leftSlice.setChildrensKeys(leftKeys);
        leftSlice.setSliceID(this.requestNewSliceID());
        //
        Node[] rightKeys = new Node[childrens.length - splitPoint];
        System.arraycopy(childrens, leftKeys.length, rightKeys, 0, rightKeys.length);
        Slice rightSlice = new Slice();
        rightSlice.setChildrensKeys(rightKeys);
        rightSlice.setSliceID(this.requestNewSliceID());
        //
        // .重新计算 sliceResult
        if (sliceResult.getAtPosition() < splitPoint) {
            sliceResult = new ResultSlice(sliceResult.getParentSlice(), leftSlice, sliceResult.getAtPosition());
        } else {
            sliceResult = new ResultSlice(sliceResult.getParentSlice(), rightSlice, sliceResult.getAtPosition() - splitPoint);
        }
        //
        // .决定是否增加树的高度
        if (sliceResult.getParentSlice() < 0) {
            // - 新的树根
            Slice newParent = new Slice();
            newParent.setSliceID(this.requestNewSliceID());
            TreeNode leftNode = new TreeNode(leftKeys[leftKeys.length - 1].getDataKey(), leftSlice.getSliceID());
            TreeNode rightNode = new TreeNode(rightKeys[rightKeys.length - 1].getDataKey(), rightSlice.getSliceID());
            newParent.setChildrensKeys(new Node[] { leftNode, rightNode, });
            //
            this.storeSlice(leftSlice);
            this.storeSlice(rightSlice);
            this.storeSlice(newParent);
            //
            this.updataEntryPoint(newParent.getSliceID());
            this.releaseSlice(slice.getSliceID());
            //
            return sliceResult;
        }
        //
        // .不需要增加树的高度，那么向父 Slice 插入数据
        rightSlice.setSliceID(slice.getSliceID());
        this.storeSlice(leftSlice);
        this.storeSlice(rightSlice);
        this.insertToParent(//
                new TreeNode(leftKeys[leftKeys.length - 1].getDataKey(), leftSlice.getSliceID()),//
                this.getSlice(sliceResult.getParentSlice()),//
                sliceResult.getParentSlice()//
        );
        //
        return sliceResult;
    }
    //
    //
    //
    //
    //
    //
    //
    //
    //
    protected void printNodeTree(int sliceID, int depth) {
        depth = (depth < 1) ? 1 : depth;
        Slice slice = getSlice(sliceID);
        if (slice == null) {
            System.out.println(sliceID);
        }
        for (Node node : slice.getChildrensKeys()) {
            if (node.isData()) {
                System.out.println("Data " + StringUtils.fixedString(' ', depth * 3) + node.getDataKey() + //
                        "\tslice-(" + slice.getSliceID() + ")");
            } else {
                System.out.println("Index" + StringUtils.fixedString(' ', depth * 3) + node.getDataKey() +//
                        "\tslice-(" + slice.getSliceID() + ")");
                printNodeTree((int) node.getPosition(), depth + 1);
            }
            //
        }
    }
    public static void main(String[] args) throws InterruptedException, IOException {
        SliceAdapter sliceAdapter = null;
        File bTree = new File("bTree.dat");
        BlockFileAdapter adapter = new BlockFileAdapter(new File("bTree.dat"));
        Random random = new Random(System.currentTimeMillis());
        //
        if (adapter.fileSize() > 0) {
            sliceAdapter = SliceAdapter.loadIndex(adapter);
        } else {
            int slicePoolSize = 1024 + random.nextInt(512);
            sliceAdapter = SliceAdapter.initIndex(5, slicePoolSize, adapter);
        }
        //
        int counter = 0;
        while (true) {
            if (counter >= 300)
                break;
            long randomLong = random.nextLong();
            System.out.println(randomLong);
            sliceAdapter.insertData(new DataNode(randomLong));
            //            sliceAdapter.insertData(new DataNode(counter));
            counter++;
        }
        //
        sliceAdapter.submit();
        //
        System.out.println("enteryKey = " + sliceAdapter.entryPoint);
        sliceAdapter.printNodeTree((int) sliceAdapter.entryPoint, 1);
        Thread.sleep(1000);
    }
}