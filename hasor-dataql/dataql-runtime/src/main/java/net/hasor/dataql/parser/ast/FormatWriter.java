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
package net.hasor.dataql.parser.ast;
import java.io.IOException;
import java.io.Writer;

/**
 * DataQL 代码格式化的输出 Writer
 */
public class FormatWriter extends Writer {
    private final Writer writer;

    public FormatWriter(Writer writer) {
        this.writer = writer;
    }

    @Override
    public void write(char[] cBuf, int off, int len) throws IOException {
        this.writer.write(cBuf, off, len);
    }

    @Override
    public void flush() throws IOException {
        this.writer.flush();
    }

    @Override
    public void close() throws IOException {
        this.writer.close();
    }
}
