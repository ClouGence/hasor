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
package net.hasor.web.upload.factorys.disk;
import net.hasor.utils.IOUtils;
import net.hasor.web.FileItemStream;
import net.hasor.web.upload.FileItemBase;
import net.hasor.web.upload.util.DeferredFileOutputStream;

import java.io.*;
/**
 * 磁盘缓存,50KB以内的数据在内存中驻留,超过50KB的数据全部走磁盘缓存。
 * @version : 2016-08-31
 * @author 赵永春 (zyc@hasor.net)
 */
public class DiskFileItem extends FileItemBase {
    public static final int DEFAULT_SIZE_THRESHOLD = 51200;// 50KB
    private DeferredFileOutputStream dfos;
    private File                     cacheFile;
    public DiskFileItem(FileItemStream stream, File cacheFile) throws IOException {
        super(stream);
        this.init(stream, cacheFile);
    }
    protected void init(FileItemStream stream, File cacheFile) throws IOException {
        this.cacheFile = cacheFile;
        File parent = cacheFile.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        this.dfos = new DeferredFileOutputStream(DEFAULT_SIZE_THRESHOLD, cacheFile);
        IOUtils.copy(stream.openStream(), this.dfos);
    }
    @Override
    protected void finalize() throws Throwable {
        try {
            this.deleteOrSkip();
        } finally {
            super.finalize();
        }
    }
    @Override
    public long getSize() {
        if (this.dfos.isInMemory()) {
            return this.dfos.getData().length;
        } else {
            return this.dfos.getFile().length();
        }
    }
    @Override
    public void deleteOrSkip() {
        if (this.cacheFile != null && this.cacheFile.exists()) {
            this.cacheFile.delete();
        }
    }
    @Override
    public InputStream openStream() throws IOException {
        if (this.dfos.isInMemory()) {
            return new ByteArrayInputStream(this.dfos.getData());
        } else {
            return new FileInputStream(this.dfos.getFile());
        }
    }
}