package test.net.hasor.test.storage;
import net.hasor.registry.storage.block.BlockFileAdapter;
import net.hasor.registry.storage.btree.DataNode;
import net.hasor.registry.storage.btree.Node;
import net.hasor.registry.storage.btree.Slice;
import net.hasor.registry.storage.btree.SliceAdapter;
import net.hasor.utils.StringUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.regex.Pattern;
//
//
public class SliceAdapterTest {
    protected void printNodeTree(SliceAdapter sliceAdapter, int sliceID, int depth) {
        depth = (depth < 1) ? 1 : depth;
        Slice slice = sliceAdapter.getSlice(sliceID);
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
                printNodeTree(sliceAdapter, (int) node.getPosition(), depth + 1);
            }
            //
        }
    }
    @Test
    public void test() throws InterruptedException, IOException {
        System.out.println(Pattern.matches("[a-zA-Z0-9]+", "ab@Cdefg12345"));
    }
    @Test
    public void main() throws InterruptedException, IOException {
        SliceAdapter sliceAdapter = null;
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
            if (counter >= 3000000)
                break;
            long randomLong = random.nextLong();
            System.out.println(counter + " - " + randomLong);
            sliceAdapter.insertData(new DataNode(randomLong));
            //            sliceAdapter.insertData(new DataNode(counter));
            counter++;
            if (counter % 10000 == 0) {
                sliceAdapter.submitToFile();
            }
        }
        //
        sliceAdapter.submitToFile();
        //
        //        long enttyPoint = sliceAdapter.getEntryPoint().getSliceID();
        //        System.out.println("enteryKey = " + enttyPoint);
        //        printNodeTree(sliceAdapter, (int) enttyPoint, 1);
        //        Thread.sleep(1000);
    }
}