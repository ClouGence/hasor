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
package net.hasor.registry.storage.file;
import net.hasor.core.AppContext;
import net.hasor.core.Singleton;
import net.hasor.core.XmlNode;
import net.hasor.registry.access.adapter.StorageDao;
import net.hasor.registry.access.adapter.StorageDaoCreater;
import net.hasor.utils.StringUtils;

import java.io.File;
import java.io.IOException;
/**
 * 服务数据存储检索
 * @version : 2018年1月2日
 * @author 赵永春 (zyc@hasor.net)
 */
@Singleton
public class FileStorageDaoCreater implements StorageDaoCreater {
    @Override
    public StorageDao create(AppContext appContext, XmlNode config) throws IOException {
        String storageDir = config.getAttribute("dataDir");
        if (StringUtils.isBlank(storageDir)) {
            storageDir = appContext.getEnvironment().evalString("%WORK_HOME%/rsf_storage");
        }
        //
        FileStorageDao storageDao = appContext.getInstance(FileStorageDao.class);
        storageDao.initDao(new File(storageDir));
        return storageDao;
    }
}