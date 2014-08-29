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
package net.test.web.startup.module;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javax.servlet.ServletContext;
import net.hasor.core.AppContext;
import net.hasor.core.AppContextAware;
import net.hasor.core.EventListener;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.model.ObjectMetadata;
import com.aliyun.openservices.oss.model.PutObjectResult;
/**
 * 
 * @version : 2014年8月1日
 * @author 赵永春(zyc@hasor.net)
 */
public class OSSModule extends WebModule {
    public static String BasePath = "C:/Users/yongchun.zyc/Desktop/apis";
    //
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        String accessKeyId = "Rmf8CTHe7Bq4DXRY";
        String accessKeySecret = "nqEa79FsiZ1nQzw3wUTkT54AKevA82";
        // 初始化一个OSSClient
        final OSSClient client = new OSSClient(accessKeyId, accessKeySecret);
        apiBinder.bindType(OSSClient.class).toInstance(client);
        //
        apiBinder.addListener(AppContext.ContextEvent_Started, new EventListener() {
            public void onEvent(String event, Object[] params) throws Throwable {
                //echPath((AppContext) params[0], new File(BasePath));
                System.out.println(">>>>>>>>>>>>>>>>完成<<<<<<<<<<<<<<<<<");
            }
        });
        //        //
        Upload up = apiBinder.autoAware(new Upload());
        apiBinder.addListener("UPLOAD", up);
    }
    // 
    public void echPath(AppContext app, File path) throws FileNotFoundException {
        if (path.isFile()) {
            app.fireAsyncEvent("UPLOAD", path);
        } else {
            File[] fs = path.listFiles();
            for (File file : fs) {
                echPath(app, file);
            }
        }
    }
}
class Upload implements EventListener, AppContextAware {
    private OSSClient      client = null;
    private ServletContext sc     = null;
    public void setAppContext(AppContext appContext) {
        this.client = appContext.getInstance(OSSClient.class);
        this.sc = appContext.getInstance(ServletContext.class);
    }
    public void onEvent(String event, Object[] params) throws Throwable {
        File file = (File) params[0];
        try {
            // 获取指定文件的输入流
            InputStream content = new FileInputStream(file);
            // 创建上传Object的Metadata
            ObjectMetadata meta = new ObjectMetadata();
            // 必须设置ContentLength
            meta.setContentLength(file.length());
            int point = file.getName().lastIndexOf('.');
            if (point != -1) {
                String mtype = sc.getMimeType("." + file.getName().substring(point));
                meta.setContentType(mtype);
            }
            // 上传Object.
            String key = file.getAbsolutePath();
            key = key.replace("\\", "/");
            key = key.substring(OSSModule.BasePath.length());
            if (key.charAt(0) == '/')
                key = key.substring(1);
            PutObjectResult result = client.putObject("www-hasor", key, content, meta);
            System.out.println("OK:" + result.getETag() + "->");
        } catch (Exception e) {
            System.err.println("\nError ->" + file.toString() + "\n");
        }
    }
}