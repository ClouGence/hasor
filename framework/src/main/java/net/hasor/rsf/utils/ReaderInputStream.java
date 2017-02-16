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
package net.hasor.rsf.utils;
import java.io.*;
/**
 * 使用InputStream读取Reader的工具类
 * @version 2009-5-13
 * @author 网络收集
 */
public class ReaderInputStream extends InputStream {
    private Reader                reader        = null;
    private ByteArrayOutputStream byteArrayOut  = null;
    private Writer                writer        = null;
    private char[]                chars         = null;
    private byte[]                buffer        = null;
    private int                   index, length = 0;
    //========================================================================================
    /**
     * 带Reader参数构造函数
     * @param readerString - 要阅读的字符串。
     */
    public ReaderInputStream(final String readerString) {
        this.reader = new StringReader(readerString);
        this.byteArrayOut = new ByteArrayOutputStream();
        this.writer = new OutputStreamWriter(this.byteArrayOut);
        this.chars = new char[1024];
    }
    /**
     * 带Reader参数构造函数
     * @param reader - InputStream使用的Reader
     */
    public ReaderInputStream(final Reader reader) {
        this.reader = reader;
        this.byteArrayOut = new ByteArrayOutputStream();
        this.writer = new OutputStreamWriter(this.byteArrayOut);
        this.chars = new char[1024];
    }
    /**
     * 带Reader和字符编码格式参数的构造函数
     * @param reader   - InputStream使用的Reader
     * @param encoding - InputStream使用的字符编码格式.
     * @throws UnsupportedEncodingException 如果字符编码格式不支持,则抛UnsupportedEncodingException异常
     */
    public ReaderInputStream(final Reader reader, final String encoding) throws UnsupportedEncodingException {
        this.reader = reader;
        this.byteArrayOut = new ByteArrayOutputStream();
        this.writer = new OutputStreamWriter(this.byteArrayOut, encoding);
        this.chars = new char[1024];
    }
    //========================================================================================
    /** @see InputStream#read() */
    @Override
    public int read() throws IOException {
        if (this.index >= this.length) {
            this.fillBuffer();
        }
        if (this.index >= this.length) {
            return -1;
        }
        return 0xff & this.buffer[this.index++];
    }
    private void fillBuffer() throws IOException {
        if (this.length < 0) {
            return;
        }
        int numChars = this.reader.read(this.chars);
        if (numChars < 0) {
            this.length = -1;
        } else {
            this.byteArrayOut.reset();
            this.writer.write(this.chars, 0, numChars);
            this.writer.flush();
            this.buffer = this.byteArrayOut.toByteArray();
            this.length = this.buffer.length;
            this.index = 0;
        }
    }
    /** @see InputStream#read(byte[], int, int) */
    @Override
    public int read(final byte[] data, final int off, final int len) throws IOException {
        if (this.index >= this.length) {
            this.fillBuffer();
        }
        if (this.index >= this.length) {
            return -1;
        }
        int amount = Math.min(len, this.length - this.index);
        System.arraycopy(this.buffer, this.index, data, off, amount);
        this.index += amount;
        return amount;
    }
    /** @see InputStream#available() */
    @Override
    public int available() throws IOException {
        return this.index < this.length ? this.length - this.index : this.length >= 0 && this.reader.ready() ? 1 : 0;
    }
    /** @see InputStream#close() */
    @Override
    public void close() throws IOException {
        this.reader.close();
    }
}
