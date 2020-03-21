package net.hasor.dataway.config;
import net.hasor.utils.ResourcesUtils;
import net.hasor.utils.StringUtils;
import net.hasor.utils.io.FilenameUtils;
import net.hasor.utils.io.IOUtils;
import net.hasor.utils.io.output.ByteArrayOutputStream;
import net.hasor.web.Invoker;
import net.hasor.web.InvokerChain;
import net.hasor.web.InvokerFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import static net.hasor.dataway.config.DatawayModule.fixUrl;

class InterfaceUiFilter implements InvokerFilter {
    private              String baseUri;
    private              String baseApiUri;
    private static final String resourceBaseUri  = "/META-INF/hasor-framework/dataway-ui/";
    private              String resourceIndexUri = null;

    public InterfaceUiFilter(String baseUri) {
        this.baseUri = baseUri;
        this.baseApiUri = fixUrl(this.baseUri + "/api/");
        this.resourceIndexUri = fixUrl(this.baseUri + "/index.html");
    }

    @Override
    public Object doInvoke(Invoker invoker, InvokerChain chain) throws Throwable {
        HttpServletRequest httpRequest = invoker.getHttpRequest();
        HttpServletResponse httpResponse = invoker.getHttpResponse();
        String requestURI = httpRequest.getRequestURI();
        //
        if (requestURI.startsWith(this.baseApiUri)) {
            return chain.doNext(invoker);
        }
        //
        // 处理 index.html
        if (this.baseUri.equalsIgnoreCase(requestURI)) {
            requestURI = resourceIndexUri;
        }
        if (requestURI.equalsIgnoreCase(resourceIndexUri)) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try (InputStream inputStream = ResourcesUtils.getResourceAsStream(fixUrl(resourceBaseUri + "/index.html"))) {
                IOUtils.copy(inputStream, outputStream);
            }
            //
            String htmlBody = new String(outputStream.toByteArray());
            htmlBody = htmlBody.replace("{API_BASE_URL}", fixUrl(this.baseUri));
            httpResponse.setContentType(invoker.getMimeType("html"));
            httpResponse.setContentLength(htmlBody.length());
            PrintWriter writer = httpResponse.getWriter();
            writer.write(htmlBody);
            writer.flush();
            return null;
        }
        // 其它资源
        if (requestURI.startsWith(this.baseUri)) {
            String extension = FilenameUtils.getExtension(requestURI);
            String mimeType = invoker.getMimeType(extension);
            if (StringUtils.isNotBlank(mimeType)) {
                httpResponse.setContentType(mimeType);
            }
            //
            String resourceName = fixUrl(resourceBaseUri + requestURI.substring(this.baseUri.length()));
            //${host}
            try (OutputStream outputStream = httpResponse.getOutputStream();) {
                try (InputStream inputStream = ResourcesUtils.getResourceAsStream(resourceName)) {
                    IOUtils.copy(inputStream, outputStream);
                }
                outputStream.flush();
            }
            return null;
        }
        //
        return chain.doNext(invoker);
    }
}