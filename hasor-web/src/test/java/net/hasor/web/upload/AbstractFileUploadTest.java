package net.hasor.web.upload;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;
import net.hasor.core.AppContext;
import net.hasor.core.Module;
import net.hasor.utils.CommonCodeUtils;
import net.hasor.utils.future.BasicFuture;
import net.hasor.web.AbstractTest;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.powermock.api.mockito.PowerMockito;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AbstractFileUploadTest extends AbstractTest {
    protected String md5(String datas) {
        try {
            return CommonCodeUtils.MD5.getMD5(datas);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    protected String md5(byte[] datas) {
        try {
            return CommonCodeUtils.MD5.encodeMD5(datas);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    protected void doUploadTest(String requestURI, Module module, DoTest doTest) throws Exception {
        BasicFuture<Object> basicFuture = new BasicFuture<>();
        //
        // .随机数据
        byte[] bytes = new byte[1024 * 1024];
        new Random(System.currentTimeMillis()).nextBytes(bytes);
        HashMap<String, Object> oriData = new HashMap<String, Object>() {{
            put("bytes", bytes);
            put("fileName", "test_file");
        }};
        //
        // .Http Server
        HttpServer server = HttpServer.create(new InetSocketAddress(8001), 0);
        server.createContext(requestURI, httpExchange -> {
            Headers requestHeaders = httpExchange.getRequestHeaders();
            Map<String, String[]> headerMap = new HashMap<>();
            requestHeaders.forEach((key, strings) -> headerMap.put(key, strings.toArray(new String[0])));
            InputStream inputStream = httpExchange.getRequestBody();
            //
            HttpServletRequest mockRequest = mockRequest("post", new URL("http://www.hasor.net" + requestURI), headerMap, null, null);
            PowerMockito.when(mockRequest.getInputStream()).thenReturn(new InnerInputStream(inputStream));
            PowerMockito.when(mockRequest.getContentType()).thenReturn(httpExchange.getRequestHeaders().getFirst("Content-type"));
            //
            AppContext appContext = buildWebAppContext("/META-INF/hasor-framework/web-hconfig.xml", apiBinder -> {
                apiBinder.installModule(module);
            }, servlet25("/"), LoadModule.Web);
            //
            //
            try {
                Object o = callInvoker(appContext, mockRequest);
                if (o != null) {
                    doTest.doTest(oriData, o);
                } else {
                    throw new IllegalStateException("not call test.");
                }
                //
                httpExchange.sendResponseHeaders(200, 0);
                basicFuture.completed(o);
            } catch (Throwable throwable) {
                basicFuture.failed(throwable);
                httpExchange.sendResponseHeaders(500, 0);
            }
            httpExchange.getResponseBody().close();
        });
        server.start();
        //
        // .发起文件上传
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        oriData.forEach((key, val) -> {
            if (val instanceof byte[]) {
                entityBuilder.addBinaryBody(key, (byte[]) val);
            } else if (val instanceof File) {
                entityBuilder.addPart(key, new FileBody((File) val));
            } else {
                entityBuilder.addTextBody(key, val.toString());
            }
        });
        //
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpPost httpRequest = new HttpPost("http://localhost:8001" + requestURI);
            httpRequest.setEntity(entityBuilder.build());
            client.execute(httpRequest);
            basicFuture.get();
        } finally {
            server.stop(1);
        }
    }
}