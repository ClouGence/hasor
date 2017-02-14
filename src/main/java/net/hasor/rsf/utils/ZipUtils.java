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
package net.hasor.rsf.utils;
import net.hasor.core.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
/**
 * 业务线程
 * @version : 2014年11月11日
 * @author 赵永春(zyc@hasor.net)
 */
public class ZipUtils {
    protected final static Logger logger = LoggerFactory.getLogger(ZipUtils.class);
    public static void writeEntry(ZipOutputStream zipStream, String scriptBody, String entryName, String comment) throws IOException {
        ZipEntry entry = new ZipEntry(entryName);
        entry.setComment(comment);
        zipStream.putNextEntry(entry);
        {
            OutputStreamWriter writer = new OutputStreamWriter(zipStream, Settings.DefaultCharset);
            BufferedWriter bfwriter = new BufferedWriter(writer);
            if (StringUtils.isBlank(scriptBody)) {
                bfwriter.write("");
            } else {
                bfwriter.write(scriptBody);
            }
            bfwriter.flush();
            writer.flush();
        }
        zipStream.closeEntry();
    }
}