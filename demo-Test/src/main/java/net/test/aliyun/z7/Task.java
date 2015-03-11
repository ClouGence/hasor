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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Iterator;

import net.hasor.core.AppContext;
import net.hasor.core.Environment;
import net.hasor.db.jdbc.core.JdbcTemplate;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.more.util.io.FileFilterUtils;
import org.more.util.io.FileUtils;
import org.more.util.io.IOUtils;

import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.model.OSSObject;
import com.aliyun.openservices.oss.model.ObjectMetadata;
import com.aliyun.openservices.oss.model.PutObjectResult;
/**
 * 使用 7z 转换 oss1 中的 rar 文件为 zip 格式并保存到 oss2里,同时将压缩包中的文件目录保存到数据库中。
 * @version : 2014年8月1日
 * @author 赵永春(zyc@hasor.net)
 */
/* */
public class Task extends AbstractTask {
    private long           index              = 0;
    private String         tempPath           = null;
    private JdbcTemplate   jdbc               = null;
    private OSSClient      client             = null;
    private String         newKey             = null;
    private String         oldKey             = null;
    //
    public Task(long index, String newKey, AppContext appContext) throws SQLException {
        this.index = index;
        System.out.println("init Task([" + index + "]from :" + newKey + ")");
        //
        //OSS客户端，由 OSSModule 类初始化.
        this.client = appContext.getInstance(OSSClient.class);
        //数据库操作接口，由 OneDataSourceWarp 类初始化.
        this.jdbc = appContext.getInstance(JdbcTemplate.class);
        //临时文件目录
        this.tempPath = appContext.getEnvironment().envVar(Environment.HASOR_TEMP_PATH);
        //
        this.newKey = newKey;
        this.oldKey = newKey.substring(0, newKey.length() - ".zip".length()) + ".rar";
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
        System.out.println("do Task " + index + "\t from :" + oldKey);
        //
        //1.写入本地文件 
        System.out.print("\t save to Local -> working... ");
        OSSObject ossObject = client.getObject("files-subtitle", oldKey);
        File rarFile = new File(this.tempPath, ossObject.getObjectMetadata().getContentDisposition());
        rarFile.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(rarFile, false);
        InputStream inStream = ossObject.getObjectContent();
        IOUtils.copy(inStream, fos);
        fos.flush();
        fos.close();
        System.out.print("-> finish.\n");
        //
        //2.解压
        System.out.print("\t extract rar -> working... ");
        String extToosHome = "C:\\Program Files (x86)\\7-Zip";
        String rarFileStr = rarFile.getAbsolutePath();
        String toDir = rarFileStr.substring(0, rarFileStr.length() - ".rar".length());
        //
        //
        int extract = Zip7Object.extract(extToosHome, rarFile.getAbsolutePath(), toDir);
        if (extract != 0) {
            if (extract != 2)
                System.out.println();
            FileUtils.deleteDir(new File(toDir));
            rarFile.delete();
            throw new Exception("extract error.");
        }
        System.out.print("-> finish.\n");
        //
        //3.压缩
        System.out.print("\t package zip-> working... ");
        String zipFileName = rarFile.getAbsolutePath();
        zipFileName = zipFileName.substring(0, zipFileName.length() - ".rar".length()) + ".zip";
        ZipArchiveOutputStream outStream = new ZipArchiveOutputStream(new File(zipFileName));
        outStream.setEncoding("GBK");
        Iterator<File> itFile = FileUtils.iterateFiles(new File(toDir), FileFilterUtils.fileFileFilter(), FileFilterUtils.directoryFileFilter());
        StringBuffer buffer = new StringBuffer();
        while (itFile.hasNext()) {
            File it = itFile.next();
            if (it.isDirectory())
                continue;
            String entName = it.getAbsolutePath().substring(toDir.length() + 1);
            ZipArchiveEntry ent = new ZipArchiveEntry(it, entName);
            outStream.putArchiveEntry(ent);
            InputStream itInStream = new FileInputStream(it);
            IOUtils.copy(itInStream, outStream);
            itInStream.close();
            outStream.flush();
            outStream.closeArchiveEntry();
            buffer.append(ent.getName());
        }
        outStream.flush();
        outStream.close();
        System.out.print("-> finish.\n");
        //
        //4.删除临时文件
        FileUtils.deleteDir(new File(toDir));
        System.out.print("\t delete temp dir -> finish.\n");
        //
        //5.save to
        System.out.print("\t save to oss -> working... ");
        ObjectMetadata omd = ossObject.getObjectMetadata();
        String contentDisposition = omd.getContentDisposition();
        contentDisposition = contentDisposition.substring(0, contentDisposition.length() - ".rar".length()) + ".zip";
        omd.setContentDisposition(contentDisposition);
        omd.setContentLength(new File(zipFileName).length());
        InputStream zipInStream = new FileInputStream(zipFileName);
        PutObjectResult result = client.putObject("files-subtitle-format-zip", newKey, zipInStream, omd);
        zipInStream.close();
        new File(zipFileName).delete();
        System.out.print("-> OK:" + result.getETag());
        System.out.print("-> finish.\n");
        //
        //6.save files info
        int res = jdbc.update("update `oss-subtitle` set files=? , size=? , lastTime=now() where oss_key =?", buffer.toString(), omd.getContentLength(), newKey);
        System.out.println("\t save info to db -> " + res);
    }
}