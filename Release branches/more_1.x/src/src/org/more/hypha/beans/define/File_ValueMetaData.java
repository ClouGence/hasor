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
package org.more.hypha.beans.define;
/**
 * 表示一个或一组文件，文件夹类型数据，对应的PropertyMetaTypeEnum类型为{@link PropertyMetaTypeEnum#File}。
 * @version 2010-9-17
 * @author 赵永春 (zyc@byshell.org)
 */
public class File_ValueMetaData extends AbstractValueMetaData {
    private String  fileObject = null; //表示文件或目录的字符串。
    private boolean isDir      = false; //表示是否是一个目录
    /**该方法将会返回{@link PropertyMetaTypeEnum#File}。*/
    public String getMetaDataType() {
        return PropertyMetaTypeEnum.File;
    }
    /**获取表示文件或目录的字符串*/
    public String getFileObject() {
        return this.fileObject;
    }
    /**设置表示文件或目录的字符串*/
    public void setFileObject(String fileObject) {
        this.fileObject = fileObject;
    }
    /**获取一个值该值表示fileObject是否是一个目录。*/
    public boolean isDir() {
        return this.isDir;
    }
    /**设置一个值该值表示fileObject是否是一个目录。*/
    public void setDir(boolean isDir) {
        this.isDir = isDir;
    }
}