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
package org.platform.context.support;
import java.io.File;
import java.io.IOException;
import org.more.util.StringUtils;
import org.platform.context.WorkSpace;
/**
 * 
 * @version : 2013-5-23
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractWorkSpace implements WorkSpace {
    /**程序工作空间基础目录（绝对地址）*/
    public static final String Workspace_WorkDir           = "workspace.workDir";
    /** 程序的文件数据目录（默认相对workDir地址，可以通过设置absolute属性为true表示一个绝对地址）*/
    public static final String Workspace_DataDir           = "workspace.dataDir";
    public static final String Workspace_DataDir_Absolute  = "workspace.dataDir.absolute";
    /** 程序运行期间所需的临时数据存放地址（默认相对baseDir地址，可以通过设置absolute属性为true表示一个绝对地址）*/
    public static final String Workspace_TempDir           = "workspace.tempDir";
    public static final String Workspace_TempDir_Absolute  = "workspace.tempDir.absolute";
    /** 程序运行时生成的缓存数据存放位置（默认相对baseDir地址，可以通过设置absolute属性为true表示一个绝对地址）*/
    public static final String Workspace_CacheDir          = "workspace.cacheDir";
    public static final String Workspace_CacheDir_Absolute = "workspace.cacheDir.absolute";
    //
    //
    @Override
    public String getWorkDir() {
        String workDir = getSettings().getDirectoryPath(Workspace_WorkDir);
        workDir = workDir.replace("/", File.separator);
        if (workDir.startsWith("." + File.separatorChar))
            return new File(workDir.substring(2)).getAbsolutePath();
        return workDir;
    };
    @Override
    public String getDataDir() {
        String workDir = getWorkDir();
        String dataDir = getSettings().getDirectoryPath(Workspace_DataDir);
        boolean absolute = getSettings().getBoolean(Workspace_DataDir_Absolute);
        if (absolute == false)
            return str2path(new File(workDir, dataDir).getAbsolutePath());
        else
            return str2path(new File(dataDir).getAbsolutePath());
    };
    @Override
    public String getTempDir() {
        String workDir = getWorkDir();
        String tempDir = getSettings().getDirectoryPath(Workspace_TempDir);
        boolean absolute = getSettings().getBoolean(Workspace_TempDir_Absolute);
        if (absolute == false)
            return str2path(new File(workDir, tempDir).getAbsolutePath());
        else
            return str2path(new File(tempDir).getAbsolutePath());
    };
    @Override
    public String getCacheDir() {
        String workDir = getWorkDir();
        String cacheDir = getSettings().getDirectoryPath(Workspace_CacheDir);
        boolean absolute = getSettings().getBoolean(Workspace_CacheDir_Absolute);
        if (absolute == false)
            return str2path(new File(workDir, cacheDir).getAbsolutePath());
        else
            return str2path(new File(cacheDir).getAbsolutePath());
    };
    @Override
    public String getDataDir(String name) {
        if (StringUtils.isBlank(name) == true)
            return getDataDir();
        else
            return str2path(new File(getDataDir(), name).getAbsolutePath());
    };
    @Override
    public String getTempDir(String name) {
        if (StringUtils.isBlank(name) == true)
            return getTempDir();
        else
            return str2path(new File(getTempDir(), name).getAbsolutePath());
    };
    @Override
    public String getCacheDir(String name) {
        if (StringUtils.isBlank(name) == true)
            return getCacheDir();
        else
            return str2path(new File(getCacheDir(), name).getAbsolutePath());
    };
    private File tempFileDirectory = null;
    @Override
    public synchronized File createTempFile() throws IOException {
        if (this.tempFileDirectory == null) {
            this.tempFileDirectory = new File(this.getTempDir("tempFile"));
            this.tempFileDirectory.deleteOnExit();
        }
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {}
        long markTime = System.currentTimeMillis();
        String atPath = genPath(markTime, 512);
        String fileName = atPath.substring(0, atPath.length() - 1) + "_" + String.valueOf(markTime) + ".tmp";
        File tmpFile = new File(tempFileDirectory, fileName);
        tmpFile.getParentFile().mkdirs();
        tmpFile.createNewFile();
        return tmpFile;
    };
    private String str2path(String oriPath) {
        int length = oriPath.length();
        if (oriPath.charAt(length - 1) == File.separatorChar)
            return oriPath;
        else
            return oriPath + File.separatorChar;
    };
    @Override
    public String genPath(long number, int size) {
        StringBuffer buffer = new StringBuffer();
        long b = size;
        long c = number;
        do {
            long m = number % b;
            buffer.append(m + File.separator);
            c = number / b;
            number = c;
        } while (c > 0);
        return buffer.reverse().toString();
    }
}