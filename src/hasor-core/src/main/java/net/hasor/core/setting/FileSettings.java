/*
 * Copyright 2008-2009 the original author or authors.
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
package net.hasor.core.setting;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.hasor.Hasor;
/***
 * 传入File的方式获取Settings接口的支持。
 * @version : 2013-9-8
 * @author 赵永春 (zyc@byshell.org)
 */
public class FileSettings extends InputStreamSettings {
    /**创建{@link FileSettings}对象。*/
    public FileSettings() throws IOException {
        super();
    }
    /**创建{@link FileSettings}对象。*/
    public FileSettings(String fileName) throws IOException {
        this();
        Hasor.assertIsNotNull(fileName);
        this.addFile(fileName);
        this.refresh();
    }
    /**创建{@link FileSettings}对象。*/
    public FileSettings(String[] fileNames) throws IOException {
        this();
        Hasor.assertIsNotNull(fileNames);
        for (String fileName : fileNames) {
            Hasor.assertIsNotNull(fileName);
            this.addFile(fileName);
        }
        this.refresh();
    }
    /**创建{@link FileSettings}对象。*/
    public FileSettings(File settingsFile) throws IOException {
        this();
        Hasor.assertIsNotNull(settingsFile);
        this.addFile(settingsFile);
        this.refresh();
    }
    /**创建{@link FileSettings}对象。*/
    public FileSettings(File[] settingsFiles) throws IOException {
        this();
        Hasor.assertIsNotNull(settingsFiles);
        for (File settingsFile : settingsFiles) {
            Hasor.assertIsNotNull(settingsFile);
            this.addFile(settingsFile);
        }
        this.refresh();
    }
    //
    //
    //
    protected static class FileEntity {
        public long hashID;
        public File entity;
    }
    private Set<Long>        fileHash = new HashSet<Long>();
    private List<FileEntity> fileList = new ArrayList<FileEntity>();
    //
    /**添加一个Settings配置文件。*/
    public void addFile(String fileName) throws IOException {
        Hasor.assertIsNotNull(fileName);
        this.addFile(new File(fileName));
    }
    /**添加一个Settings配置文件。*/
    public void addFile(File settingsFile) throws IOException {
        Hasor.assertIsNotNull(settingsFile);
        if (settingsFile.exists() == false || settingsFile.canRead() == false || settingsFile.isDirectory() == true)
            throw new IOException("file can not  read , not exists or is directory.");
        //
        FileEntity fe = new FileEntity();
        fe.hashID = settingsFile.getAbsolutePath().hashCode();
        fe.entity = settingsFile;
        //
        if (this.fileHash.contains(fe.hashID) == true)
            return;
        //
        this.fileList.add(fe);
        this.fileHash.add(fe.hashID);
    }
    /**重新装载所有配置文件。*/
    public synchronized void refresh() throws IOException {
        Hasor.info("reload configuration.");
        this.getNamespaceSettingMap().clear();
        this.getSettingsMap().removeAllMap();
        //
        for (FileEntity feItem : this.fileList) {
            Hasor.info("addStream FileEntity ID is %s file is %s.", feItem.hashID, feItem.entity);
            this.addStream(new FileInputStream(feItem.entity));
        }
        //
        try {
            this.loadSettings();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}