/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package org.hasor.updown.upload;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
/**
 * 上传的条目。
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
public interface IFileItem {
    /**上传的文件名。*/
    public String getFileName();
    /**获取上传的表单名。*/
    public String getFieldName();
    /**上传的数据是否为一个表单项目。*/
    public boolean isFormField();
    /**获取上传数据的输入流。*/
    public InputStream getInputStream() throws IOException;
    /**获取上传数据的输出流。*/
    public OutputStream getOutputStream() throws IOException;
    /**获取上传数据的ContentType。*/
    public String getContentType();
    /**上传数据是否存在于内存。*/
    public boolean isInMemory();
    /**上传的项目大小。*/
    public long getSize();
    /**以字节方式返回上传的所有数据。*/
    public byte[] get();
    /**返回String形式的上传表单数据，参数表示使用的字符编码。*/
    public String getString(String encoding) throws UnsupportedEncodingException;
    /**返回String形式的上传表单数据。*/
    public String getString();
    /**将上传数据写入到一个文件对象上，第二个参数表示当遇到文件存在时是否允许重写。*/
    public boolean write(File file, boolean overwrite) throws IOException;
    /**删除上传文件或数据。*/
    public void delete();
}