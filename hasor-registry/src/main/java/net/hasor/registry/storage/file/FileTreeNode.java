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
import net.hasor.registry.access.adapter.ObjectData;
import net.hasor.registry.storage.mem.MenTreeNode;
import net.hasor.utils.future.FutureCallback;
import net.hasor.utils.json.JSON;

import java.io.File;
import java.io.IOException;
/**
 * 服务数据存储检索
 * @version : 2018年1月2日
 * @author 赵永春 (zyc@hasor.net)
 */
public class FileTreeNode extends MenTreeNode {
    private boolean writeToFile = true;
    private File    storagePath = null;
    private DiskIOManager diskIOManager;
    //
    public FileTreeNode(String name, MenTreeNode parent, File storagePath, DiskIOManager diskIOManager) {
        super(name, parent);
        this.storagePath = storagePath;
        this.diskIOManager = diskIOManager;
    }
    //
    //
    public void loadData() throws IOException {
        this.lockNode();
        try {
            this.writeToFile = false;
            if (!this.storagePath.exists()) {
                return;
            }
            //
            this.diskIOManager.requestRead(this.storagePath, new FutureCallback<String>() {
                @Override
                public void completed(String jsonData) {
                    updateData((ObjectData) JSON.parse(jsonData));

                }
                @Override
                public void failed(Throwable ex) {
                    notifyDie();
                }
                @Override
                public void cancelled() {
                    notifyDie();
                }
            });
            //
        } finally {
            this.writeToFile = true;
            this.unlockNode();
        }
    }
    @Override
    protected void notifyDie() {
        super.notifyDie();
        try {
            boolean delete = this.storagePath.delete();
        } catch (Throwable e) {
            //
        }
    }
    @Override
    protected void notifyUpdate(ObjectData oldData, ObjectData newData) {
        super.notifyUpdate(oldData, newData);
    }
}