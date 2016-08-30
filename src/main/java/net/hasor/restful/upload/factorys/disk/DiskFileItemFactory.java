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
package net.hasor.restful.upload.factorys.disk;
import net.hasor.restful.FileItem;
import net.hasor.restful.FileItemFactory;
import net.hasor.restful.FileItemStream;
import net.hasor.restful.upload.FileItemBase;
import org.more.util.io.IOUtils;
import org.more.util.io.output.DeferredFileOutputStream;

import java.io.*;
import java.util.UUID;
/**
 *
 * @version : 2015年2月11日
 * @author 赵永春(zyc@hasor.net)
 */
public class DiskFileItemFactory implements FileItemFactory {
    /** The default threshold above which uploads will be stored on disk. */
    public static final int DEFAULT_SIZE_THRESHOLD = 10240;
    private File cacheDirectory;
    public DiskFileItemFactory(String cacheDirectory) {
        this.cacheDirectory = new File(cacheDirectory);
    }
    @Override
    public FileItem createItem(FileItemStream itemStream) throws IOException {
        String fid = UUID.randomUUID().toString() + ".tmp";
        //
        if (itemStream.isFormField()) {
            return new MemoryFileItem(itemStream);
        } else {
            return new DiskFileItem(itemStream, DEFAULT_SIZE_THRESHOLD, new File(cacheDirectory, fid));
        }
    }
}
//
class MemoryFileItem extends FileItemBase {
    private byte[] cachedContent;
    public MemoryFileItem(FileItemStream stream) throws IOException {
        super(stream);
        ByteArrayOutputStream arrays = new ByteArrayOutputStream();
        IOUtils.copy(stream.openStream(), arrays);
        this.cachedContent = arrays.toByteArray();
    }
    @Override
    public long getSize() {
        if (this.cachedContent != null) {
            return this.cachedContent.length;
        } else {
            return 0;
        }
    }
    @Override
    public void deleteOrSkip() {
    }
    @Override
    public InputStream openStream() throws IOException {
        if (this.cachedContent == null) {
            return null;
        }
        return new ByteArrayInputStream(this.cachedContent);
    }
}
class DiskFileItem extends FileItemBase {
    private DeferredFileOutputStream dfos;
    private File                     cacheFile;
    public DiskFileItem(FileItemStream stream, int sizeThreshold, File cacheFile) throws IOException {
        super(stream);
        this.init(stream, sizeThreshold, cacheFile);
    }
    protected void init(FileItemStream stream, int sizeThreshold, File cacheFile) throws IOException {
        this.cacheFile = cacheFile;
        File parent = cacheFile.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        this.dfos = new DeferredFileOutputStream(sizeThreshold, cacheFile);
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