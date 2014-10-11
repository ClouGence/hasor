package net.hasor.rsf._test;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import net.hasor.rsf.protocol.field.DataField;
/**
 * 
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class CopyOfHelloClient {
    public static void main(String[] args) throws Exception {
        ByteBuf allData = PooledByteBufAllocator.DEFAULT.heapBuffer();
        DataField df = new DataField();
        //
        //encode
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 42767; i++)
            sb.append("a");
        df.setValue(sb.toString());
        df.encode(allData);
        //
        //decode
        df.decode(allData);
        String oriData = df.getValue();
        //
        System.out.println(oriData.length());
    }
}