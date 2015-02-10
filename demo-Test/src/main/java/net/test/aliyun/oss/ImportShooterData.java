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
import java.io.IOException;
import java.util.Enumeration;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Environment;
import net.hasor.core.Hasor;
import net.hasor.core.StartModule;
import net.test.aliyun.OSSModule;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.model.ObjectMetadata;
/**
 * 射手 数据 导入
 * @version : 2014年8月1日
 * @author 赵永春(zyc@hasor.net)
 */
public class ImportShooterData implements StartModule {
    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {}
    @Override
    public void onStart(AppContext appContext) throws Throwable {
        String BasePath = "D:/shooterData/";
        OSSClient client = appContext.getInstance(OSSClient.class);
        String tempPath = appContext.getEnvironment().envVar(Environment.HASOR_TEMP_PATH);
        //
        File[] zipPacks = new File(BasePath).listFiles();
        long intCount = 0;
        long size = 0;
        for (File zipfile : zipPacks) {
            String fileName = zipfile.getName();
            fileName = fileName.split("\\.")[0];
            ZipFile zipPack = new ZipFile(zipfile);
            Enumeration<ZipArchiveEntry> enumZip = zipPack.getEntries();
            System.out.println(fileName);
            while (enumZip.hasMoreElements()) {
                ZipArchiveEntry ent = enumZip.nextElement();
                if (ent.isDirectory() == true) {
                    continue;
                }
                String itemName = ent.getName();
                //
                //				ObjectMetadata info = this.passInfo(tempPath, zipPack, ent);
                //				info.addUserMetadata("oldFileName", itemName);
                //
                //				String key = fileName + "/" + UUID.randomUUID().toString().replace("-", "") + ".rar";
                //InputStream inStream = zipPack.getInputStream(ent);
                //PutObjectResult res = client.putObject("files-subtitle", key, inStream, info);
                //
                intCount++;
                long itemSize = ent.getSize();
                String stated = String.format("%s-%s/%s\t%s\t%s", intCount, fileName, itemName, itemSize, "");
                System.out.println(stated + " -> " + "");
                size = size + itemSize;
            }
            zipPack.close();
        }
        System.out.println(intCount + "\t" + size);
    }
    private ObjectMetadata passInfo(String tempPath, ZipFile zipPack, ZipArchiveEntry ent) throws IOException {
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentLength(ent.getSize());
        meta.setContentDisposition(ent.getName() + ".rar");
        return meta;
    }
    public static void main(String[] args) {
        AppContext app = Hasor.createAppContext(new OSSModule(), new ImportShooterData());
        System.out.println("end");
    }
}