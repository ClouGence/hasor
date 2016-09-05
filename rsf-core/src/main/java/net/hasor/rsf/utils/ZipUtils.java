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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.more.util.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 业务线程
 * @version : 2014年11月11日
 * @author 赵永春(zyc@hasor.net)
 */
public class ZipUtils {
    protected final static Logger logger      = LoggerFactory.getLogger(ZipUtils.class);
    public static final    String CharsetName = "UTF-8";
    public static void writeEntry(ZipOutputStream zipStream, String scriptBody, String entryName, String comment) throws IOException, UnsupportedEncodingException {
        ZipEntry entry = new ZipEntry(entryName);
        entry.setComment(comment);
        zipStream.putNextEntry(entry);
        {
            OutputStreamWriter writer = new OutputStreamWriter(zipStream, CharsetName);
            BufferedWriter bfwriter = new BufferedWriter(writer);
            bfwriter.write(scriptBody);
            bfwriter.flush();
            writer.flush();
            zipStream.finish();
        }
        zipStream.closeEntry();
    }
    public static String readToString(ZipFile zipFile, String entryName) throws IOException {
        List<String> readToList = readToList(zipFile, entryName);
        if (readToList == null || readToList.isEmpty()) {
            return null;
        }
        StringBuilder strBuilder = new StringBuilder();
        for (String readItem : readToList) {
            strBuilder.append(readItem).append("\n");
        }
        return strBuilder.toString();
    }
    public static List<String> readToList(ZipFile zipFile, String entryName) throws IOException {
        ZipEntry entry = zipFile.getEntry(entryName);
        if (entry != null) {
            InputStream inStream = zipFile.getInputStream(entry);
            InputStreamReader reader = new InputStreamReader(inStream, CharsetName);
            BufferedReader bfreader = new BufferedReader(reader);
            return IOUtils.readLines(bfreader);
        } else {
            return null;
        }
    }
}