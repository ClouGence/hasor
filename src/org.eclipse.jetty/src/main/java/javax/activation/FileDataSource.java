/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package javax.activation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @version $Rev: 467742 $ $Date: 2011/05/07 04:32:39 $
 */
public class FileDataSource implements DataSource {
    private final File file;
    private FileTypeMap fileTypeMap;

    /**
     * Creates a FileDataSource from a File object
     */
    public FileDataSource(File file) {
        this.file = file;
    }

    /**
     * Creates a FileDataSource from the specified path name
     */
    public FileDataSource(String name) {
        this(new File(name));
    }

    /**
     * Return the InputStream obtained from the data source
     */
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(file);
    }

    /**
     * Return the OutputStream obtained from the data source
     */
    public OutputStream getOutputStream() throws IOException {
        return new FileOutputStream(file);
    }

    /**
     * Returns the content type of the data source
     */
    public String getContentType() {
        if (fileTypeMap == null) {
            return FileTypeMap.getDefaultFileTypeMap().getContentType(file);
        } else {
            return fileTypeMap.getContentType(file);
        }
    }

    /**
     * Returns the name of the data source object
     */
    public String getName() {
        return file.getName();
    }

    /**
     * Returns the data source file
     */
    public File getFile() {
        return file;
    }

    /**
     * Sets the FileTypeMap associated with the data source
     */
    public void setFileTypeMap(FileTypeMap map) {
        fileTypeMap = map;
    }
}