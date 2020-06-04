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
package net.hasor.dataway.config;
import com.alibaba.fastjson.JSON;
import net.hasor.dataway.DatawayService;
import net.hasor.utils.ResourcesUtils;
import net.hasor.utils.StringUtils;
import net.hasor.utils.io.FilenameUtils;
import net.hasor.utils.io.IOUtils;
import net.hasor.utils.io.output.ByteArrayOutputStream;
import net.hasor.web.Invoker;
import net.hasor.web.InvokerChain;
import net.hasor.web.InvokerFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 负责UI界面资源的请求响应。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-20
 */
class InterfaceUiFilter implements InvokerFilter {
    protected static     Logger               logger           = LoggerFactory.getLogger(InterfaceUiFilter.class);
    private static final String               resourceBaseUri  = "/META-INF/hasor-framework/dataway-ui/";
    private static final String               datawayVersion;
    private              String               resourceIndexUri = null;
    private final        String               apiBaseUri;
    private final        String               uiBaseUri;
    private final        String               uiAdminBaseUri;
    private final        Map<String, Integer> resourceSize;

    public InterfaceUiFilter(String apiBaseUri, String uiBaseUri) {
        this.apiBaseUri = apiBaseUri;
        this.uiBaseUri = uiBaseUri;
        this.uiAdminBaseUri = fixUrl(uiBaseUri + "/api/");
        this.resourceIndexUri = fixUrl(uiBaseUri + "/index.html");
        this.resourceSize = new ConcurrentHashMap<>();
    }

    private static String fixUrl(String url) {
        return url.replaceAll("/+", "/");
    }

    static {
        String version = null;
        try {
            InputStream inputStream = ResourcesUtils.getResourceAsStream("/META-INF/maven/net.hasor/hasor-dataway/pom.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            version = properties.getProperty("version");
        } catch (Exception e) {
            version = DatawayService.VERSION;
        }
        datawayVersion = version;
    }

    @Override
    public Object doInvoke(Invoker invoker, InvokerChain chain) throws Throwable {
        HttpServletRequest httpRequest = invoker.getHttpRequest();
        HttpServletResponse httpResponse = invoker.getHttpResponse();
        httpRequest.setCharacterEncoding("UTF-8");
        httpResponse.setCharacterEncoding("UTF-8");
        String requestURI = invoker.getRequestPath();
        CorsUtils.setup(invoker);
        if (requestURI.startsWith(this.uiAdminBaseUri)) {
            try {
                DatawayUtils.resetLocalTime();
                return chain.doNext(invoker);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                Object objectMap = DatawayUtils.exceptionToResult(e).getResult();
                PrintWriter writer = httpResponse.getWriter();
                writer.write(JSON.toJSONString(objectMap));
                writer.flush();
                return objectMap;
            }
        }
        //
        // 处理 index.html
        if (this.uiBaseUri.equalsIgnoreCase(requestURI)) {
            requestURI = resourceIndexUri;
        }
        if (requestURI.equalsIgnoreCase(resourceIndexUri)) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try (InputStream inputStream = ResourcesUtils.getResourceAsStream(fixUrl(resourceBaseUri + "/index.html"))) {
                IOUtils.copy(inputStream, outputStream);
            }
            //
            String contextPath = httpRequest.getContextPath();
            if (StringUtils.isBlank(contextPath)) {
                contextPath = "/";
            }
            if (contextPath.endsWith("/")) {
                contextPath = contextPath.substring(0, contextPath.length() - 1);
            }
            String htmlBody = new String(outputStream.toByteArray());
            htmlBody = htmlBody.replace("{CONTEXT_PATH}", contextPath);
            htmlBody = htmlBody.replace("{API_BASE_URL}", fixUrl(this.apiBaseUri));
            htmlBody = htmlBody.replace("{ADMIN_BASE_URL}", fixUrl(this.uiBaseUri));
            htmlBody = htmlBody.replace("{ALL_MAC}", allLocalMac());
            htmlBody = htmlBody.replace("{DATAWAY_VERSION}", datawayVersion);
            httpResponse.setContentType(invoker.getMimeType("html"));
            httpResponse.setContentLength(htmlBody.length());
            PrintWriter writer = httpResponse.getWriter();
            writer.write(htmlBody);
            writer.flush();
            return null;
        }
        // 其它资源
        if (requestURI.startsWith(this.uiBaseUri)) {
            String extension = FilenameUtils.getExtension(requestURI);
            String mimeType = invoker.getMimeType(extension);
            if (StringUtils.isNotBlank(mimeType)) {
                httpResponse.setContentType(mimeType);
            }
            httpResponse.setHeader("Cache-control", "public, max-age=2592000");
            //
            String resourceName = fixUrl(resourceBaseUri + requestURI.substring(this.uiBaseUri.length()));
            try (OutputStream outputStream = httpResponse.getOutputStream()) {
                // .准备输出流
                OutputStream output = null;
                Integer size = this.resourceSize.get(resourceName);
                if (size == null) {
                    output = new ByteArrayOutputStream();
                } else {
                    output = outputStream;
                    httpResponse.setContentLength(size);
                }
                // .把数据写入流
                try (InputStream inputStream = ResourcesUtils.getResourceAsStream(resourceName)) {
                    if (inputStream == null) {
                        httpResponse.sendError(404, "not found " + requestURI);
                        return null;
                    }
                    IOUtils.copy(inputStream, output);
                } catch (Exception e) {
                    logger.error("load " + resourceName + " failed -> " + e.getMessage(), e);
                }
                // .如果是第一次，那么缓存资源长度。然后拷贝到真的流中
                if (size == null) {
                    byte[] byteArray = ((ByteArrayOutputStream) output).toByteArray();
                    size = byteArray.length;
                    this.resourceSize.put(resourceName, size);
                    outputStream.write(byteArray);
                }
                outputStream.flush();
            }
            return null;
        }
        //
        return chain.doNext(invoker);
    }

    private static String allLocalMac() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        Set<String> macPool = new HashSet<>();
        while (interfaces.hasMoreElements()) {
            NetworkInterface nextElement = interfaces.nextElement();
            byte[] hardwareAddress = nextElement.getHardwareAddress();
            if (hardwareAddress == null) {
                continue;
            }
            StringBuilder strBuilder = new StringBuilder();
            for (int i = 0; i < hardwareAddress.length; i++) {
                String str = Integer.toHexString(hardwareAddress[i] & 0xff);
                strBuilder.append((str.length() == 1) ? ("0" + str) : str);
            }
            macPool.add(strBuilder.toString());
        }
        return StringUtils.join(macPool.toArray(), ",").toUpperCase();
    }
}