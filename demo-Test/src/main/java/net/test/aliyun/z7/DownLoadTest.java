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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

import net.hasor.core.AppContext;
import net.hasor.core.Environment;
import net.hasor.db.jdbc.core.JdbcTemplate;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.more.util.io.IOUtils;

import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.model.OSSObject;
import com.aliyun.openservices.oss.model.ObjectMetadata;
/**
 * 使用 7z 转换 oss1 中的 rar 文件为 zip 格式并保存到 oss2里,同时将压缩包中的文件目录保存到数据库中。
 * @version : 2014年8月1日
 * @author 赵永春(zyc@hasor.net)
 */
/* */
public class DownLoadTest extends AbstractTask {
    private long         index    = 0;
    private String       tempPath = null;
    private JdbcTemplate jdbc     = null;
    private OSSClient    client   = null;
    private String       newKey   = null;
    //
    public DownLoadTest(long index, String newKey, AppContext appContext) throws SQLException {
        this.index = index;
        System.out.println("init Task([" + index + "]from :" + newKey + ")");
        //
        //OSS客户端，由 OSSModule 类初始化.
        this.client = appContext.getInstance(OSSClient.class);
        //数据库操作接口，由 OneDataSourceWarp 类初始化.
        this.jdbc = appContext.getInstance(JdbcTemplate.class);
        this.newKey = newKey;
        //临时文件目录
        this.tempPath = appContext.getEnvironment().envVar(Environment.HASOR_TEMP_PATH);
    }
    //
    public void markError(Throwable errorMsg) {
        try {
            StringWriter sw = new StringWriter();
            errorMsg.printStackTrace(new PrintWriter(sw));
            //
            int res = jdbc.update("update `oss-subtitle` set files =null ,lastTime=now() where oss_key =?",newKey);
            System.out.println("\t dump to db -> " + res);
        } catch (Throwable e) {
            try {
                String dumpName = newKey.substring(newKey.lastIndexOf("/"), newKey.length());
                File dumpFile = new File(tempPath, dumpName);
                dumpFile.getParentFile().mkdirs();
                FileOutputStream fos = new FileOutputStream(dumpFile + ".log");
                e.printStackTrace(new PrintStream(fos, true));
                fos.flush();
                fos.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
    // 
    public void doWork() throws Throwable {
        //init task to DB
        System.out.println("do Task " + index + "\t from :" + this.newKey);
        //
        try {
            OSSObject ossObject = client.getObject("files-subtitle-format-zip", this.newKey);
            InputStream infromOSS =ossObject.getObjectContent();
            ZipArchiveInputStream inStream = new ZipArchiveInputStream(infromOSS,"GBK");
            ZipArchiveEntry entry = null;
            StringBuffer buffer = new StringBuffer();
            while ((entry = inStream.getNextZipEntry()) != null) {
                if (entry.isDirectory())
                    continue;
                IOUtils.copy(inStream, new ByteArrayOutputStream());
                buffer.append(entry.getName() + "\n");
            }
            infromOSS.close();
            //
            ObjectMetadata meta = ossObject.getObjectMetadata();
            int res = jdbc.update("update `oss-subtitle` set files=?,size=?,lastTime=now() where oss_key =?",
            		buffer.toString(),meta.getContentLength(), newKey);
            System.out.println(this.index + " - Validation ok. -> " + res);
            if (res == 0 ){
            	res = jdbc.update("insert into `oss-subtitle` (oss_key,files,ori_name,size,lastTime,doWork) values (?,?,?,?,now(),0)",
            			newKey,buffer.toString(),meta.getContentDisposition(),meta.getContentLength());
            }
            //
        } catch (Throwable e) {
        	e.printStackTrace();
            //
            int res = jdbc.update("update `oss-subtitle` set files=null,lastTime=now() where oss_key =?",newKey);
            System.out.println("\t error dump to db -> " + res);
        }
    }
}