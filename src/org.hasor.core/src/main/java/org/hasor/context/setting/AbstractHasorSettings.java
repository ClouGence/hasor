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
package org.hasor.context.setting;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import org.hasor.context.Settings;
import org.hasor.context.XmlProperty;
import org.more.convert.ConverterUtils;
import org.more.util.ScanClassPath;
import org.more.util.StringUtils;
/**
 * Settings接口的抽象实现。
 * @version : 2013-4-2
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractHasorSettings implements Settings {
    /**获取配置项Map*/
    protected abstract Map<String, Object> getSettingMap();
    /**获取指在某个特定命名空间下的Settings接口对象。*/
    public abstract AbstractHasorSettings getNamespace(String namespace);
    /**将指定的配置文件作为主配置文件载入。*/
    public abstract void load(String mainConfig) throws IOException;
    /**在框架扫描包的范围内查找具有特征类集合。（特征可以是继承的类、标记某个注解的类）*/
    public Set<Class<?>> getClassSet(Class<?> featureType, String[] loadPackages) {
        if (featureType == null)
            return null;
        if (loadPackages == null)
            loadPackages = new String[] { "" };
        return ScanClassPath.getClassSet(loadPackages, featureType);
    }
    /**在框架扫描包的范围内查找具有特征类集合。（特征可以是继承的类、标记某个注解的类）*/
    public Set<Class<?>> getClassSet(Class<?> featureType, String loadPackages) {
        if (featureType == null)
            return null;
        loadPackages = (loadPackages == null) ? "" : loadPackages;
        String[] spanPackage = loadPackages.split(",");
        return this.getClassSet(featureType, spanPackage);
    }
    /**解析全局配置参数，并且返回toType参数指定的类型。*/
    public final <T> T getToType(String name, Class<T> toType, T defaultValue) {
        Object oriObject = this.getSettingMap().get(StringUtils.isBlank(name) ? "" : name.toLowerCase());
        if (oriObject == null)
            return defaultValue;
        //
        T var = null;
        if (oriObject instanceof String)
            //原始数据是字符串经过Eval过程
            var = (T) ConverterUtils.convert(toType, oriObject);
        else if (oriObject instanceof GlobalProperty)
            //原始数据是GlobalProperty直接get
            var = ((GlobalProperty) oriObject).getValue(toType, defaultValue);
        else
            //其他类型不予处理（数据就是要的值）
            var = (T) oriObject;
        return var;
    };
    public <T> T[] getToTypeArray(String name, Class<T> toType) {
        return this.getToTypeArray(name, toType, null);
    }
    public <T> T[] getToTypeArray(String name, Class<T> toType, T defaultValue) {
        ArrayList<T> targetObjects = new ArrayList<T>();
        for (String url : this.getNamespaceArray()) {
            T targetObject = this.getNamespace(url).getToType(name, toType, defaultValue);
            if (targetObject == null)
                continue;//空
            //
            targetObjects.add(targetObject);
        }
        return targetObjects.toArray((T[]) Array.newInstance(toType, targetObjects.size()));
    }
    /**解析全局配置参数，并且返回toType参数指定的类型。*/
    public final <T> T getToType(String name, Class<T> toType) {
        return this.getToType(name, toType, null);
    };
    /**解析全局配置参数，并且返回其{@link Object}形式对象。*/
    public Object getObject(String name) {
        return this.getToType(name, Object.class);
    };
    /**解析全局配置参数，并且返回其{@link Object}形式对象。第二个参数为默认值。*/
    public Object getObject(String name, Object defaultValue) {
        return this.getToType(name, Object.class, defaultValue);
    };
    /**解析全局配置参数，并且返回其{@link Character}形式对象。*/
    public Character getChar(String name) {
        return this.getToType(name, Character.class);
    };
    /**解析全局配置参数，并且返回其{@link Character}形式对象。第二个参数为默认值。*/
    public Character getChar(String name, Character defaultValue) {
        return this.getToType(name, Character.class, defaultValue);
    };
    @Override
    public Character[] getCharArray(String name) {
        return this.getToTypeArray(name, Character.class);
    }
    @Override
    public Character[] getCharArray(String name, Character defaultValue) {
        return this.getToTypeArray(name, Character.class, defaultValue);
    }
    /**解析全局配置参数，并且返回其{@link String}形式对象。*/
    public String getString(String name) {
        return this.getToType(name, String.class);
    };
    /**解析全局配置参数，并且返回其{@link String}形式对象。第二个参数为默认值。*/
    public String getString(String name, String defaultValue) {
        return this.getToType(name, String.class, defaultValue);
    };
    @Override
    public String[] getStringArray(String name) {
        return this.getToTypeArray(name, String.class);
    }
    @Override
    public String[] getStringArray(String name, String defaultValue) {
        return this.getToTypeArray(name, String.class, defaultValue);
    }
    /**解析全局配置参数，并且返回其{@link Boolean}形式对象。*/
    public Boolean getBoolean(String name) {
        return this.getToType(name, Boolean.class);
    };
    /**解析全局配置参数，并且返回其{@link Boolean}形式对象。第二个参数为默认值。*/
    public Boolean getBoolean(String name, Boolean defaultValue) {
        return this.getToType(name, Boolean.class, defaultValue);
    };
    @Override
    public Boolean[] getBooleanArray(String name) {
        return this.getToTypeArray(name, Boolean.class);
    }
    @Override
    public Boolean[] getBooleanArray(String name, Boolean defaultValue) {
        return this.getToTypeArray(name, Boolean.class, defaultValue);
    }
    /**解析全局配置参数，并且返回其{@link Short}形式对象。*/
    public Short getShort(String name) {
        return this.getToType(name, Short.class);
    };
    /**解析全局配置参数，并且返回其{@link Short}形式对象。第二个参数为默认值。*/
    public Short getShort(String name, Short defaultValue) {
        return this.getToType(name, Short.class, defaultValue);
    };
    @Override
    public Short[] getShortArray(String name) {
        return this.getToTypeArray(name, Short.class);
    }
    @Override
    public Short[] getShortArray(String name, Short defaultValue) {
        return this.getToTypeArray(name, Short.class, defaultValue);
    }
    /**解析全局配置参数，并且返回其{@link Integer}形式对象。*/
    public Integer getInteger(String name) {
        return this.getToType(name, Integer.class);
    };
    /**解析全局配置参数，并且返回其{@link Integer}形式对象。第二个参数为默认值。*/
    public Integer getInteger(String name, Integer defaultValue) {
        return this.getToType(name, Integer.class, defaultValue);
    };
    @Override
    public Integer[] getIntegerArray(String name) {
        return this.getToTypeArray(name, Integer.class);
    }
    @Override
    public Integer[] getIntegerArray(String name, Integer defaultValue) {
        return this.getToTypeArray(name, Integer.class, defaultValue);
    }
    /**解析全局配置参数，并且返回其{@link Long}形式对象。*/
    public Long getLong(String name) {
        return this.getToType(name, Long.class);
    };
    /**解析全局配置参数，并且返回其{@link Long}形式对象。第二个参数为默认值。*/
    public Long getLong(String name, Long defaultValue) {
        return this.getToType(name, Long.class, defaultValue);
    };
    @Override
    public Long[] getLongArray(String name) {
        return this.getToTypeArray(name, Long.class);
    }
    @Override
    public Long[] getLongArray(String name, Long defaultValue) {
        return this.getToTypeArray(name, Long.class, defaultValue);
    }
    /**解析全局配置参数，并且返回其{@link Float}形式对象。*/
    public Float getFloat(String name) {
        return this.getToType(name, Float.class);
    };
    /**解析全局配置参数，并且返回其{@link Float}形式对象。第二个参数为默认值。*/
    public Float getFloat(String name, Float defaultValue) {
        return this.getToType(name, Float.class, defaultValue);
    };
    @Override
    public Float[] getFloatArray(String name) {
        return this.getToTypeArray(name, Float.class);
    }
    @Override
    public Float[] getFloatArray(String name, Float defaultValue) {
        return this.getToTypeArray(name, Float.class, defaultValue);
    }
    /**解析全局配置参数，并且返回其{@link Double}形式对象。*/
    public Double getDouble(String name) {
        return this.getToType(name, Double.class);
    };
    /**解析全局配置参数，并且返回其{@link Double}形式对象。第二个参数为默认值。*/
    public Double getDouble(String name, Double defaultValue) {
        return this.getToType(name, Double.class, defaultValue);
    };
    @Override
    public Double[] getDoubleArray(String name) {
        return this.getToTypeArray(name, Double.class);
    }
    @Override
    public Double[] getDoubleArray(String name, Double defaultValue) {
        return this.getToTypeArray(name, Double.class, defaultValue);
    }
    /**解析全局配置参数，并且返回其{@link Date}形式对象。*/
    public Date getDate(String name) {
        return this.getToType(name, Date.class);
    };
    /**解析全局配置参数，并且返回其{@link Date}形式对象。第二个参数为默认值。*/
    public Date getDate(String name, Date defaultValue) {
        return this.getToType(name, Date.class, defaultValue);
    };
    /**解析全局配置参数，并且返回其{@link Date}形式对象。第二个参数为默认值。*/
    public Date getDate(String name, long defaultValue) {
        return this.getToType(name, Date.class, new Date(defaultValue));
    };
    @Override
    public Date[] getDateArray(String name) {
        return this.getToTypeArray(name, Date.class);
    }
    @Override
    public Date[] getDateArray(String name, Date defaultValue) {
        return this.getToTypeArray(name, Date.class, defaultValue);
    }
    @Override
    public Date[] getDateArray(String name, long defaultValue) {
        return this.getToTypeArray(name, Date.class, new Date(defaultValue));
    }
    /**解析全局配置参数，并且返回其{@link Enum}形式对象。第二个参数为默认值。*/
    public <T extends Enum<?>> T getEnum(String name, Class<T> enmType) {
        return this.getToType(name, enmType, null);
    };
    /**解析全局配置参数，并且返回其{@link Enum}形式对象。第二个参数为默认值。*/
    public <T extends Enum<?>> T getEnum(String name, Class<T> enmType, T defaultValue) {
        return this.getToType(name, enmType, defaultValue);
    };
    @Override
    public <T extends Enum<?>> T[] getEnumArray(String name, Class<T> enmType) {
        return this.getToTypeArray(name, enmType, null);
    }
    @Override
    public <T extends Enum<?>> T[] getEnumArray(String name, Class<T> enmType, T defaultValue) {
        return this.getToTypeArray(name, enmType, defaultValue);
    }
    /**解析全局配置参数，并且返回其{@link Date}形式对象（用于表示文件）。第二个参数为默认值。*/
    public String getFilePath(String name) {
        return this.getFilePath(name, null);
    };
    /**解析全局配置参数，并且返回其{@link Date}形式对象（用于表示文件）。第二个参数为默认值。*/
    public String getFilePath(String name, String defaultValue) {
        String filePath = this.getToType(name, String.class);
        if (filePath == null || filePath.length() == 0)
            return defaultValue;//空
        //
        int length = filePath.length();
        if (filePath.charAt(length - 1) == File.separatorChar)
            return filePath.substring(0, length - 1);
        else
            return filePath;
    };
    @Override
    public String[] getFilePathArray(String name) {
        return this.getFilePathArray(name, null);
    }
    @Override
    public String[] getFilePathArray(String name, String defaultValue) {
        ArrayList<String> filePaths = new ArrayList<String>();
        for (String url : this.getNamespaceArray()) {
            String filePath = this.getNamespace(url).getFilePath(name, defaultValue);
            if (filePath == null || filePath.length() == 0)
                continue;//空
            //
            int length = filePath.length();
            if (filePath.charAt(length - 1) == File.separatorChar)
                filePaths.add(filePath.substring(0, length - 1));
            else
                filePaths.add(filePath);
        }
        return filePaths.toArray(new String[filePaths.size()]);
    }
    /**解析全局配置参数，并且返回其{@link File}形式对象（用于表示目录）。第二个参数为默认值。*/
    public String getDirectoryPath(String name) {
        return this.getDirectoryPath(name, null);
    };
    /**解析全局配置参数，并且返回其{@link File}形式对象（用于表示目录）。第二个参数为默认值。*/
    public String getDirectoryPath(String name, String defaultValue) {
        String filePath = this.getToType(name, String.class);
        if (filePath == null || filePath.length() == 0)
            return defaultValue;//空
        //
        int length = filePath.length();
        if (filePath.charAt(length - 1) == File.separatorChar)
            return filePath;
        else
            return filePath + File.separatorChar;
    }
    @Override
    public String[] getDirectoryPathArray(String name) {
        return this.getDirectoryPathArray(name, null);
    }
    @Override
    public String[] getDirectoryPathArray(String name, String defaultValue) {
        ArrayList<String> directoryPaths = new ArrayList<String>();
        for (String url : this.getNamespaceArray()) {
            String filePath = this.getNamespace(url).getDirectoryPath(name, defaultValue);
            if (filePath == null || filePath.length() == 0)
                continue;//空
            //
            int length = filePath.length();
            if (filePath.charAt(length - 1) == File.separatorChar)
                directoryPaths.add(filePath.substring(0, length - 1));
            else
                directoryPaths.add(filePath);
        }
        return directoryPaths.toArray(new String[directoryPaths.size()]);
    }
    /**解析全局配置参数，并且返回其{@link XmlProperty}形式对象。*/
    public XmlProperty getXmlProperty(String name) {
        return this.getToType(name, XmlProperty.class, null);
    }
    @Override
    public XmlProperty[] getXmlPropertyArray(String name) {
        return this.getToTypeArray(name, XmlProperty.class, null);
    }
}