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
import net.hasor.core.Inject;
import net.hasor.registry.storage.mem.MemStorageDao;
import net.hasor.registry.storage.mem.MenTreeNode;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.utils.Objects;
import net.hasor.utils.StringUtils;

import java.io.File;
import java.io.IOException;
/**
 * 服务数据存储检索
 * @version : 2018年1月2日
 * @author 赵永春 (zyc@hasor.net)
 */
public class FileStorageDao extends MemStorageDao {
    @Inject
    private RsfEnvironment rsfEnvironment;
    @Inject
    private DiskIOManager  diskIOManager;
    private File           storageDir;
    //
    //
    //
    //
    public void initDao(File storageDir) throws IOException {
        this.storageDir = Objects.requireNonNull(storageDir);
        super.initDao();
        ((FileTreeNode) this.rootNode).loadData();
    }
    @Override
    public FileTreeNode createTreeNode(String name, MenTreeNode parent) {
        String nodeDataPath = (StringUtils.isBlank(name) ? "" : name + "/") + "data.json";
        return new FileTreeNode(name, parent, new File(this.storageDir, nodeDataPath), this.diskIOManager);
    }
}