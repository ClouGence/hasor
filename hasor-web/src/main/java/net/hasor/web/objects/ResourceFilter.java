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
package net.hasor.web.objects;
import net.hasor.utils.StringUtils;
import net.hasor.utils.io.FilenameUtils;
import net.hasor.utils.io.IOUtils;
import net.hasor.utils.resource.ResourceLoader;
import net.hasor.web.Invoker;
import net.hasor.web.InvokerChain;
import net.hasor.web.InvokerFilter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

/**
 * 通过 ResourceLoader 来响应对于 Web 资源的请求。
 * @version : 2020-03-01
 * @author 赵永春 (zyc@hasor.net)
 */
public class ResourceFilter implements InvokerFilter {
    private ResourceLoader loader;

    public ResourceFilter(ResourceLoader loader) {
        this.loader = loader;
    }

    @Override
    public Object doInvoke(Invoker invoker, InvokerChain chain) throws Throwable {
        HttpServletRequest httpRequest = invoker.getHttpRequest();
        String requestURI = httpRequest.getRequestURI();
        if (!this.loader.exist(requestURI)) {
            return chain.doNext(invoker);
        }
        //
        HttpServletResponse httpResponse = invoker.getHttpResponse();
        String extension = FilenameUtils.getExtension(requestURI);
        String mimeType = invoker.getMimeType(extension);
        if (StringUtils.isNotBlank(mimeType)) {
            httpResponse.setContentType(mimeType);
        }
        //
        long size = loader.getResourceSize(requestURI);
        if (size > 0) {
            if (size >= Integer.MAX_VALUE) {
                httpResponse.setContentLengthLong(size);
            } else {
                httpResponse.setContentLength((int) size);
            }
        }
        //
        try (ServletOutputStream outputStream = httpResponse.getOutputStream()) {
            try (InputStream inputStream = loader.getResourceAsStream(requestURI)) {
                IOUtils.copy(inputStream, outputStream);
            }
        }
        return null;
    }
}
