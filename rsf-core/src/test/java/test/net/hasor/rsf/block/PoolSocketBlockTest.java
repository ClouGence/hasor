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
import net.hasor.rsf.protocol.protocol.PoolSocketBlock;
import org.junit.Test;
import org.more.util.StringUtils;
/**
 * 
 * @version : 2015年4月19日
 * @author 赵永春(zyc@hasor.net)
 */
public class PoolSocketBlockTest {
    @Test
    public void socketBlockTest() throws Exception {
        //写测试
        PoolSocketBlock block = new PoolSocketBlock();
        String messageTmp = "Block-%s-Mssage.";
        //
        System.err.println("\nbegin write...");
        block.pushData(null);//0
        block.pushData(new byte[0]);//1
        for (int i = 2; i < 0xFFFF; i++) {
            String msg = String.format(messageTmp, i);
            block.pushData(msg.getBytes());
        }
        //
        try {
            System.err.println("\nmax test.");
            block.pushData("123456789".getBytes());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        //
        System.err.println("\nbegin read...");
        for (int i = 0; i < 0xFFFF; i++) {
            byte[] msgByte = block.readPool(i);
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