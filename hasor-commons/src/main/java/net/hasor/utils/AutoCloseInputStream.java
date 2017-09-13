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
package net.hasor.utils;
import java.io.IOException;
import java.io.InputStream;
/**
 * 具有自动关闭的输入流
 * @version 2009-5-13
 * @author 赵永春 (zyc@hasor.net)
 */
class AutoCloseInputStream extends InputStream {
    /** 目标读取的流 */
    private InputStream in    = null;
    /** 目标读取的流 */
    private boolean     close = false;
    //========================================================================================
    /**
     * 创建自动关闭的输入流
     * @param in 读取的目标流
     */
    public AutoCloseInputStream(final InputStream in) {
        this.in = in;
    }
    //========================================================================================
    /**
     * 如果读取的目标流已经读取到末尾则自动关闭该流，并且对于已经关闭的流调用该方法将始终返回-1。
     * @return 如果读取的目标流已经读取到末尾则自动关闭该流，并且对于已经关闭的流调用该方法将始终返回-1。
     */
    @Override
    public int read() throws IOException {
        if (this.close) {
            return -1;
        }
        int read = this.in.read();
        if (read == -1) {
            this.close();
        }
        return read;
    }
    @Override
    public int available() throws IOException {
        return this.in.available();
    }
    @Override
    public void close() throws IOException {
        this.in.close();
        this.close = true;
    }
    @Override
    public synchronized void mark(final int readlimit) {
        this.in.mark(readlimit);
    }
    @Override
    public boolean markSupported() {
        return this.in.markSupported();
    }
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        return this.in.read(b, off, len);
    }
    @Override
    public int read(final byte[] b) throws IOException {
        return this.in.read(b);
    }
    @Override
    public synchronized void reset() throws IOException {
        this.in.reset();
    }
    @Override
    public long skip(final long n) throws IOException {
        return this.in.skip(n);
    }
}