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
package org.hasor.test;
import java.io.Writer;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.hasor.servlet.WebErrorHook;
import org.hasor.servlet.anno.WebError;
@WebError(ServletException.class)
public class WebError_500 implements WebErrorHook {
    @Override
    public void doError(ServletRequest request, ServletResponse response, Throwable error) throws Throwable {
        System.out.println(error.getMessage());
        Writer w = response.getWriter();
        w.write("----------");
        w.write("Error Msg:" + error.getMessage());
        w.flush();
    }
}
