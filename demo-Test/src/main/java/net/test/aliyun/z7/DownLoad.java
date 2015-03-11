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
package net.test.aliyun.z7;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.SQLException;
import net.hasor.core.AppContext;
import net.hasor.core.Environment;
import org.more.util.io.IOUtils;
import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.model.OSSObject;
/**
 * 使用 7z 转换 oss1 中的 rar 文件为 zip 格式并保存到 oss2里,同时将压缩包中的文件目录保存到数据库中。
 * @version : 2014年8月1日
 * @author 赵永春(zyc@hasor.net)
 */
/* */
public class DownLoad extends AbstractTask {
    private long      index    = 0;
    private String    tempPath = null;
    private OSSClient client   = null;
    private String    newKey   = null;
    //
    public DownLoad(long index, String newKey, AppContext appContext) throws SQLException {
        this.index = index;
        System.out.println("init Task([" + index + "]from :" + newKey + ")");
        //
        //OSS客户端，由 OSSModule 类初始化.
        this.client = appContext.getInstance(OSSClient.class);
        this.newKey = newKey;
        //临时文件目录
        this.tempPath = appContext.getEnvironment().envVar(Environment.HASOR_TEMP_PATH);
    }
    //
    public void markError(Throwable errorMsg) {
        System.out.println("");
    }
    // 
    public void doWork() throws Throwable {
        //init task to DB
        System.out.println("do Task " + index + "\t from :" + newKey);
        //
        //1.写入本地文件 
        System.out.print("\t save to Local -> working... ");
        OSSObject ossObject = client.getObject("files-subtitle-format-zip", newKey);
        File rarFile = new File(this.tempPath, ossObject.getObjectMetadata().getContentDisposition());
        rarFile.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(rarFile, false);
        InputStream inStream = ossObject.getObjectContent();
        IOUtils.copy(inStream, fos);
        fos.flush();
        fos.close();
        System.out.print("-> finish.\n");
        //
        client.deleteObject("files-subtitle-format-zip", newKey);
    }
}