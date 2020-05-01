package test.net.hasor.rsf.hprose;
import hprose.io.HproseReader;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by yongchun.zyc on 2017/1/11.
 */
public class HproseTest {
    @Test
    public void readTest() throws IOException {
        //String data = "s5\"hello\"a1{s5\"world\"}z";
        //String data = "s3\"sum\"a3{012}z";
        String data = "s4\"sort\"a1{a10{2465318790}}tz";
        //
        HproseReader reader = new HproseReader(data.getBytes());
        //
        String string = reader.readString();
        System.out.println(string);
        Object[] objects = reader.readObjectArray();
    }
}
