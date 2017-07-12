/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.web;
import java.io.IOException;
/**
 * <p>A factory interface for creating {@link FileItem} instances. Factories
 * can provide their own custom configuration, over and above that provided
 * by the default file upload implementation.</p>
 *
 * @version $Id: FileItemFactory.java 1454690 2013-03-09 12:08:48Z simonetripodi $
 */
public interface FileItemFactory {
    /**
     * Create a new {@link FileItem} instance from the supplied parameters and
     * any local factory configuration.
     *
     * @param itemStream   The FileItemStream of the form field.
     * @return The newly created file item.
     */
    public FileItem createItem(FileItemStream itemStream) throws IOException;
}