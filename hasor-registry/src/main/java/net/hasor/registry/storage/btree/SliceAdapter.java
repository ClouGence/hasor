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
import net.hasor.registry.storage.block.Block;
import net.hasor.registry.storage.block.BlockChannel;
import net.hasor.registry.storage.block.BlockFileAdapter;
import net.hasor.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
/**
 * B-Tree 索引操作类
 * @version : 2018年5月28日
 * @author 赵永春 (zyc@hasor.net)
 */
public class SliceAdapter {
    protected static Logger           logger = LoggerFactory.getLogger(SliceAdapter.class);
    private          int              entryPoint;      // 入口点
    private          byte[]           slicePool;       // slice 使用状态池，所有 bit状态位的总数等于 maxEchoSequence
    private          ByteBuffer       slicePosition;   // slice 数据块所在真实位置
    private          List<Long>       changeList;//
    //
    private          BlockFileAdapter fileAdapter;     //
    private          int              splitSize;       // 切块大小
    private          int              maxEchoSequence; // slice 编号分配采用自旋序列，这个值是旋转的最大上限
    private          int              echoSequence;    // 当前自旋值
    //
    //
    /** 从文件中读取一个索引 */
    public static SliceAdapter loadIndex(BlockFileAdapter fileAdapter) throws IOException {
        Block firstBlock = fileAdapter.firstBlock();
        if (firstBlock == null) {
            throw new IOException("cannot load empty file.");
        }
        //
        BlockChannel blockChannel = fileAdapter.getBlockChannel(firstBlock);
        ByteBuffer headBuffer = ByteBuffer.allocate(11);
        int readSize1 = blockChannel.read(headBuffer);
        if (readSize1 != 11) {
            throw new IOException("file is damaged.");
        }
        //
        headBuffer.flip();
        byte version = headBuffer.get();                    // 1 bytes - 版本
        int splitSize = headBuffer.getShort();              // 2 bytes - Slice 切分大小
        int entryPoint = headBuffer.getInt();               // 4 bytes - Btree 的树根 SliceID
        int splitStatusBytesLength = headBuffer.getInt();   // 4 bytes - 状态池字节大小
        //
        ByteBuffer slicePoolBuffer = ByteBuffer.allocate(splitStatusBytesLength);
        blockChannel.read(slicePoolBuffer);
        slicePoolBuffer.flip();
        byte[] slicePool = slicePoolBuffer.array();
        //
        SliceAdapter adapter = new SliceAdapter();
        adapter.entryPoint = entryPoint;
        adapter.splitSize = splitSize;
        adapter.slicePool = slicePool;
        adapter.maxEchoSequence = splitStatusBytesLength * 8;// 序列总数等于这些字节的总共二进制位数
        adapter.echoSequence = 0;
        //
        // slice 数据真实位置使用一个 long 存储，总长度等于 maxEchoSequence 乘 8
        adapter.slicePosition = ByteBuffer.allocateDirect(adapter.maxEchoSequence * 8);
        int readSize2 = blockChannel.read(adapter.slicePosition);
        if (readSize2 != adapter.slicePosition.capacity()) {
            throw new IOException("file is damaged.");
        }
        //
        adapter.fileAdapter = fileAdapter;
        return adapter;
    }
    //
    /** 初始化一个全新的索引 */
    public static SliceAdapter initIndex(int splitSize, int slicePoolSize, BlockFileAdapter fileAdapter) throws IOException {
        if (splitSize < 3 || slicePoolSize < 1) {
            throw new IndexOutOfBoundsException("splitSize < 3 or slicePoolSize < 1");
        }
        //
        SliceAdapter adapter = new SliceAdapter();
        adapter.splitSize = splitSize;
        int realPoolBytesLength = Math.abs(slicePoolSize) / 8;
        realPoolBytesLength = realPoolBytesLength == 0 ? 1 : realPoolBytesLength;
        adapter.echoSequence = 0;
        adapter.maxEchoSequence = realPoolBytesLength * 8;
        int slicePositionDirectBufferSize = adapter.maxEchoSequence * 8; // all of slice position
        logger.info("realPoolSize= " + realPoolBytesLength + " ,echoSequence=" + adapter.maxEchoSequence + " ,slicePositionDirectBufferSize=" + slicePositionDirectBufferSize);
        //
        adapter.slicePosition = ByteBuffer.allocateDirect(slicePositionDirectBufferSize);
        adapter.slicePool = new byte[realPoolBytesLength];
        for (int i = 0; i < adapter.slicePool.length; i++) {
            adapter.slicePool[i] = -1;
        }
        //
        adapter.fileAdapter = fileAdapter;
        adapter.submitStore(true);
        return adapter;
    }
    //
    //
    //
    //
    //
    private Map<String, byte[]> sliceDataMap = new ConcurrentHashMap<String, byte[]>();
    private void deleteBlock(long position) {
        sliceDataMap.remove(String.valueOf(position));
        System.out.println("releaseBlock-" + position);
    }
    //
    private ByteBuffer readBlock(long position) {
        String sliceKey = String.valueOf(position);
        if (!sliceDataMap.containsKey(sliceKey)) {
            return null;
        }
        return ByteBuffer.wrap(sliceDataMap.get(sliceKey));
    }
    private long writeBlock(ByteBuffer dataBufer) {
        long timeMillis = Math.abs(new Random(System.currentTimeMillis()).nextLong());
        while (this.sliceDataMap.containsKey(String.valueOf(timeMillis))) {
            timeMillis = Math.abs(new Random(System.currentTimeMillis()).nextLong());
        }
        //
        byte[] temp = new byte[dataBufer.capacity()];
        dataBufer.flip();
        dataBufer.get(temp);
        sliceDataMap.put(String.valueOf(timeMillis), temp);
        return timeMillis;
    }
    /**递交存储改动，保存到磁盘上*/
    private void submitStore(boolean init) throws IOException {
        ByteBuffer submitData = ByteBuffer.allocate(11 + this.slicePool.length + this.slicePool.length * 8 * 8);
        //
        submitData.put((byte) 1);                   // 1 bytes - 版本
        submitData.putShort((short) this.splitSize);// 2 bytes - Slice 切分大小
        submitData.putInt(this.entryPoint);         // 4 bytes - Btree 的树根 SliceID
        submitData.putInt(this.slicePool.length);   // 4 bytes - 状态池字节大小
        //
        submitData.put(this.slicePool);
        submitData.put(this.slicePosition);
        //
        if (init) {
            Block block = this.fileAdapter.firstBlock();
            if (block == null) {
                block = this.fileAdapter.eofBlock();
            }
            OutputStream outputStream = this.fileAdapter.getOutputStream(block);
            byte[] data = new byte[submitData.capacity()];
            submitData.flip();
            submitData.get(data);
            outputStream.write(data);
            outputStream.flush();
            outputStream.close();
        }
    }
    //
    public void close() throws IOException {
        this.fileAdapter.close();
    }
    //
    //
    //
    //
    //
    //
    private SliceAdapter() {
    }
    /*获取跟 Slice */
    private Slice getEntryPoint() {
        Slice root = getSlice(entryPoint);
        if (root != null) {
            return root;
        }
        //
        root = new Slice();
        root.setSliceID(requestNewSliceID());
        root.setChildrensKeys(new Node[] {});
        this.storeSlice(root);
        //
        return root;
    }
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
            byte testBit = (byte) (this.slicePool[byteIndex] | (1 << bitIndex));
            if (this.slicePool[byteIndex] == testBit) {
                this.slicePool[byteIndex] = (byte) (this.slicePool[byteIndex] & ~(1 << bitIndex));
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
        // .清理状态
        int byteIndex = sliceID / 8;
        int bitIndex = sliceID % 8;
        this.slicePool[byteIndex] = (byte) (this.slicePool[byteIndex] | (1 << bitIndex));
        // .获取Block位置，然后删除它
        long position = this.slicePosition.getLong(sliceID * 8);
        this.deleteBlock(position);
    }
    //
    //
    protected Slice getSlice(int sliceID) {
        // .根据 sliceID 计算slicePosition位置，然后取出 slice 的 Position
        long positionLong = this.slicePosition.getLong(sliceID * 8);
        ByteBuffer byteBuffer = this.readBlock(positionLong);
        if (byteBuffer == null) {
            return null;
        }
        //
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
    //
    //
    protected long storeSlice(Slice sliceData) {
        // .计算 Slice 所需长度 (最大不过 512K)
        int dataLength = sliceData.getChildrensKeys().length * 16 + 6; // Node length is long + long = 8 + 8
        //
        // .写数据到 Buffer
        ByteBuffer writeBuffer = ByteBuffer.allocateDirect(dataLength);
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
        // .保存到 Block
        long dataPosition = this.writeBlock(writeBuffer);
        this.slicePosition.putLong(sliceData.getSliceID() * 8, dataPosition);
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
        boolean operStatus = false;
        try {
            resultSlice = splitAndStore(resultSlice, this.splitSize);
            operStatus = true;
            return resultSlice;
        } finally {
            if (operStatus) {
                submitStore(false);
            }
        }
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
        splitAndStore(new ResultSlice(parentSlice, atSlice, atPosition), this.splitSize);
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
    private void updataEntryPoint(int entryPoint) {
        this.entryPoint = entryPoint;
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
        if (bTree.exists()) {
            sliceAdapter = SliceAdapter.loadIndex(adapter);
        } else {
            int slicePoolSize = 1024 + random.nextInt(512);
            sliceAdapter = SliceAdapter.initIndex(5, slicePoolSize, adapter);
        }
        //
        int counter = 0;
        while (true) {
            if (counter >= 30)
                break;
            long randomLong = random.nextLong();
            System.out.println(randomLong);
            sliceAdapter.insertData(new DataNode(randomLong));
            counter++;
        }
        //
        System.out.println("enteryKey = " + sliceAdapter.entryPoint);
        sliceAdapter.printNodeTree((int) sliceAdapter.entryPoint, 1);
        Thread.sleep(1000);
    }
}