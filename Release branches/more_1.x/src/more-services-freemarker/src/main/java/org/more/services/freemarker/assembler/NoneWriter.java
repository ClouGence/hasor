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
package org.more.services.freemarker.assembler;
import java.io.IOException;
import java.io.Writer;
/**
 * {@link Writer}
 * @version : 2011-8-12
 * @author 赵永春 (zyc@byshell.org)
 */
class NoneWriter extends Writer {
    private Writer writer = null;
    /**什么都不干的{@link Writer}。*/
    public NoneWriter() {}
    /**所有内容都输出到参数所代表的{@link Writer}中。*/
    public NoneWriter(Writer writer) {
        this.writer = writer;
    }
    public void close() throws IOException {
        if (this.writer != null)
            this.writer.close();
    }
    public void flush() throws IOException {
        if (this.writer != null)
            this.writer.flush();
    }
    public void write(char[] cbuf, int off, int len) throws IOException {
        if (this.writer != null)
            this.writer.write(cbuf, off, len);
    }
}