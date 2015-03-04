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
package net.test.aliyun.oss.ent;
import java.util.Date;
import net.hasor.db.orm.ar.anno.Column;
import net.hasor.db.orm.ar.anno.Table;
/**
 * 
 * @version : 2015年3月4日
 * @author 赵永春(zyc@hasor.net)
 */
@Table(tableName = "oss-subtitle-copy", primaryKey = "ossKey")
public class SubtitleBean {
    @Column(column = "oss_key")
    private String ossKey;
    @Column(column = "files")
    private String files;
    @Column(column = "ori_name")
    private String oriName;
    @Column(column = "size")
    private long   size;
    @Column(column = "lastTime")
    private Date   lastTime;
    @Column(column = "doWork")
    private int    doWork;
    //
    public String getOssKey() {
        return ossKey;
    }
    public void setOssKey(String ossKey) {
        this.ossKey = ossKey;
    }
    public String getFiles() {
        return files;
    }
    public void setFiles(String files) {
        this.files = files;
    }
    public String getOriName() {
        return oriName;
    }
    public void setOriName(String oriName) {
        this.oriName = oriName;
    }
    public long getSize() {
        return size;
    }
    public void setSize(long size) {
        this.size = size;
    }
    public Date getLastTime() {
        return lastTime;
    }
    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }
    public int getDoWork() {
        return doWork;
    }
    public void setDoWork(int doWork) {
        this.doWork = doWork;
    }
}
