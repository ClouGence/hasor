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
import java.util.Date;
import org.more.global.assembler.xml.XmlProperty;
/**
 * 配置文件设置
 * @version : 2013-4-23
 * @author 赵永春 (zyc@byshell.org)
 */
public interface Settings {
    /**添加配置文件改变事件监听器。*/
    public void addSettingsListener(SettingListener listener);
    /**删除配置文件改变事件监听器。*/
    public void removeSettingsListener(SettingListener listener);
    /**获得所有配置文件改变事件监听器。*/
    public SettingListener[] getSettingListeners();
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
}