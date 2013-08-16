/*
 * Copyright 2008-2009 the original ’‘”¿¥∫(zyc@hasor.net).
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
package org.demo.errors.process;
import java.io.PrintWriter;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.demo.errors.define.LoginSecurityException;
import org.hasor.context.AppContext;
import org.hasor.servlet.ErrorHook;
import org.hasor.servlet.anno.WebError;
/**
 * 
 * @version : 2013-7-31
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@WebError(LoginSecurityException.class)
public class LoginSecurityException_Process implements ErrorHook {
    @Override
    public void init(AppContext appContext) {}
    @Override
    public void doError(ServletRequest request, ServletResponse response, Throwable error) throws Throwable {
        PrintWriter printWriter = response.getWriter();
        //
        printWriter.write("\n<br/>");
        printWriter.write("\n<h1>LoginSecurityException:");
        printWriter.write(error.getMessage() + "</h1>");
        printWriter.write("\n<br/>");
        error.printStackTrace(printWriter);
    }
    @Override
    public void destroy(AppContext appContext) {}
}