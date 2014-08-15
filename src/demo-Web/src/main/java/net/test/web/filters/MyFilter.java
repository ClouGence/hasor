/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.test.web.filters;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.hasor.web.startup.RuntimeListener;
import org.more.util.IOUtils;
import org.more.util.StringUtils;
import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.model.OSSObject;
/**
 * 
 * @version : 2014年7月24日
 * @author 赵永春(zyc@hasor.net)
 */
public class MyFilter implements Filter {
    private OSSClient client = null;
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.client = RuntimeListener.getLocalAppContext().getInstance(OSSClient.class);
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //
        HttpServletRequest req = (HttpServletRequest) request;
        String reqURI = req.getRequestURI();
        reqURI = reqURI.substring(req.getContextPath().length());
        if (StringUtils.isBlank(reqURI) == false && reqURI.charAt(0) == '/')
            reqURI = reqURI.substring(1);
        //
        if (StringUtils.isBlank(reqURI))
            reqURI = "index.html";
        //
        OSSObject obj = null;
        try {
            obj = client.getObject("www-hasor", reqURI);
            OutputStream out = response.getOutputStream();
            IOUtils.copy(obj.getObjectContent(), out);
        } catch (Exception e) {
            obj = client.getObject("www-hasor", reqURI + "/index.html");
            ((HttpServletResponse) response).sendRedirect(reqURI + "/index.html");
        }
    }
    @Override
    public void destroy() {
        // TODO Auto-generated method stub
    }
}