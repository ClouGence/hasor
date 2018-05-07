package test.net.hasor.test.storage;
import net.hasor.registry.storage.Block;
import net.hasor.registry.storage.BlockFileAdapter;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.Random;
//
//
public class BlockFileAdapterTest {
    //
    private static int  emptyBlock = 3;
    private static File FILE       = new File("myfile.dat");
    private static byte[] tempData;

    static {
        String temp = "1234567890abcdefghijklmnopqrstuvwxyz";
        String finalString = "";
        while (finalString.length() < 8102) {
            finalString = finalString + temp;
        }
        tempData = finalString.getBytes();
    }
    //
    //
    /** 随机写若干个 Block 块，每个块大小随机。同时是否删除也随机 */
    @Before
    public void writeFile() throws IOException {
        FILE.delete();
        BlockFileAdapter adapter = new BlockFileAdapter(FILE);
        //
        Random random = new Random(System.currentTimeMillis());
        int count = random.nextInt(50) + 50;
        System.out.println(count);
        for (int i = 1; i <= count; i++) {
            int dataSize = random.nextInt(1024 * 1024);
            if (i == emptyBlock) {
                dataSize = 0;
            }
            writeItem(random.nextBoolean(), dataSize, adapter);
            System.out.println("i=" + i + " of " + count);
        }
        adapter.close();
    }
    private void writeItem(boolean delete, int dataSize, BlockFileAdapter adapter) throws IOException {
        Block block = adapter.endBlock();
        OutputStream outStream = adapter.getOutputStream(block);
        while (dataSize > 0) {
            //
            if (dataSize > tempData.length) {
                dataSize -= tempData.length;
                outStream.write(tempData);
            } else {
                outStream.write(tempData, 0, dataSize);
                dataSize = 0;
            }
        }
        outStream.close();
        //
        if (delete) {
            block = adapter.findBlock(block);
            adapter.deleteBlock(block);
        }
    }
    //
    // --------------------------------------------------------------------------------------------
    //
    /** 迭代方式访问所有Block：删除、总量 */
    @Test
    public void iteratorlocks() throws IOException {
        BlockFileAdapter adapter = new BlockFileAdapter(FILE);
        int sumCount = 0;
        int delCount = 0;
        while (adapter.hasNext()) {
            Block block = adapter.nextBlock();
            if (block == null) {
                break;
            }
            if (block.isInvalid()) {
                delCount++;
            }
            sumCount++;
            System.out.println("dataItem = " + block.getPosition() + ", blockSize= " + block.getBlockSize() + ", dataSize = " + block.getDataSize());
        }
        adapter.close();
        System.out.println("del=" + delCount + " ,sum=" + sumCount);
    }
    //
    /** 扫描所有块统计：删除、总量 */
    @Test
    public void sumBlocks() throws IOException {
        BlockFileAdapter adapter = new BlockFileAdapter(FILE);
        int sumCount = 0;
        int delCount = 0;
        while (true) {
            Block block = adapter.nextBlock();
            if (block == null) {
                break;
            }
            if (block.isInvalid()) {
                delCount++;
            }
            sumCount++;
        }
        adapter.close();
        System.out.println("del=" + delCount + " ,sum=" + sumCount);
    }
    //
    /** 在一个随机区块后面查找最近一个失效的区块 */
    @Test
    public void findFreeSpace() throws IOException {
        BlockFileAdapter adapter = new BlockFileAdapter(FILE);
        int sumBlock = 0;
        while (adapter.nextBlock() != null) {
            sumBlock++;
        }
        //
        adapter.reset();
        int atBlock = new Random(System.currentTimeMillis()).nextInt(sumBlock);
        for (int i = 0; i < atBlock; i++) {
            adapter.nextBlock();
        }
        Block freeSpace = adapter.findFreeSpace();
        //
        adapter.close();
        System.out.println("freeSpace=" + freeSpace);
    }
    //
    /** 随机位置的 Block 读取随机数量的内容，然后继续上述过程
     *    - 验证 tryReadBlock 返回的 stream 在被操作一半情况下对 nextBlock 有无影响 */
    @Test
    public void testSomeRead() throws IOException {
        Random random = new Random(System.currentTimeMillis());
        BlockFileAdapter adapter = new BlockFileAdapter(FILE);
        Block block = null;
        while ((block = adapter.nextBlock()) != null) {
            if (random.nextBoolean() || block.getDataSize() == 0) {
                System.out.println("read = " + block.getPosition() + ", be ignored, dataSize = " + block.getDataSize());
                continue; //随机跳过 & body 为0 的跳过
            }
            InputStream inputStream = adapter.getInputStream(block);
            int dataSize = inputStream.available();
            int readSize = random.nextInt(dataSize);
            System.out.println("read = " + block.getPosition() + ", readSize= " + readSize + ", dataSize = " + dataSize);
            byte[] buffer = new byte[2048];
            while (true) {
                if (readSize > buffer.length) {
                    inputStream.read(buffer);
                    readSize = readSize - buffer.length;
                } else {
                    inputStream.read(buffer, 0, readSize);
                    readSize = 0;
                }
                if (readSize == 0) {
                    break;
                }
            }
            inputStream.close();
        }
        adapter.close();
    }
    //
    /** 读取0个字节是否有影响 */
    @Test
    public void testZeroRead() throws IOException {
        BlockFileAdapter adapter = new BlockFileAdapter(FILE);
        Block block = adapter.nextBlock();
        byte[] buffer = new byte[8];
        InputStream inputStream = adapter.getInputStream(block);
        int read = inputStream.read(buffer, 0, 0);
        //
        adapter.nextBlock();
        adapter.close();
    }
    //
    /** 在文件的最末尾调用findFreeSpace */
    @Test
    public void testLastBlockRead() throws IOException {
        BlockFileAdapter adapter = new BlockFileAdapter(FILE);
        //
        while (adapter.nextBlock() != null)
            ;
        //
        Block freeSpace = adapter.findFreeSpace();
        System.out.println("freeSpace=" + freeSpace + " , fileSize=" + adapter.fileSize());
        //
        adapter.close();
    }
    //
    /** 测试 Stream 过期的情况 */
    @Test
    public void testReadExpiredStream() throws IOException {
        BlockFileAdapter adapter = new BlockFileAdapter(FILE);
        //
        Block block = adapter.nextBlock();
        InputStream inputA = adapter.getInputStream(block);
        inputA.read();
        //
        block = adapter.nextBlock();
        InputStream inputB = adapter.getInputStream(block);
        inputB.read();
        //
        try {
            inputA.read();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assert "stream has expired.".equals(e.getMessage());
        }
        adapter.close();
    }
    //
    /** 读取到 Stream 中 */
    @Test
    public void testReadToStream() throws IOException {
        BlockFileAdapter adapter = new BlockFileAdapter(FILE);
        //
        Block block = adapter.nextBlock();
        //
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        adapter.readToStream(block, outStream);
        System.out.println("blockDataSize = " + block.getDataSize() + " ,dataLength =" + new String(outStream.toByteArray()));
        //
        adapter.close();
    }
    //
    // --------------------------------------------------------------------------------------------
    //
    /** 随机删除一个 Block */
    @Test
    public void deleteBlock() throws IOException {
        iteratorlocks();
        //
        Random random = new Random(System.currentTimeMillis());
        BlockFileAdapter adapter = new BlockFileAdapter(FILE);
        while (adapter.hasNext()) {
            Block block = adapter.nextBlock();
            if (block == null) {
                break;
            }
            if (block.isInvalid()) {
                continue;
            }
            if (random.nextBoolean()) {
                adapter.deleteBlock(block);
                break;
            }
            //
        }
        adapter.close();
        //
        iteratorlocks();
    }
    //
    /** 随机写若干有效的 Block，会复用 已经删除的 Block 空间（文件大小不会减少）*/
    @Test
    public void reWriteFile() throws IOException {
        iteratorlocks();
        //
        BlockFileAdapter adapter = new BlockFileAdapter(FILE);
        Block freeSpace = adapter.findFreeSpace();
        System.out.println("near deleteItem = " + freeSpace);
        //
        Random random = new Random(System.currentTimeMillis());
        int reWriteSize = 0;
        if (freeSpace.isEof()) {
            reWriteSize = random.nextInt(1024 * 1024);
        } else {
            reWriteSize = random.nextInt((int) freeSpace.getBlockSize());
        }
        reWriteSize -= 3; // to use "END"
        System.out.println("reWrite size = " + reWriteSize);
        //
        OutputStream outStream = adapter.getOutputStream(freeSpace);
        while (reWriteSize > 0) {
            //
            if (reWriteSize > tempData.length) {
                reWriteSize -= tempData.length;
                outStream.write(tempData);
            } else {
                outStream.write(tempData, 0, reWriteSize);
                outStream.write("END".getBytes());
                reWriteSize = 0;
            }
        }
        outStream.close();
        adapter.close();
        //
        iteratorlocks();
        //
        //
        adapter = new BlockFileAdapter(FILE);
        freeSpace = adapter.findBlock(freeSpace);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        adapter.readToStream(freeSpace, stream);
        System.out.println("blockDataSize = " + freeSpace + " ,data=" + new String(stream.toByteArray()));
    }
    //
    /** 末尾追加内容 */
    @Test
    public void appendFile() throws IOException {
        iteratorlocks();
        //
        BlockFileAdapter adapter = new BlockFileAdapter(FILE);
        Block endBlock = adapter.endBlock();
        System.out.println("endBlock = " + endBlock);
        //
        OutputStream outputStream = adapter.getOutputStream(endBlock);
        outputStream.write("END".getBytes());
        outputStream.close();
        adapter.close();
        //
        iteratorlocks();
        //
        adapter = new BlockFileAdapter(FILE);
        endBlock = adapter.findBlock(endBlock);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        adapter.readToStream(endBlock, stream);
        System.out.println("blockDataSize = " + endBlock + " ,data=" + new String(stream.toByteArray()));
    }
    //
    /** 分裂一个Block（被分裂的 Block 必须是 delete 状态） */
    @Test
    public void splitBlockTest() throws IOException {
        iteratorlocks();
        //
        Random random = new Random(System.currentTimeMillis());
        BlockFileAdapter adapter = new BlockFileAdapter(FILE);
        Block block = null;
        while ((block = adapter.nextBlock()) != null) {
            if (block.isInvalid()) {
                break;
            }
            if (random.nextBoolean()) {
                adapter.deleteBlock(block);
                break;
            }
        }
        if (block == null) {
            assert false;
        }
        //
        System.out.println("split at :" + block);
        adapter.splitBlock(block, block.getBlockSize() / 2);
        adapter.close();
        //
        iteratorlocks();
    }
    /** 合并两个Block */
    @Test
    public void mergeBlockTest() throws IOException {
        iteratorlocks();
        //
        Random random = new Random(System.currentTimeMillis());
        BlockFileAdapter adapter = new BlockFileAdapter(FILE);
        Block block = null;
        while ((block = adapter.nextBlock()) != null) {
            if (block.isInvalid()) {
                break;
            }
            if (random.nextBoolean()) {
                adapter.deleteBlock(block);
                break;
            }
        }
        if (block == null) {
            assert false;
        }
        //
        System.out.println("split at :" + block);
        Block[] splitBlock = adapter.splitBlock(block, block.getBlockSize() / 2);
        adapter.close();
        //
        iteratorlocks();
        //
        //
        adapter = new BlockFileAdapter(FILE);
        adapter.mergeBlock(splitBlock);
        adapter.close();
        //
        iteratorlocks();
    }
}