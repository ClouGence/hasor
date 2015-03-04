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
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Environment;
import net.hasor.core.Hasor;
import net.hasor.core.StartModule;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.test.aliyun.OSSModule;
import net.test.aliyun.z7.Zip7Object;
import net.test.hasor.db._07_datasource.warp.OneDataSourceWarp;
import net.test.other.queue.TrackManager;
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
 * 使用 7z 转换 oss1 中的 rar 文件为 zip 格式保存到 oss2里，同时将压缩包中的文件目录保存到数据库中。
 *  -- 使用多线程无锁队列处理
 * @version : 2014年8月1日
 * @author 赵永春(zyc@hasor.net)
 */
public class Rar2Zip implements StartModule {
    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        //初始化一条铁路，铁路上的车站由TaskEnum枚举定义
        apiBinder.bindType(TrackManager.class, new TrackManager<Task>(TaskEnum.class,10,2));
    }
    @Override
    public void onStart(AppContext appContext) throws Throwable {
        JdbcTemplate jdbc = appContext.getInstance(JdbcTemplate.class);
        jdbc.queryForList("select count(*) from `oss-subtitle` where oss_key is null");
        //
        //OSS客户端，由 OSSModule 类初始化.
        OSSClient client = appContext.getInstance(OSSClient.class);
        //无锁队列由 loadModule 方法初始化.
        TrackManager<Task> track = appContext.getInstance(TrackManager.class);
        //
        //Work线程
        for (int i=0;i<15;i++){
	        TaskProcess work00 = new TaskProcess(track);
	        work00.start();
        }
        //
        ListObjectsRequest listQuery = new ListObjectsRequest("files-subtitle");
        long index = 0;
        while (true) {
            //查询列表
            ObjectListing listData = client.listObjects(listQuery);
            List<OSSObjectSummary> objSummary = listData.getObjectSummaries();
            for (OSSObjectSummary summary : objSummary) {
                //计数器
                index++;
                Task task = new Task(index, summary, appContext);
                //装货
                track.waitForWrite(TaskEnum.Task, TaskEnum.Task, task);
            }
            //设置下一个分页Mark
            listQuery.setMarker(listData.getNextMarker());
            //如果下一个分页Mark为空表示最后一页了
            if (StringUtils.isBlank(listData.getNextMarker())) {
                break;
            }
        }
        //
        while (true) {
            if (track.isEmpty())
                break;
            Thread.sleep(3000);
        }
        System.out.println("the end!");
    }
    //
    public static void main(String[] args) {
        AppContext app = Hasor.createAppContext("net/test/simple/db/jdbc-config.xml",//
                new OneDataSourceWarp(), new OSSModule(), new Rar2Zip());
        System.out.println("end");
    }
}
enum TaskEnum {
    Task
}
class TaskProcess extends Thread {
    private TrackManager<Task> track;
    public TaskProcess(TrackManager<Task> track) {
        this.track = track;
    }
    public void run() {
        this.setName("TaskProcess - " + this.getId());
        while (true) {
            this.doProcess();
        }
    }
    private void doProcess() {
        Task task = track.waitForRead(TaskEnum.Task, TaskEnum.Task);
        try {
            task.doWork();
        } catch (Throwable e) {
            task.markError(e);
        }
    }
}
/*使用 7z 转换 oss1 中的 rar 文件为 zip 格式并保存到 oss2里,同时将压缩包中的文件目录保存到数据库中。*/
class Task {
    private long           index     = 0;
    private String         tempPath  = null;
    private JdbcTemplate   jdbc      = null;
    private OSSClient      client    = null;
    private OSSObjectSummary summary =null;
    private ObjectMetadata ossObject = null;
    private String         newKey    = null;
    private String         contentDisposition = null;
    //
    public Task(long index, OSSObjectSummary summary, AppContext appContext) throws SQLException {
        this.index = index;
        this.summary=summary;
        System.out.println("init Task([" + index + "]from :" + summary.getKey() + ")");
        //
        //OSS客户端，由 OSSModule 类初始化.
        this.client = appContext.getInstance(OSSClient.class);
        //数据库操作接口，由 OneDataSourceWarp 类初始化.
        this.jdbc = appContext.getInstance(JdbcTemplate.class);
        //临时文件目录
        this.tempPath = appContext.getEnvironment().envVar(Environment.HASOR_TEMP_PATH);
        //
        this.ossObject = client.getObjectMetadata("files-subtitle", summary.getKey());
        //
        this.newKey = summary.getKey();
        newKey = newKey.substring(0, newKey.length() - ".rar".length()) + ".zip";
        this.contentDisposition = ossObject.getContentDisposition();
        this.contentDisposition = contentDisposition.substring(0, contentDisposition.length() - ".rar".length()) + ".zip";
    }
    //
    public void markError(Throwable errorMsg) {
        try {
            StringWriter sw = new StringWriter();
            errorMsg.printStackTrace(new PrintWriter(sw));
            //
            int res = jdbc.update("update `oss-subtitle-copy` set files=? ,size=-1 ,lastTime=now() where oss_key =?", sw.toString(), newKey);
            System.out.println("\t dump to db -> " + res );
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
        System.out.println("do Task " + index + "\t from :" + this.summary.getKey());
        try {
            ObjectMetadata ossObject = client.getObjectMetadata("files-subtitle-format-zip", this.newKey);
            String desc = ossObject.getContentDisposition();
            Object filesData = jdbc.queryForObject("select files from `oss-subtitle` where oss_key =? and ori_name=? and size >0" ,String.class, newKey,desc);
            if (filesData!=null){
                int res = jdbc.update("update `oss-subtitle-copy` set files=? ,size=? ,lastTime=now() where oss_key =?",
                        filesData,ossObject.getContentLength(), newKey);
                System.out.println("init task has OK dump to 'copy' table ->" + res);
                return;
            }
        } catch (Exception e) { }
//        //
//        //1.写入本地文件 
//        System.out.print("\t save to Local -> working... ");
//        OSSObject ossObject =  client.getObject("files-subtitle", summary.getKey());
//        File rarFile = new File(this.tempPath, ossObject.getObjectMetadata().getContentDisposition());
//        rarFile.getParentFile().mkdirs();
//        FileOutputStream fos = new FileOutputStream(rarFile, false);
//        InputStream inStream = ossObject.getObjectContent();
//        IOUtils.copy(inStream, fos);
//        fos.flush();
//        fos.close();
//        System.out.print("-> finish.\n");
//        //
//        //2.解压
//        System.out.print("\t extract rar -> working... ");
//        String extToosHome = "C:\\Program Files (x86)\\7-Zip";
//        String rarFileStr = rarFile.getAbsolutePath();
//        String toDir = rarFileStr.substring(0, rarFileStr.length() - ".rar".length());
//        boolean extract = Zip7Object.extract(extToosHome, rarFile.getAbsolutePath(), toDir);
//        if (extract == false) {
//            FileUtils.deleteDir(new File(toDir));
//            rarFile.delete();
//            throw new Exception("extract error.");
//        }
//        System.out.print("-> finish.\n");
//        //
//        //3.压缩
//        System.out.print("\t package zip-> working... ");
//        String zipFileName = rarFile.getAbsolutePath();
//        zipFileName = zipFileName.substring(0, zipFileName.length() - ".rar".length()) + ".zip";
//        ZipArchiveOutputStream outStream = new ZipArchiveOutputStream(new File(zipFileName));
//        outStream.setEncoding("GBK");
//        StringBuffer files = new StringBuffer();
//        Iterator<File> itFile = FileUtils.iterateFiles(new File(toDir), FileFilterUtils.fileFileFilter(), FileFilterUtils.directoryFileFilter());
//        while (itFile.hasNext()) {
//            File it = itFile.next();
//            if (it.isDirectory())
//                continue;
//            String entName = it.getAbsolutePath().substring(toDir.length() + 1);
//            outStream.putArchiveEntry(new ZipArchiveEntry(it, entName));
//            InputStream itInStream = new FileInputStream(it);
//            IOUtils.copy(itInStream, outStream);
//            itInStream.close();
//            outStream.flush();
//            outStream.closeArchiveEntry();
//            files.append(entName + "\n");
//        }
//        outStream.flush();
//        outStream.close();
//        System.out.print("-> finish.\n");
//        //
//        //4.删除临时文件
//        FileUtils.deleteDir(new File(toDir));
//        System.out.print("\t delete temp dir -> finish.\n");
//        //
//        //5.save to
//        System.out.print("\t save to oss -> working... ");
//        
//        ObjectMetadata omd = ossObject.getObjectMetadata();
//        omd.setContentDisposition(contentDisposition);
//        omd.setContentLength(new File(zipFileName).length());
//        InputStream zipInStream = new FileInputStream(zipFileName);
//        PutObjectResult result = client.putObject("files-subtitle-format-zip", newKey, zipInStream, omd);
//        zipInStream.close();
//        new File(zipFileName).delete();
//        System.out.print("-> OK:" + result.getETag());
//        System.out.print("-> finish.\n");
//        //
//        //6.save files info
//        int res = jdbc.update("update `oss-subtitle-copy` set files=? , size=? , lastTime=now() where oss_key =?",
//        		files.toString(), omd.getContentLength(),newKey);
//        System.out.println("\t save info to db -> " + res);
        //
    }
}