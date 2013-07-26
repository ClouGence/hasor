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
package org.hasor.context.workspace;
import java.io.File;
import java.io.IOException;
import org.hasor.Hasor;
import org.hasor.context.Settings;
import org.hasor.context.WorkSpace;
import org.more.util.StringUtils;
/**
 * 
 * @version : 2013-5-23
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class StandardWorkSpace implements WorkSpace {
    private Settings settings = null;
    //
    public StandardWorkSpace(Settings settings) {
        Hasor.assertIsNotNull(settings, "Settings type parameter is empty!");
        this.settings = settings;
    }
    @Override
    public Settings getSettings() {
        return this.settings;
    }
    @Override
    public String getWorkDir() {
        String workDir = getSettings().getDirectoryPath("workspace.workDir");
        workDir = workDir.replace("/", File.separator);
        if (workDir.startsWith("." + File.separatorChar))
            return new File(workDir.substring(2)).getAbsolutePath();
        return workDir;
    };
    @Override
    public String getDataDir() {
        String workDir = getWorkDir();
        String dataDir = getSettings().getDirectoryPath("workspace.dataDir");
        boolean absolute = getSettings().getBoolean("workspace.dataDir.absolute");
        if (absolute == false)
            return str2path(new File(workDir, dataDir).getAbsolutePath());
        else
            return str2path(new File(dataDir).getAbsolutePath());
    };
    @Override
    public String getTempDir() {
        String workDir = getWorkDir();
        String tempDir = getSettings().getDirectoryPath("workspace.tempDir");
        boolean absolute = getSettings().getBoolean("workspace.tempDir.absolute");
        if (absolute == false)
            return str2path(new File(workDir, tempDir).getAbsolutePath());
        else
            return str2path(new File(tempDir).getAbsolutePath());
    };
    @Override
    public String getCacheDir() {
        String workDir = getWorkDir();
        String cacheDir = getSettings().getDirectoryPath("workspace.cacheDir");
        boolean absolute = getSettings().getBoolean("workspace.cacheDir.absolute");
        if (absolute == false)
            return str2path(new File(workDir, cacheDir).getAbsolutePath());
        else
            return str2path(new File(cacheDir).getAbsolutePath());
    };
    @Override
    public String getPluginDir() {
        String workDir = getWorkDir();
        String pluginDir = getSettings().getDirectoryPath("workspace.pluginDir");
        boolean absolute = getSettings().getBoolean("workspace.pluginDir.absolute");
        if (absolute == false)
            return str2path(new File(workDir, pluginDir).getAbsolutePath());
        else
            return str2path(new File(pluginDir).getAbsolutePath());
    }
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
    @Override
    public String getPluginDir(Class<?> hasorModule) {
        if (hasorModule == null)
            return null;
        String pluginName = hasorModule.getName();
        if (StringUtils.isBlank(pluginName) == true)
            return getCacheDir();
        else
            return str2path(new File(getCacheDir(), pluginName).getAbsolutePath());
    }
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
        Hasor.debug("create Temp File at %s.", tmpFile);
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