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
package net.hasor.core.setting;
import net.hasor.core.Settings;
import net.hasor.core.XmlNode;

import java.io.IOException;
import java.util.Date;
/**
 * Settings接口的抽象实现。
 *
 * @version : 2013-4-2
 * @author 赵永春 (zyc@hasor.net)
 */
public class SettingsWrap implements Settings {
    private final Settings settings;
    public SettingsWrap(Settings settings) {
        this.settings = settings;
    }
    //
    protected Settings getSettings() {
        return settings;
    }
    //
    public String[] getSettingArray() {
        return this.settings.getSettingArray();
    }
    public Settings getSettings(String namespace) {
        return this.settings.getSettings(namespace);
    }
    public void refresh() throws IOException {
        this.settings.refresh();
    }
    @Override
    public void setSetting(String key, Object value) {
        this.settings.setSetting(key, value);
    }
    public void setSetting(String key, Object value, String namespace) {
        this.settings.setSetting(key, value, namespace);
    }
    public void addSetting(String key, Object value, String namespace) {
        this.settings.addSetting(key, value, namespace);
    }
    public void removeSetting(String key, String namespace) {
        this.settings.removeSetting(key, namespace);
    }
    public Character getChar(String name) {
        return this.settings.getChar(name);
    }
    public Character getChar(String name, Character defaultValue) {
        return this.settings.getChar(name, defaultValue);
    }
    public String getString(String name) {
        return this.settings.getString(name);
    }
    public String getString(String name, String defaultValue) {
        return this.settings.getString(name, defaultValue);
    }
    public Boolean getBoolean(String name) {
        return this.settings.getBoolean(name);
    }
    public Boolean getBoolean(String name, Boolean defaultValue) {
        return this.settings.getBoolean(name, defaultValue);
    }
    public Short getShort(String name) {
        return this.settings.getShort(name);
    }
    public Short getShort(String name, Short defaultValue) {
        return this.settings.getShort(name, defaultValue);
    }
    public Integer getInteger(String name) {
        return this.settings.getInteger(name);
    }
    public Integer getInteger(String name, Integer defaultValue) {
        return this.settings.getInteger(name, defaultValue);
    }
    public Long getLong(String name) {
        return this.settings.getLong(name);
    }
    public Long getLong(String name, Long defaultValue) {
        return this.settings.getLong(name, defaultValue);
    }
    public Float getFloat(String name) {
        return this.settings.getFloat(name);
    }
    public Float getFloat(String name, Float defaultValue) {
        return this.settings.getFloat(name, defaultValue);
    }
    public Double getDouble(String name) {
        return this.settings.getDouble(name);
    }
    public Double getDouble(String name, Double defaultValue) {
        return this.settings.getDouble(name, defaultValue);
    }
    public Date getDate(String name) {
        return this.settings.getDate(name);
    }
    public Date getDate(String name, Date defaultValue) {
        return this.settings.getDate(name, defaultValue);
    }
    public Date getDate(String name, long defaultValue) {
        return this.settings.getDate(name, defaultValue);
    }
    public Date getDate(String name, String format) {
        return this.settings.getDate(name, format);
    }
    public Date getDate(String name, String format, Date defaultValue) {
        return this.settings.getDate(name, format, defaultValue);
    }
    public Date getDate(String name, String format, long defaultValue) {
        return this.settings.getDate(name, format, defaultValue);
    }
    public <T extends Enum<?>> T getEnum(String name, Class<T> enmType) {
        return this.settings.getEnum(name, enmType);
    }
    public <T extends Enum<?>> T getEnum(String name, Class<T> enmType, T defaultValue) {
        return this.settings.getEnum(name, enmType, defaultValue);
    }
    public String getFilePath(String name) {
        return this.settings.getFilePath(name);
    }
    public String getFilePath(String name, String defaultValue) {
        return this.settings.getFilePath(name, defaultValue);
    }
    public String getDirectoryPath(String name) {
        return this.settings.getDirectoryPath(name);
    }
    public String getDirectoryPath(String name, String defaultValue) {
        return this.settings.getDirectoryPath(name, defaultValue);
    }
    public XmlNode getXmlNode(String name) {
        return this.settings.getXmlNode(name);
    }
    public Character[] getCharArray(String name) {
        return this.settings.getCharArray(name);
    }
    public Character[] getCharArray(String name, Character defaultValue) {
        return this.settings.getCharArray(name, defaultValue);
    }
    public String[] getStringArray(String name) {
        return this.settings.getStringArray(name);
    }
    public String[] getStringArray(String name, String defaultValue) {
        return this.settings.getStringArray(name, defaultValue);
    }
    public Boolean[] getBooleanArray(String name) {
        return this.settings.getBooleanArray(name);
    }
    public Boolean[] getBooleanArray(String name, Boolean defaultValue) {
        return this.settings.getBooleanArray(name, defaultValue);
    }
    public Short[] getShortArray(String name) {
        return this.settings.getShortArray(name);
    }
    public Short[] getShortArray(String name, Short defaultValue) {
        return this.settings.getShortArray(name, defaultValue);
    }
    public Integer[] getIntegerArray(String name) {
        return this.settings.getIntegerArray(name);
    }
    public Integer[] getIntegerArray(String name, Integer defaultValue) {
        return this.settings.getIntegerArray(name, defaultValue);
    }
    public Long[] getLongArray(String name) {
        return this.settings.getLongArray(name);
    }
    public Long[] getLongArray(String name, Long defaultValue) {
        return this.settings.getLongArray(name, defaultValue);
    }
    public Float[] getFloatArray(String name) {
        return this.settings.getFloatArray(name);
    }
    public Float[] getFloatArray(String name, Float defaultValue) {
        return this.settings.getFloatArray(name, defaultValue);
    }
    public Double[] getDoubleArray(String name) {
        return this.settings.getDoubleArray(name);
    }
    public Double[] getDoubleArray(String name, Double defaultValue) {
        return this.settings.getDoubleArray(name, defaultValue);
    }
    public Date[] getDateArray(String name) {
        return this.settings.getDateArray(name);
    }
    public Date[] getDateArray(String name, Date defaultValue) {
        return this.settings.getDateArray(name, defaultValue);
    }
    public Date[] getDateArray(String name, long defaultValue) {
        return this.settings.getDateArray(name, defaultValue);
    }
    public Date[] getDateArray(String name, String format) {
        return this.settings.getDateArray(name, format);
    }
    public Date[] getDateArray(String name, String format, Date defaultValue) {
        return this.settings.getDateArray(name, format, defaultValue);
    }
    public Date[] getDateArray(String name, String format, long defaultValue) {
        return this.settings.getDateArray(name, format, defaultValue);
    }
    public <T extends Enum<?>> T[] getEnumArray(String name, Class<T> enmType) {
        return this.settings.getEnumArray(name, enmType);
    }
    public <T extends Enum<?>> T[] getEnumArray(String name, Class<T> enmType, T defaultValue) {
        return this.settings.getEnumArray(name, enmType, defaultValue);
    }
    public String[] getFilePathArray(String name) {
        return this.settings.getFilePathArray(name);
    }
    public String[] getFilePathArray(String name, String defaultValue) {
        return this.settings.getFilePathArray(name, defaultValue);
    }
    public String[] getDirectoryPathArray(String name) {
        return this.settings.getDirectoryPathArray(name);
    }
    public String[] getDirectoryPathArray(String name, String defaultValue) {
        return this.settings.getDirectoryPathArray(name, defaultValue);
    }
    public XmlNode[] getXmlNodeArray(String name) {
        return this.settings.getXmlNodeArray(name);
    }
    @Override
    public String toString() {
        return "SettingsWarp -> " + this.settings.toString();
    }
}