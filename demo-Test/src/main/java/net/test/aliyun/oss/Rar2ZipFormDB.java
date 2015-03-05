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
import javax.sql.DataSource;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.StartModule;
import net.hasor.db.orm.PageResult;
import net.hasor.db.orm.Paginator;
import net.hasor.db.orm.Paginator.Order.OrderBy;
import net.hasor.db.orm.ar.ArConfiguration;
import net.hasor.db.orm.ar.DataBase;
import net.hasor.db.orm.ar.Record;
import net.hasor.db.orm.ar.Sechma;
import net.hasor.db.orm.ar.dialect.SQLBuilderEnum;
import net.hasor.db.orm.ar.record.MapRecord;
import net.test.aliyun.OSSModule;
import net.test.aliyun.oss.ent.SubtitleBean;
import net.test.aliyun.z7.AbstractTask;
import net.test.aliyun.z7.DownLoadTest;
import net.test.hasor.db._07_datasource.warp.OneDataSourceWarp;
import net.test.other.queue.TrackManager;
/**
 * 使用 7z 转换 oss1 中的 rar 文件为 zip 格式保存到 oss2里，同时将压缩包中的文件目录保存到数据库中。
 *  -- 使用多线程无锁队列处理
 * @version : 2014年8月1日
 * @author 赵永春(zyc@hasor.net)
 */
public class Rar2ZipFormDB implements StartModule {
    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        //初始化一条铁路，铁路上的车站由TaskEnum枚举定义
        apiBinder.bindType(TrackManager.class, new TrackManager<AbstractTask>(TaskEnum.class, 10, 2));
    }
    @Override
    public void onStart(AppContext appContext) throws Throwable {
        DataSource dataSource = appContext.getInstance(DataSource.class);
        DataBase db = new DataBase(dataSource, SQLBuilderEnum.MySql);
        db.queryBySQL("select * from `oss-subtitle` where 1=2");
        //
        //无锁队列由 loadModule 方法初始化.
        TrackManager<AbstractTask> track = appContext.getInstance(TrackManager.class);
        //Work线程
        for (int i = 0; i < 10; i++) {
            TaskProcess work00 = new TaskProcess(track);
            work00.start();
        }
        //
        ArConfiguration arConfig = new ArConfiguration();
        Sechma sechma = arConfig.loadSechma(SubtitleBean.class);
        Record example = new MapRecord(sechma);
        example.set("size", 0);
        //
        Paginator page = new Paginator();
        page.setEnable(true);
        page.setTotalCount(db.getJdbc().queryForInt("select count(*) from `oss-subtitle-copy` where `size` > 0"));
        page.setPageSize(500);
        page.addOrderBy("oss_key", OrderBy.ASC);
        //
        int errorIndex = 0;
        for (int i = 0; i < page.getTotalPage(); i++) {
            page.setCurrentPage(i);
            PageResult<Record> result = db.queryBySQL("select * from `oss-subtitle-copy` where `size` > 0", page);
            //
            for (Record rec : result.getResult()) {
                //计数器
                errorIndex++;
                //
                String newKey = rec.asString("oss_key");;
                String oldKey = newKey.substring(0, newKey.length() - ".zip".length()) + ".rar";
                AbstractTask task = new DownLoadTest(errorIndex, oldKey, appContext);
                //装货
                track.waitForWrite(TaskEnum.Task, TaskEnum.Task, task);
                System.out.println(errorIndex + "\t" + rec.asString("oss_key"));
            }
        }
        //
        while (true) {
            if (track.isEmpty())
                break;
            Thread.sleep(3000);
        }
        System.out.println(errorIndex + " <-the end!");
    }
    //
    public static void main(String[] args) {
        AppContext app = Hasor.createAppContext("net/test/simple/db/jdbc-config.xml",//
                new OneDataSourceWarp(), new OSSModule(), new Rar2ZipFormDB());
        System.out.println("end");
    }
    enum TaskEnum {
        Task
    }
    class TaskProcess extends Thread {
        private TrackManager<AbstractTask> track;
        public TaskProcess(TrackManager<AbstractTask> track) {
            this.track = track;
        }
        public void run() {
            this.setName("TaskProcess - " + this.getId());
            while (true) {
                this.doProcess();
            }
        }
        private void doProcess() {
            AbstractTask task = track.waitForRead(TaskEnum.Task, TaskEnum.Task);
            try {
                task.doWork();
            } catch (Throwable e) {
                task.markError(e);
            }
        }
    }
}
