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

/**
 * FileTypeMap is an abstract class that provides a data type interface for files.
 *
 * @version $Rev: 467742 $ $Date: 2011/05/07 04:32:39 $
 */
public abstract class FileTypeMap {
    // we use null here rather than a default because
    // the constructor for MimetypesFileTypeMap does a lot of I/O
    private static FileTypeMap defaultFileTypeMap = null;

    /**
     * Sets the default FileTypeMap for the system.
     * @param fileMap the new default FileTypeMap
     * @throws SecurityException if the caller does not have "SetFactory" RuntimePermission
     */
    public static void setDefaultFileTypeMap(FileTypeMap fileMap) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkSetFactory();
        }
        defaultFileTypeMap = fileMap;
    }

    /**
     * Returns the default FileTypeMap
     * @return the default FileTYpeMap; if null returns a MimetypesFileTypeMap
     */
    public synchronized static FileTypeMap getDefaultFileTypeMap() {
        if (defaultFileTypeMap == null) {
            defaultFileTypeMap = new MimetypesFileTypeMap();
        }
        return defaultFileTypeMap;
    }

    public FileTypeMap() {
    }

    public abstract String getContentType(File file);

    public abstract String getContentType(String filename);
}