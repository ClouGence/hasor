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
package net.hasor.rsf.utils;
import net.hasor.core.Settings;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
/**
 * General IO stream manipulation utilities.
 */
public abstract class IOUtils extends net.hasor.utils.IOUtils {
    public static BufferedReader toBufferedReader(Reader reader) {
        return reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
    }
    public static List<String> readLines(Reader input) throws IOException {
        BufferedReader reader = toBufferedReader(input);
        List<String> list = new ArrayList<String>();
        String line = reader.readLine();
        while (line != null) {
            list.add(line);
            line = reader.readLine();
        }
        return list;
    }
    public static List<String> readLines(InputStream input) throws IOException {
        InputStreamReader reader = new InputStreamReader(input, Settings.DefaultCharset);
        return readLines(reader);
    }
    public static String readToString(InputStream input) throws IOException {
        List<String> stringList = readLines(input);
        StringBuilder builder = new StringBuilder();
        for (String str : stringList) {
            builder = builder.append(str);
        }
        return builder.toString();
    }
}