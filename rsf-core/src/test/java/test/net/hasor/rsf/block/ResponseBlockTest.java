/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package test.net.hasor.rsf.block;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import net.hasor.rsf.protocol.codec.RpcResponseProtocol;
import net.hasor.rsf.protocol.protocol.ResponseSocketBlock;
import org.junit.Test;
import org.more.util.StringUtils;
/**
 * 
 * @version : 2015年4月19日
 * @author 赵永春(zyc@hasor.net)
 */
public class ResponseBlockTest {
    @Test
    public void socketBlockTest() throws Exception {
        ByteBuf buf = ByteBufAllocator.DEFAULT.heapBuffer();
        //
        //写测试
        ResponseSocketBlock writeblock = new ResponseSocketBlock();
        String messageTmp = "Block-%s-Mssage.";
        //
        System.err.println("\nbegin write...");
        writeblock.pushData(null);//0
        writeblock.pushData(new byte[0]);//1
        for (short i = 2; i < 0x0FFF; i++) {
            String msg = String.format(messageTmp, i);
            writeblock.pushData(msg.getBytes());
        }
        RpcResponseProtocol resProtocol = new RpcResponseProtocol();
        resProtocol.encode(writeblock, buf);
        //
        //
        //
        System.err.println("\nbegin read...");
        ResponseSocketBlock readblock = resProtocol.decode(buf);
        for (short i = 0; i < 0x0FFF; i++) {
            byte[] msgByte = readblock.readPool(i);
            if (i == 0) {
                if (msgByte == null) {
                    System.out.println("data is null.");
                    continue;
                } else {
                    throw new Exception();
                }
            }
            if (i == 1) {
                if (msgByte.length == 0) {
                    System.out.println("data is empty.");
                    continue;
                } else {
                    throw new Exception();
                }
            }
            //
            String msg1 = new String(msgByte);
            String msg2 = String.format(messageTmp, i);
            if (StringUtils.equals(msg1, msg2) == false) {
                System.out.println(msg2);
                throw new Exception();
            }
        }
    }
}