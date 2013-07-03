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
package org.hasor.view.decorate.support;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.hasor.view.decorate.DecorateServletResponse;
/**
 * 
 * @version : 2013-6-9
 * @author 赵永春 (zyc@byshell.org)
 */
class DecHttpServletResponsePropxy extends HttpServletResponseWrapper implements DecorateServletResponse {
    private ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
    //
    public DecHttpServletResponsePropxy(HttpServletResponse response) {
        super(response);
    }
    @Override
    public void flushBuffer() throws IOException {
        this.byteArray.flush();
    }
    @Override
    public void reset() {
        super.reset();
        this.resetBuffer();
    }
    @Override
    public void resetBuffer() {
        this.byteArray.reset();
    }
    @Override
    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(this.getOutputStream());
    }
    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return new ServletOutputStream() {
            @Override
            public void write(int b) throws IOException {
                byteArray.write(b);
            }
            @Override
            public void close() throws IOException {}
        };
    }
    /**获取装饰之前的内容*/
    public byte[] getBufferData() {
        return this.byteArray.toByteArray();
    }
    /**输出装饰之后的内容*/
    public void sendByteData(byte[] bytes) throws IOException {
        if (super.isCommitted() == false) {
            this.setContentLength(bytes.length);
            super.resetBuffer();
            super.getOutputStream().write(bytes);
        }
    }
}