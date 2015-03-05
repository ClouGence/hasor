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
import java.util.List;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.StartModule;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.test.aliyun.OSSModule;
import net.test.aliyun.z7.Task;
import net.test.hasor.db._07_datasource.warp.OneDataSourceWarp;
import net.test.other.queue.TrackManager;
import org.more.util.StringUtils;
import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.model.ListObjectsRequest;
import com.aliyun.openservices.oss.model.OSSObjectSummary;
import com.aliyun.openservices.oss.model.ObjectListing;
/**
 * 使用 7z 转换 oss1 中的 rar 文件为 zip 格式保存到 oss2里，同时将压缩包中的文件目录保存到数据库中。
 *  -- 使用多线程无锁队列处理
 * @version : 2014年8月1日
 * @author 赵永春(zyc@hasor.net)
 */
public class Rar2ZipFormOSS implements StartModule {
    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        //初始化一条铁路，铁路上的车站由TaskEnum枚举定义
        apiBinder.bindType(TrackManager.class, new TrackManager<Task>(TaskEnum.class, 10, 2));
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
        for (int i = 0; i < 15; i++) {
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
                Task task = new Task(index, summary.getKey(), appContext);
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
                new OneDataSourceWarp(), new OSSModule(), new Rar2ZipFormOSS());
        System.out.println("end");
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
}