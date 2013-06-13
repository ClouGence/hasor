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
package org.platform.view.decorate;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
/**
 * 
 * @version : 2013-6-9
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class ResponsePropxy extends HttpServletResponseWrapper {
    public ResponsePropxy(ServletResponse response) {
        super((HttpServletResponse) response);
    }
    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        // TODO Auto-generated method stub
        return super.getOutputStream();
    }
    @Override
    public PrintWriter getWriter() throws IOException {
        // TODO Auto-generated method stub
        return super.getWriter();
    }
    //
    @Override
    public void setContentLength(int len) {
        // TODO Auto-generated method stub
        super.setContentLength(len);
    }
    @Override
    public void setContentType(String type) {
        // TODO Auto-generated method stub
        super.setContentType(type);
    }
}