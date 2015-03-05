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
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
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
    public static int extract(final String extToosHome, final String extractFile, final String toDir) throws Throwable {
        String cmdLine = String.format("%s\\7z.exe x \"%s\" -aoa -y \"-o%s\"", extToosHome, extractFile, toDir);
        CommandLine commandline = CommandLine.parse(cmdLine);
        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
        DefaultExecutor executor = new DefaultExecutor();
        int extValue = -1;
        try {
            executor.execute(commandline, resultHandler);
            resultHandler.waitFor(300 * 1000);
            extValue = resultHandler.getExitValue();
            if (extValue == 0) {
                new File(extractFile).delete();
            }
        } catch (Exception e) {
            //
        } finally {
            if (extValue != 0) {
                FileUtils.deleteDir(new File(toDir));
                return extValue;
            }
        }
        //
        Iterator<File> itFile = FileUtils.iterateFiles(new File(toDir), FileFilterUtils.fileFileFilter(), FileFilterUtils.directoryFileFilter());
        while (itFile.hasNext()) {
            File it = itFile.next();
            if (it.isDirectory())
                continue;
            for (String com : compression) {
                if (StringUtils.endsWithIgnoreCase(it.getName(), com)) {
                    String itPath = it.getAbsolutePath();
                    String subToDir = itPath.substring(0, itPath.length() - com.length());
                    extract(extToosHome, itPath, subToDir);
                }
            }
        }
        return 0;
    }
    public static void main(String[] args) throws Throwable {
        String extToosHome = "C:\\Program Files (x86)\\7-Zip";
        String extractFile = "D:\\work-space\\hasor-git\\hasor-src\\demo-Test\\hasor-work\\temp\\1234567890.rar";
        String toDir = "D:\\work-space\\hasor-git\\hasor-src\\demo-Test\\hasor-work\\temp\\1234567890";
        int res = Zip7Object.extract(extToosHome, extractFile, toDir);
        System.out.println(res);
    }
}