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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Environment;
import net.hasor.core.Hasor;
import net.hasor.core.StartModule;
import net.test.aliyun.OSSModule;
import net.test.aliyun.z7.Zip7Object;
import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.more.util.StringUtils;
import org.more.util.io.FileFilterUtils;
import org.more.util.io.FileUtils;
import org.more.util.io.IOUtils;
import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.model.ListObjectsRequest;
import com.aliyun.openservices.oss.model.OSSObject;
import com.aliyun.openservices.oss.model.OSSObjectSummary;
import com.aliyun.openservices.oss.model.ObjectListing;
import com.aliyun.openservices.oss.model.ObjectMetadata;
import com.aliyun.openservices.oss.model.PutObjectResult;
/**
 * 射手 数据遍历
 * @version : 2014年8月1日
 * @author 赵永春(zyc@hasor.net)
 */
public class Rar2Zip implements StartModule {
    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {}
    @Override
    public void onStart(AppContext appContext) throws Throwable {
        OSSClient client = appContext.getInstance(OSSClient.class);
        ListObjectsRequest listQuery = new ListObjectsRequest("files-subtitle");
        //
        long index = 0;
        while (true) {
            ObjectListing listData = client.listObjects(listQuery);
            List<OSSObjectSummary> objSummary = listData.getObjectSummaries();
            for (OSSObjectSummary summary : objSummary) {
                index++;
                System.out.println(index + "\t from :" + summary.getKey());
                rar2zip(summary, client, appContext);
            }
            //
            listQuery.setMarker(listData.getNextMarker());
            if (StringUtils.isBlank(listData.getNextMarker())) {
                break;
            }
        }
    }
    public void rar2zip(OSSObjectSummary summary, OSSClient client, AppContext appContext) throws Throwable {
        String tempPath = appContext.getEnvironment().envVar(Environment.HASOR_TEMP_PATH);
        //
        //1.写入本地文件 
        System.out.print("\t save to Local -> working... ");
        OSSObject ossObject = client.getObject("files-subtitle", summary.getKey());
        String contentDisposition = ossObject.getObjectMetadata().getContentDisposition();
        File rarFile = new File(tempPath, ossObject.getObjectMetadata().getContentDisposition());
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
        Zip7Object.extract(extToosHome, rarFile.getAbsolutePath(), toDir);
        System.out.print("-> finish.\n");
        //
        //3.压缩
        System.out.print("\t package zip-> working... ");
        String zipFileName = rarFile.getAbsolutePath();
        zipFileName = zipFileName.substring(0, zipFileName.length() - ".rar".length()) + ".zip";
        ZipArchiveOutputStream outStream = new ZipArchiveOutputStream(new File(zipFileName));
        outStream.setEncoding("GBK");
        Iterator<File> itFile = FileUtils.iterateFiles(new File(toDir), FileFilterUtils.fileFileFilter(), FileFilterUtils.directoryFileFilter());
        while (itFile.hasNext()) {
            File it = itFile.next();
            if (it.isDirectory())
                continue;
            String entName = it.getAbsolutePath().substring(toDir.length() + 1);
            outStream.putArchiveEntry(new ZipArchiveEntry(it, entName));
            InputStream itInStream = new FileInputStream(it);
            IOUtils.copy(itInStream, outStream);
            itInStream.close();
            outStream.flush();
            outStream.closeArchiveEntry();
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
        String ossKey = ossObject.getKey();
        ossKey = ossKey.substring(0, ossKey.length() - ".rar".length()) + ".zip";
        contentDisposition = contentDisposition.substring(0, contentDisposition.length() - ".rar".length()) + ".zip";
        ObjectMetadata omd = ossObject.getObjectMetadata();
        omd.setContentDisposition(contentDisposition);
        omd.setContentLength(new File(zipFileName).length());
        InputStream zipInStream = new FileInputStream(zipFileName);
        PutObjectResult result = client.putObject("files-subtitle-zip", ossKey, zipInStream, omd);
        zipInStream.close();
        new File(zipFileName).delete();
        System.out.print("-> OK:" + result.getETag());
        System.out.print("-> finish.\n");
        //
    }
    public static void compressFiles2Zip(File outputZipFile, File[] files) throws IOException {
        if (files != null && files.length > 0) {
            ZipArchiveOutputStream zaos = new ZipArchiveOutputStream(outputZipFile);
            zaos.setUseZip64(Zip64Mode.AsNeeded);//Use Zip64 extensions for all entries where they are required
            for (File file : files) {
                ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(file, file.getName());
                zaos.putArchiveEntry(zipArchiveEntry);
                InputStream is = new FileInputStream(file);
                IOUtils.copy(is, zaos);
                zaos.closeArchiveEntry();
            }
            zaos.finish();
        }
    }
    //
    public static void main(String[] args) {
        AppContext app = Hasor.createAppContext(new OSSModule(), new Rar2Zip());
        System.out.println("end");
    }
}