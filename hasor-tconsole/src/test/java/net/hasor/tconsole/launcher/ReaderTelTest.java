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
package net.hasor.tconsole.launcher;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ReaderTelTest {
    @Test
    public void expectBlankLineTest() throws Throwable {
        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.heapBuffer();
        TelReaderObject readerObject = new TelReaderObject(PooledByteBufAllocator.DEFAULT, byteBuf);
        int i = 1;
        //
        ArrayList<String> arrayList = new ArrayList<>();
        while (true) {
            if (i > 100) {
                byteBuf.writeCharSequence("abcdefg abc\r\nabc\n", StandardCharsets.UTF_8);
                i = -1;
            } else if (i > 0) {
                i++;
            }
            //
            readerObject.update();
            readerObject.reset();
            if (!arrayList.isEmpty() && readerObject.isEof()) {
                break;
            }
            boolean blankLine = readerObject.expectBlankLine();
            if (blankLine) {
                arrayList.add(readerObject.removeReadData());
            }
        }
        //
        assert arrayList.size() == 2;
        assert arrayList.get(0).equals("abcdefg abc\r");
        assert arrayList.get(1).equals("abc");
    }

    @Test
    public void expectDoubleBlankLines() throws Throwable {
        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.heapBuffer();
        TelReaderObject readerObject = new TelReaderObject(PooledByteBufAllocator.DEFAULT, byteBuf);
        byteBuf.writeCharSequence("abcdefg abc\r\nabc\n\n", StandardCharsets.UTF_8);
        //
        ArrayList<String> arrayList = new ArrayList<>();
        while (true) {
            readerObject.update();
            readerObject.reset();
            if (readerObject.isEof()) {
                break;
            }
            boolean blankLine = readerObject.expectDoubleBlankLines();
            if (blankLine) {
                arrayList.add(readerObject.removeReadData());
            }
        }
        //
        assert arrayList.size() == 1;
        assert arrayList.get(0).equals("abcdefg abc\r\nabc");
    }
}