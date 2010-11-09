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
package org.more.util;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.jar.JarEntry;
/**
 * 发现事件
 * @version 2010-10-13
 * @author 赵永春 (zyc@byshell.org)
 */
public class ScanEvent {
    private String      name    = null;
    private boolean     isRead  = false; //是否可读。
    private boolean     isWrite = false; //是否可写
    //
    private InputStream stream  = null;
    private File        file    = null;
    //
    /**创建{@link ScanEvent}*/
    ScanEvent(String name, File file) {
        this.isRead = file.canRead();
        this.isWrite = file.canWrite();
        this.file = file;
        this.name = name;
    }
    /**创建{@link ScanEvent}*/
    ScanEvent(String name, JarEntry entry, InputStream stream) {
        this.isRead = !entry.isDirectory();
        this.isWrite = false;
        this.stream = stream;
        this.name = name;
    }
    //----------------------------------
    public String getName() {
        return name;
    }
    public boolean isRead() {
        return this.isRead;
    }
    public boolean isWrite() {
        return this.isWrite;
    }
    public InputStream getStream() throws FileNotFoundException {
        if (this.stream != null)
            return this.stream;
        if (this.file != null && this.isRead == true)
            return new FileInputStream(this.file);
        return null;
    }
}