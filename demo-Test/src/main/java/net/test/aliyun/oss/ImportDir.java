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
package net.test.aliyun.oss;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.StartModule;
import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.model.ObjectMetadata;
import com.aliyun.openservices.oss.model.PutObjectResult;
/**
 * 目录导入
 * @version : 2014年8月1日
 * @author 赵永春(zyc@hasor.net)
 */
public class ImportDir implements StartModule {
    public void loadModule(ApiBinder apiBinder) throws Throwable {}
    //
    public void onStart(AppContext appContext) throws Throwable {
        OSSClient client = appContext.getInstance(OSSClient.class);
        String localFielPath = "C:/Users/yongchun.zyc/Desktop/apis";
        String remotePath = "/hasor/apis/0.0.10";
        this.echPath(localFielPath,new File(localFielPath), client,remotePath);
    }
    //
    public void echPath(String basePath,File localFielPath, OSSClient client,String remotePath) throws FileNotFoundException {
        if (localFielPath.isFile()) {
            this.upload(basePath,localFielPath, client,remotePath);
        } else {
            File[] fs = localFielPath.listFiles();
            for (File file : fs) {
                echPath(basePath,file, client,remotePath);
            }
        }
    }
    private void upload(String basePath,File localFielPath, OSSClient client,String remotePath) {
        // 获取指定文件的输入流
        InputStream content = new FileInputStream(localFielPath);
        // 创建上传Object的Metadata
        ObjectMetadata meta = new ObjectMetadata();
        // 必须设置ContentLength
        meta.setContentLength(localFielPath.length());
        int point = localFielPath.getName().lastIndexOf('.');
        if (point != -1) {
            String mtype = sc.getMimeType("." + localFielPath.getName().substring(point));
            meta.setContentType(mtype);
        }
        // 上传Object.
        String key = localFielPath.getAbsolutePath();
        key = key.replace("\\", "/");
        key = key.substring(basePath.length());
        key = remotePath + key;
        if (key.charAt(0) == '/') {
            key = key.substring(1);
        }
        PutObjectResult result = client.putObject("www-hasor", key, content, meta);
        System.out.println("OK:" + result.getETag() + "->");
    }
}