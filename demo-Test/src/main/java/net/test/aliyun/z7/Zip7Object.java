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
package net.test.aliyun.z7;
import java.io.File;
import java.util.Iterator;
import org.more.util.StringUtils;
import org.more.util.io.FileFilterUtils;
import org.more.util.io.FileUtils;
/**
 * 射手 数据遍历
 * @version : 2014年8月1日
 * @author 赵永春(zyc@hasor.net)
 */
public class Zip7Object {
    private static String[] compression = new String[] { ".zip", ".7z", ".rar" };
    //
    public static void extract(String extToosHome, String extractFile, String toDir) throws Throwable {
        String cmdFormat = String.format("%s\\7z.exe e \"%s\" \"-o%s\"", extToosHome, extractFile, toDir);
        Process p = Runtime.getRuntime().exec(cmdFormat);
        int extValue = p.waitFor();
        if (extValue == 0) {
            new File(extractFile).delete();
            p.destroy();
        } else {
            FileUtils.deleteDir(new File(toDir));
            p.destroy();
            return;
        }
        //
        Iterator<File> itFile = FileUtils.iterateFiles(new File(toDir), FileFilterUtils.fileFileFilter(), FileFilterUtils.directoryFileFilter());
        while (itFile.hasNext()) {
            File it = itFile.next();
            if (it.isDirectory())
                continue;
            //
            for (String com : compression) {
                if (StringUtils.endsWithIgnoreCase(it.getName(), com)) {
                    String itPath = it.getAbsolutePath();
                    String subToDir = itPath.substring(0, itPath.length() - com.length());
                    extract(extToosHome, itPath, subToDir);
                }
            }
        }
    }
}