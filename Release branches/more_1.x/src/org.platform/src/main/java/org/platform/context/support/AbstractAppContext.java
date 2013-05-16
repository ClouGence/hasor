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
import static org.platform.PlatformConfig.Platform_LoadPackages;
import static org.platform.PlatformConfig.Workspace_CacheDir;
import static org.platform.PlatformConfig.Workspace_CacheDir_Absolute;
import static org.platform.PlatformConfig.Workspace_DataDir;
import static org.platform.PlatformConfig.Workspace_DataDir_Absolute;
import static org.platform.PlatformConfig.Workspace_TempDir;
import static org.platform.PlatformConfig.Workspace_TempDir_Absolute;
import static org.platform.PlatformConfig.Workspace_WorkDir;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.more.util.ClassUtil;
import org.more.util.StringUtil;
import org.platform.binder.BeanInfo;
import org.platform.context.AppContext;
import com.google.inject.Binding;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
/**
 * {@link AppContext}接口的抽象实现类。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractAppContext implements AppContext {
    private long                  startTime   = System.currentTimeMillis(); //系统启动时间
    private Map<String, BeanInfo> beanInfoMap = null;
    //
    /**启动*/
    public abstract void start(Module... modules);
    /**销毁方法。*/
    public abstract void destroyed();
    @Override
    public long getAppStartTime() {
        return this.startTime;
    };
    @Override
    public Set<Class<?>> getClassSet(Class<?> featureType) {
        if (featureType == null)
            return null;
        String loadPackages = this.getSettings().getString(Platform_LoadPackages);
        String[] spanPackage = loadPackages.split(",");
        return ClassUtil.getClassSet(spanPackage, featureType);
    }
    @Override
    public <T> T getInstance(Class<T> beanType) {
        return this.getGuice().getInstance(beanType);
    };
    //    /**通过名称创建bean实例，使用guice。*/
    //    public abstract <T extends IService> T getService(String servicesName);
    //    /**通过类型创建该类实例，使用guice*/
    //    public abstract <T extends IService> T getService(Class<T> servicesType);
    @Override
    public <T> Class<T> getBeanType(String name) {
        if (this.beanInfoMap == null)
            this.collectBeanInfos();
        BeanInfo info = this.beanInfoMap.get(name);
        if (info != null)
            return (Class<T>) info.getBeanType();
        return null;
    }
    @Override
    public String[] getBeanNames() {
        if (this.beanInfoMap == null)
            this.collectBeanInfos();
        return this.beanInfoMap.values().toArray(new String[this.beanInfoMap.size()]);
    }
    @Override
    public BeanInfo getBeanInfo(String name) {
        if (this.beanInfoMap == null)
            this.collectBeanInfos();
        return this.beanInfoMap.get(name);
    }
    private void collectBeanInfos() {
        this.beanInfoMap = new HashMap<String, BeanInfo>();
        TypeLiteral<BeanInfo> INFO_DEFS = TypeLiteral.get(BeanInfo.class);
        for (Binding<BeanInfo> entry : this.getGuice().findBindingsByType(INFO_DEFS)) {
            BeanInfo beanInfo = entry.getProvider().get();
            this.beanInfoMap.put(beanInfo.getName(), beanInfo);
        }
    }
    @Override
    public <T> T getBean(String name) {
        BeanInfo beanInfo = this.getBeanInfo(name);
        if (beanInfo == null)
            return null;
        return (T) this.getGuice().getInstance(beanInfo.getKey());
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
    /*----------------------------------------------------------------------*/
    @Override
    public String getWorkDir() {
        String workDir = getSettings().getDirectoryPath(Workspace_WorkDir);
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
        if (StringUtil.isBlank(name) == true)
            return getDataDir();
        else
            return str2path(new File(getDataDir(), name).getAbsolutePath());
    };
    @Override
    public String getTempDir(String name) {
        if (StringUtil.isBlank(name) == true)
            return getTempDir();
        else
            return str2path(new File(getTempDir(), name).getAbsolutePath());
    };
    @Override
    public String getCacheDir(String name) {
        if (StringUtil.isBlank(name) == true)
            return getCacheDir();
        else
            return str2path(new File(getCacheDir(), name).getAbsolutePath());
    };
}