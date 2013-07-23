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
package org.hasor.context;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Set;
/**
 * 配置文件设置
 * @version : 2013-4-23
 * @author 赵永春 (zyc@byshell.org)
 */
public interface Settings {
    /**在框架扫描包的范围内查找具有特征类集合。（特征可以是继承的类、标记某个注解的类）*/
    public Set<Class<?>> getClassSet(Class<?> featureType, String[] loadPackages);
    /**在框架扫描包的范围内查找具有特征类集合。（特征可以是继承的类、标记某个注解的类）*/
    public Set<Class<?>> getClassSet(Class<?> featureType, String loadPackages);
    /**获取指在某个特定命名空间下的Settings接口对象。*/
    public String[] getNamespaceArray();
    /**获取指在某个特定命名空间下的Settings接口对象。*/
    public Settings getNamespace(String namespace);
    /**强制重新装载配置文件，该方法会引发配置文件重载事件。*/
    public void refresh() throws IOException;
    /**添加配置文件改变事件监听器。*/
    public void addSettingsListener(HasorSettingListener listener);
    /**删除配置文件改变事件监听器。*/
    public void removeSettingsListener(HasorSettingListener listener);
    /**获得所有配置文件改变事件监听器。*/
    public HasorSettingListener[] getSettingListeners();
    /**解析全局配置参数，并且返回其{@link Character}形式对象。*/
    public Character getChar(String name);
    /**解析全局配置参数，并且返回其{@link Character}形式对象。第二个参数为默认值。*/
    public Character getChar(String name, Character defaultValue);
    /**解析全局配置参数，并且返回其{@link String}形式对象。*/
    public String getString(String name);
    /**解析全局配置参数，并且返回其{@link String}形式对象。第二个参数为默认值。*/
    public String getString(String name, String defaultValue);
    /**解析全局配置参数，并且返回其{@link Boolean}形式对象。*/
    public Boolean getBoolean(String name);
    /**解析全局配置参数，并且返回其{@link Boolean}形式对象。第二个参数为默认值。*/
    public Boolean getBoolean(String name, Boolean defaultValue);
    /**解析全局配置参数，并且返回其{@link Short}形式对象。*/
    public Short getShort(String name);
    /**解析全局配置参数，并且返回其{@link Short}形式对象。第二个参数为默认值。*/
    public Short getShort(String name, Short defaultValue);
    /**解析全局配置参数，并且返回其{@link Integer}形式对象。*/
    public Integer getInteger(String name);
    /**解析全局配置参数，并且返回其{@link Integer}形式对象。第二个参数为默认值。*/
    public Integer getInteger(String name, Integer defaultValue);
    /**解析全局配置参数，并且返回其{@link Long}形式对象。*/
    public Long getLong(String name);
    /**解析全局配置参数，并且返回其{@link Long}形式对象。第二个参数为默认值。*/
    public Long getLong(String name, Long defaultValue);
    /**解析全局配置参数，并且返回其{@link Float}形式对象。*/
    public Float getFloat(String name);
    /**解析全局配置参数，并且返回其{@link Float}形式对象。第二个参数为默认值。*/
    public Float getFloat(String name, Float defaultValue);
    /**解析全局配置参数，并且返回其{@link Double}形式对象。*/
    public Double getDouble(String name);
    /**解析全局配置参数，并且返回其{@link Double}形式对象。第二个参数为默认值。*/
    public Double getDouble(String name, Double defaultValue);
    /**解析全局配置参数，并且返回其{@link Date}形式对象。*/
    public Date getDate(String name);
    /**解析全局配置参数，并且返回其{@link Date}形式对象。第二个参数为默认值。*/
    public Date getDate(String name, Date defaultValue);
    /**解析全局配置参数，并且返回其{@link Date}形式对象。第二个参数为默认值。*/
    public Date getDate(String name, long defaultValue);
    /**解析全局配置参数，并且返回其{@link Enum}形式对象。第二个参数为默认值。*/
    public <T extends Enum<?>> T getEnum(String name, Class<T> enmType);
    /**解析全局配置参数，并且返回其{@link Enum}形式对象。第二个参数为默认值。*/
    public <T extends Enum<?>> T getEnum(String name, Class<T> enmType, T defaultValue);
    /**解析全局配置参数，并且返回其{@link Date}形式对象（用于表示文件）。第二个参数为默认值。*/
    public String getFilePath(String name);
    /**解析全局配置参数，并且返回其{@link Date}形式对象（用于表示文件）。第二个参数为默认值。*/
    public String getFilePath(String name, String defaultValue);
    /**解析全局配置参数，并且返回其{@link File}形式对象（用于表示目录）。第二个参数为默认值。*/
    public String getDirectoryPath(String name);
    /**解析全局配置参数，并且返回其{@link File}形式对象（用于表示目录）。第二个参数为默认值。*/
    public String getDirectoryPath(String name, String defaultValue);
    /**解析全局配置参数，并且返回其{@link XmlProperty}形式对象。*/
    public XmlProperty getXmlProperty(String name);
    //
    /**解析全局配置参数，并且返回其{@link Character}形式对象。*/
    public Character[] getCharArray(String name);
    /**解析全局配置参数，并且返回其{@link Character}形式对象。第二个参数为默认值。*/
    public Character[] getCharArray(String name, Character defaultValue);
    /**解析全局配置参数，并且返回其{@link String}形式对象。*/
    public String[] getStringArray(String name);
    /**解析全局配置参数，并且返回其{@link String}形式对象。第二个参数为默认值。*/
    public String[] getStringArray(String name, String defaultValue);
    /**解析全局配置参数，并且返回其{@link Boolean}形式对象。*/
    public Boolean[] getBooleanArray(String name);
    /**解析全局配置参数，并且返回其{@link Boolean}形式对象。第二个参数为默认值。*/
    public Boolean[] getBooleanArray(String name, Boolean defaultValue);
    /**解析全局配置参数，并且返回其{@link Short}形式对象。*/
    public Short[] getShortArray(String name);
    /**解析全局配置参数，并且返回其{@link Short}形式对象。第二个参数为默认值。*/
    public Short[] getShortArray(String name, Short defaultValue);
    /**解析全局配置参数，并且返回其{@link Integer}形式对象。*/
    public Integer[] getIntegerArray(String name);
    /**解析全局配置参数，并且返回其{@link Integer}形式对象。第二个参数为默认值。*/
    public Integer[] getIntegerArray(String name, Integer defaultValue);
    /**解析全局配置参数，并且返回其{@link Long}形式对象。*/
    public Long[] getLongArray(String name);
    /**解析全局配置参数，并且返回其{@link Long}形式对象。第二个参数为默认值。*/
    public Long[] getLongArray(String name, Long defaultValue);
    /**解析全局配置参数，并且返回其{@link Float}形式对象。*/
    public Float[] getFloatArray(String name);
    /**解析全局配置参数，并且返回其{@link Float}形式对象。第二个参数为默认值。*/
    public Float[] getFloatArray(String name, Float defaultValue);
    /**解析全局配置参数，并且返回其{@link Double}形式对象。*/
    public Double[] getDoubleArray(String name);
    /**解析全局配置参数，并且返回其{@link Double}形式对象。第二个参数为默认值。*/
    public Double[] getDoubleArray(String name, Double defaultValue);
    /**解析全局配置参数，并且返回其{@link Date}形式对象。*/
    public Date[] getDateArray(String name);
    /**解析全局配置参数，并且返回其{@link Date}形式对象。第二个参数为默认值。*/
    public Date[] getDateArray(String name, Date defaultValue);
    /**解析全局配置参数，并且返回其{@link Date}形式对象。第二个参数为默认值。*/
    public Date[] getDateArray(String name, long defaultValue);
    /**解析全局配置参数，并且返回其{@link Enum}形式对象。第二个参数为默认值。*/
    public <T extends Enum<?>> T[] getEnumArray(String name, Class<T> enmType);
    /**解析全局配置参数，并且返回其{@link Enum}形式对象。第二个参数为默认值。*/
    public <T extends Enum<?>> T[] getEnumArray(String name, Class<T> enmType, T defaultValue);
    /**解析全局配置参数，并且返回其{@link Date}形式对象（用于表示文件）。第二个参数为默认值。*/
    public String[] getFilePathArray(String name);
    /**解析全局配置参数，并且返回其{@link Date}形式对象（用于表示文件）。第二个参数为默认值。*/
    public String[] getFilePathArray(String name, String defaultValue);
    /**解析全局配置参数，并且返回其{@link File}形式对象（用于表示目录）。第二个参数为默认值。*/
    public String[] getDirectoryPathArray(String name);
    /**解析全局配置参数，并且返回其{@link File}形式对象（用于表示目录）。第二个参数为默认值。*/
    public String[] getDirectoryPathArray(String name, String defaultValue);
    /**解析全局配置参数，并且返回其{@link XmlProperty}形式对象。*/
    public XmlProperty[] getXmlPropertyArray(String name);
}