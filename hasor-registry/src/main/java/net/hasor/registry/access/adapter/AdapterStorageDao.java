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
package net.hasor.registry.access.adapter;
import net.hasor.core.*;
import net.hasor.registry.access.ServerSettings;
import net.hasor.utils.Objects;

import java.util.List;
/**
 * 服务数据存储检索适配器，负责链接真实的存储器。
 * @version : 2018年1月2日
 * @author 赵永春 (zyc@hasor.net)
 */
@Singleton
public class AdapterStorageDao implements StorageDao {
    @Inject
    private ServerSettings serverSettings;
    @Inject
    private AppContext     appContext;
    private StorageDao     realStorage;
    @Init
    public void init() throws Exception {
        String defaultStoreage = Objects.requireNonNull(this.serverSettings.getDefaultStoreage(), "rsf-center default storage is undefined.");
        String configName = Objects.requireNonNull(this.serverSettings.getStoreageConfig().get(defaultStoreage), defaultStoreage + " storage config is undefined.");
        //
        XmlNode xmlNode = appContext.getEnvironment().getSettings().getXmlNode(configName);
        Class<? extends StorageDaoCreater> creater = (Class<? extends StorageDaoCreater>) appContext.getClassLoader().loadClass(xmlNode.getText().trim());
        this.realStorage = creater.newInstance().create(this.appContext, xmlNode);
    }
    //
    //
    //
    @Override
    public boolean saveData(String dataPath, ObjectData data) {
        return this.realStorage.saveData(dataPath, data);
    }
    @Override
    public boolean deleteData(String dataPath) {
        return this.realStorage.deleteData(dataPath);
    }
    @Override
    public ObjectData getByPath(String dataPath) {
        return this.realStorage.getByPath(dataPath);
    }
    @Override
    public int querySubCount(String dataPath) {
        return this.realStorage.querySubCount(dataPath);
    }
    @Override
    public List<String> querySubPathList(String dataPath, int rowIndex, int limit) {
        return this.realStorage.querySubPathList(dataPath, rowIndex, limit);
    }
}