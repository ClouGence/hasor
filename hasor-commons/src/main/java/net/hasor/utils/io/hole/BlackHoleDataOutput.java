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
package net.hasor.utils.io.hole;
import java.io.DataOutput;
import java.io.IOException;

/**
 * 黑洞 DataOutput
 * @version 2021-03-02
 * @author 赵永春 (zyc@hasor.net)
 */
public class BlackHoleDataOutput implements DataOutput {
    @Override
    public void write(int b) throws IOException {
    }

    @Override
    public void write(byte[] b) throws IOException {
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
    }

    @Override
    public void writeByte(int v) throws IOException {
    }

    @Override
    public void writeShort(int v) throws IOException {
    }

    @Override
    public void writeChar(int v) throws IOException {
    }

    @Override
    public void writeInt(int v) throws IOException {
    }

    @Override
    public void writeLong(long v) throws IOException {
    }

    @Override
    public void writeFloat(float v) throws IOException {
    }

    @Override
    public void writeDouble(double v) throws IOException {
    }

    @Override
    public void writeBytes(String s) throws IOException {
    }

    @Override
    public void writeChars(String s) throws IOException {
    }

    @Override
    public void writeUTF(String s) throws IOException {
    }
}
