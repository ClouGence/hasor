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
import java.util.concurrent.TimeUnit;
import org.more.future.BasicFuture;
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
    public static boolean extract(final String extToosHome, final String extractFile, final String toDir) throws Throwable {
        final BasicFuture<Integer> future = new BasicFuture<Integer>();
        class ExtractTask extends Thread {
            Process process = null;
            public void doWork() throws Throwable {
                String cmdFormat = String.format("%s\\7z.exe x \"%s\" -aoa -y \"-o%s\"", extToosHome, extractFile, toDir);
                process = Runtime.getRuntime().exec(cmdFormat);
                int extValue = process.waitFor();
                future.completed(extValue);
            }
            public void finish() {
                try {
                    stop();
                } finally {
                    if (process != null)
                        process.destroy();
                    if (future.isDone() == false)
                        future.completed(200);
                }
            }
            //
            public void run() {
                this.setName("Call 7z.exe -" + this.getId());
                try {
                    this.doWork();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        };
        ExtractTask extractTask = new ExtractTask();
        extractTask.start();
        Integer extValue = future.get(120, TimeUnit.SECONDS);//2分钟
        extractTask.finish();
        //
        if (extValue != null && extValue == 0) {
            new File(extractFile).delete();
        } else {
            FileUtils.deleteDir(new File(toDir));
            return false;
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
        return true;
    }
    public static void main(String[] args) throws Throwable {
        String extToosHome = "C:\\Program Files (x86)\\7-Zip";
        String extractFile = "D:\\work-space\\hasor-git\\hasor-src\\demo-Test\\hasor-work\\temp\\004774.rar";
        String toDir = "D:\\work-space\\hasor-git\\hasor-src\\demo-Test\\hasor-work\\temp\\004774";
        Zip7Object.extract(extToosHome, extractFile, toDir);
    }
}