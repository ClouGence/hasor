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
import net.hasor.web.FileItem;
import net.hasor.web.FileItemFactory;
import net.hasor.web.FileItemStream;
import net.hasor.web.upload.FileItemBase;

import java.io.*;
import java.util.UUID;
/**
 *
 * @version : 2015年2月11日
 * @author 赵永春(zyc@hasor.net)
 */
public class DiskFileItemFactory implements FileItemFactory {
    private File cacheDirectory;
    //
    public DiskFileItemFactory() {
    }
    public DiskFileItemFactory(String cacheDirectory) {
        this.cacheDirectory = new File(cacheDirectory);
    }
    //
    //
    public File getCacheDirectory() {
        return cacheDirectory;
    }
    public void setCacheDirectory(File cacheDirectory) {
        this.cacheDirectory = cacheDirectory;
    }
    @Override
    public FileItem createItem(FileItemStream itemStream) throws IOException {
        String fid = UUID.randomUUID().toString() + ".tmp";
        //
        if (itemStream.isFormField()) {
            return new MemoryFileItem(itemStream);
        } else {
            return createDiskFileItem(itemStream, fid);
        }
    }
    protected FileItem createDiskFileItem(FileItemStream itemStream, String fid) throws IOException {
        return new DiskFileItem(itemStream, new File(cacheDirectory, fid));
    }
    //
    //
    public static class MemoryFileItem extends FileItemBase {
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
}