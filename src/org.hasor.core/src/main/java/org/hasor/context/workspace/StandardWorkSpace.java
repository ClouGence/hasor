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
        String workDir = getSettings().getDirectoryPath("hasor.workspace.workDir");
        workDir = workDir.replace("/", File.separator);
        if (workDir.startsWith("." + File.separatorChar))
            return new File(workDir.substring(2)).getAbsolutePath();
        return workDir;
    };
    @Override
    public String getTempDir() {
        String workDir = getWorkDir();
        String tempDir = getSettings().getDirectoryPath("hasor.workspace.tempDir");
        return str2path(new File(workDir, tempDir).getAbsolutePath());
    };
    @Override
    public String getPluginDir() {
        String workDir = getWorkDir();
        String pluginDir = getSettings().getDirectoryPath("hasor.workspace.pluginDir");
        return str2path(new File(workDir, pluginDir).getAbsolutePath());
    }
    @Override
    public String getLogDir() {
        String workDir = getWorkDir();
        String pluginDir = getSettings().getDirectoryPath("hasor.workspace.logDir");
        return str2path(new File(workDir, pluginDir).getAbsolutePath());
    }
    @Override
    public String getWorkDir(String subPath) {
        if (StringUtils.isBlank(subPath) == true)
            return getWorkDir();
        else
            return str2path(new File(getWorkDir(), subPath).getAbsolutePath());
    }
    @Override
    public String getTempDir(String subPath) {
        if (StringUtils.isBlank(subPath) == true)
            return getTempDir();
        else
            return str2path(new File(getTempDir(), subPath).getAbsolutePath());
    };
    @Override
    public String getPluginDir(Class<?> hasorModule) {
        if (hasorModule == null)
            return null;
        String pluginName = hasorModule.getName();
        if (StringUtils.isBlank(pluginName) == true)
            return getPluginDir();
        else
            return str2path(new File(getPluginDir(), pluginName).getAbsolutePath());
    }
    @Override
    public String getLogDir(String subPath) {
        if (StringUtils.isBlank(subPath) == true)
            return getLogDir();
        else
            return str2path(new File(getLogDir(), subPath).getAbsolutePath());
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