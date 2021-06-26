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
import net.hasor.utils.StringUtils;
import net.hasor.utils.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Settings接口的抽象实现。
 *
 * @version : 2013-4-2
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractSettings implements Settings {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    /** 解析全局配置参数，并且返回toType参数指定的类型。 */
    public abstract <T> T getToType(final String name, final Class<T> toType, final T defaultValue);

    public abstract <T> T[] getToTypeArray(final String name, final Class<T> toType, final T defaultValue);

    public <T> T[] getToTypeArray(final String name, final Class<T> toType) {
        return this.getToTypeArray(name, toType, null);
    }

    /** 解析全局配置参数，并且返回toType参数指定的类型。 */
    public final <T> T getToType(final String name, final Class<T> toType) {
        return this.getToType(name, toType, null);
    }

    /** 解析全局配置参数，并且返回其{@link Object}形式对象。 */
    public Object getObject(final String name) {
        return this.getToType(name, Object.class);
    }

    /** 解析全局配置参数，并且返回其{@link Object}形式对象。第二个参数为默认值。 */
    public Object getObject(final String name, final Object defaultValue) {
        return this.getToType(name, Object.class, defaultValue);
    }

    /** 解析全局配置参数，并且返回其{@link Character}形式对象。 */
    public Character getChar(final String name) {
        return this.getToType(name, Character.class);
    }

    /** 解析全局配置参数，并且返回其{@link Character}形式对象。第二个参数为默认值。 */
    public Character getChar(final String name, final Character defaultValue) {
        return this.getToType(name, Character.class, defaultValue);
    }

    public Character[] getCharArray(final String name) {
        return this.getToTypeArray(name, Character.class);
    }

    public Character[] getCharArray(final String name, final Character defaultValue) {
        return this.getToTypeArray(name, Character.class, defaultValue);
    }

    /** 解析全局配置参数，并且返回其{@link String}形式对象。 */
    public String getString(final String name) {
        return this.getToType(name, String.class);
    }

    /** 解析全局配置参数，并且返回其{@link String}形式对象。第二个参数为默认值。 */
    public String getString(final String name, final String defaultValue) {
        return this.getToType(name, String.class, defaultValue);
    }

    public String[] getStringArray(final String name) {
        return this.getToTypeArray(name, String.class);
    }

    public String[] getStringArray(final String name, final String defaultValue) {
        return this.getToTypeArray(name, String.class, defaultValue);
    }

    /** 解析全局配置参数，并且返回其{@link Boolean}形式对象。 */
    public Boolean getBoolean(final String name) {
        return this.getToType(name, Boolean.class);
    }

    /** 解析全局配置参数，并且返回其{@link Boolean}形式对象。第二个参数为默认值。 */
    public Boolean getBoolean(final String name, final Boolean defaultValue) {
        return this.getToType(name, Boolean.class, defaultValue);
    }

    public Boolean[] getBooleanArray(final String name) {
        return this.getToTypeArray(name, Boolean.class);
    }

    public Boolean[] getBooleanArray(final String name, final Boolean defaultValue) {
        return this.getToTypeArray(name, Boolean.class, defaultValue);
    }

    /** 解析全局配置参数，并且返回其{@link Short}形式对象。 */
    public Short getShort(final String name) {
        return this.getToType(name, Short.class);
    }

    /** 解析全局配置参数，并且返回其{@link Short}形式对象。第二个参数为默认值。 */
    public Short getShort(final String name, final Short defaultValue) {
        return this.getToType(name, Short.class, defaultValue);
    }

    public Short[] getShortArray(final String name) {
        return this.getToTypeArray(name, Short.class);
    }

    public Short[] getShortArray(final String name, final Short defaultValue) {
        return this.getToTypeArray(name, Short.class, defaultValue);
    }

    /** 解析全局配置参数，并且返回其{@link Integer}形式对象。 */
    public Integer getInteger(final String name) {
        return this.getToType(name, Integer.class);
    }

    /** 解析全局配置参数，并且返回其{@link Integer}形式对象。第二个参数为默认值。 */
    public Integer getInteger(final String name, final Integer defaultValue) {
        return this.getToType(name, Integer.class, defaultValue);
    }

    public Integer[] getIntegerArray(final String name) {
        return this.getToTypeArray(name, Integer.class);
    }

    public Integer[] getIntegerArray(final String name, final Integer defaultValue) {
        return this.getToTypeArray(name, Integer.class, defaultValue);
    }

    /** 解析全局配置参数，并且返回其{@link Long}形式对象。 */
    public Long getLong(final String name) {
        return this.getToType(name, Long.class);
    }

    /** 解析全局配置参数，并且返回其{@link Long}形式对象。第二个参数为默认值。 */
    public Long getLong(final String name, final Long defaultValue) {
        return this.getToType(name, Long.class, defaultValue);
    }

    public Long[] getLongArray(final String name) {
        return this.getToTypeArray(name, Long.class);
    }

    public Long[] getLongArray(final String name, final Long defaultValue) {
        return this.getToTypeArray(name, Long.class, defaultValue);
    }

    /** 解析全局配置参数，并且返回其{@link Float}形式对象。 */
    public Float getFloat(final String name) {
        return this.getToType(name, Float.class);
    }

    /** 解析全局配置参数，并且返回其{@link Float}形式对象。第二个参数为默认值。 */
    public Float getFloat(final String name, final Float defaultValue) {
        return this.getToType(name, Float.class, defaultValue);
    }

    public Float[] getFloatArray(final String name) {
        return this.getToTypeArray(name, Float.class);
    }

    public Float[] getFloatArray(final String name, final Float defaultValue) {
        return this.getToTypeArray(name, Float.class, defaultValue);
    }

    /** 解析全局配置参数，并且返回其{@link Double}形式对象。 */
    public Double getDouble(final String name) {
        return this.getToType(name, Double.class);
    }

    /** 解析全局配置参数，并且返回其{@link Double}形式对象。第二个参数为默认值。 */
    public Double getDouble(final String name, final Double defaultValue) {
        return this.getToType(name, Double.class, defaultValue);
    }

    public Double[] getDoubleArray(final String name) {
        return this.getToTypeArray(name, Double.class);
    }

    public Double[] getDoubleArray(final String name, final Double defaultValue) {
        return this.getToTypeArray(name, Double.class, defaultValue);
    }

    /** 解析全局配置参数，并且返回其{@link Date}形式对象。 */
    public Date getDate(final String name) {
        return this.getDate(name, getString(name + ".format"), null);
    }

    /** 解析全局配置参数，并且返回其{@link Date}形式对象。第二个参数为默认值。 */
    public Date getDate(final String name, final Date defaultValue) {
        return this.getDate(name, getString(name + ".format"), defaultValue);
    }

    /** 解析全局配置参数，并且返回其{@link Date}形式对象。第二个参数为默认值。 */
    public Date getDate(final String name, final long defaultValue) {
        return this.getDate(name, getString(name + ".format"), new Date(defaultValue));
    }

    /** 解析全局配置参数，并且返回其{@link Date}形式对象。 */
    public Date getDate(final String name, final String format) {
        return this.getDate(name, format, null);
    }

    /** 解析全局配置参数，并且返回其{@link Date}形式对象。第二个参数为默认值。 */
    public Date getDate(final String name, final String format, final long defaultValue) {
        return this.getDate(name, format, new Date(defaultValue));
    }

    /** 解析全局配置参数，并且返回其{@link Date}形式对象。第二个参数为默认值。 */
    public Date getDate(final String name, final String format, final Date defaultValue) {
        String oriData = this.getToType(name, String.class);
        if (oriData == null || oriData.length() == 0) {
            return defaultValue;
        }
        //
        DateFormat dateFormat = new SimpleDateFormat(format);
        ParsePosition pos = new ParsePosition(0);
        dateFormat.setLenient(false);
        Date parsedDate = dateFormat.parse(oriData, pos); // ignore the result (use the Calendar)
        if (pos.getErrorIndex() >= 0 || pos.getIndex() != oriData.length() || parsedDate == null) {
            return defaultValue;
        } else {
            return parsedDate;
        }
    }

    public Date[] getDateArray(final String name) {
        return this.getDateArray(name, getString(name + ".format"), null);
    }

    public Date[] getDateArray(final String name, final Date defaultValue) {
        return this.getDateArray(name, getString(name + ".format"), defaultValue);
    }

    public Date[] getDateArray(final String name, final long defaultValue) {
        return this.getDateArray(name, getString(name + ".format"), new Date(defaultValue));
    }

    public Date[] getDateArray(final String name, final String format) {
        return this.getDateArray(name, format, null);
    }

    public Date[] getDateArray(final String name, final String format, final long defaultValue) {
        return this.getDateArray(name, format, new Date(defaultValue));
    }

    public Date[] getDateArray(final String name, final String format, final Date defaultValue) {
        String[] oriDataArray = this.getToTypeArray(name, String.class);
        if (oriDataArray == null || oriDataArray.length == 0) {
            return new Date[0];
        }
        //
        DateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setLenient(false);
        Date[] parsedDate = new Date[oriDataArray.length];
        for (int i = 0; i < oriDataArray.length; i++) {
            String oriData = oriDataArray[i];
            ParsePosition pos = new ParsePosition(0);
            parsedDate[i] = dateFormat.parse(oriData, pos); // ignore the result (use the Calendar)
            if (pos.getErrorIndex() >= 0 || pos.getIndex() != oriData.length() || parsedDate[i] == null) {
                parsedDate[i] = defaultValue == null ? null : new Date(defaultValue.getTime());
            }
        }
        return parsedDate;
    }

    /** 解析全局配置参数，并且返回其{@link Enum}形式对象。第二个参数为默认值。 */
    public <T extends Enum<?>> T getEnum(final String name, final Class<T> enmType) {
        return this.getToType(name, enmType, null);
    }

    /** 解析全局配置参数，并且返回其{@link Enum}形式对象。第二个参数为默认值。 */
    public <T extends Enum<?>> T getEnum(final String name, final Class<T> enmType, final T defaultValue) {
        return this.getToType(name, enmType, defaultValue);
    }

    public <T extends Enum<?>> T[] getEnumArray(final String name, final Class<T> enmType) {
        return this.getToTypeArray(name, enmType, null);
    }

    public <T extends Enum<?>> T[] getEnumArray(final String name, final Class<T> enmType, final T defaultValue) {
        return this.getToTypeArray(name, enmType, defaultValue);
    }

    /** 解析全局配置参数，并且返回其{@link Date}形式对象（用于表示文件）。第二个参数为默认值。 */
    public String getFilePath(final String name) {
        return getFilePath(name, null, true);
    }

    /** 解析全局配置参数，并且返回其{@link Date}形式对象（用于表示文件）。第二个参数为默认值。 */
    public String getFilePath(final String name, final String defaultValue) {
        return getFilePath(name, defaultValue, true);
    }

    /** 解析全局配置参数，并且返回其{@link File}形式对象（用于表示目录）。第二个参数为默认值。 */
    public String getDirectoryPath(final String name) {
        return getFilePath(name, null, false);
    }

    /** 解析全局配置参数，并且返回其{@link File}形式对象（用于表示目录）。第二个参数为默认值。 */
    public String getDirectoryPath(final String name, final String defaultValue) {
        return getFilePath(name, defaultValue, false);
    }

    public String[] getFilePathArray(final String name) {
        return this.getFilePathArray(name, null, true);
    }

    public String[] getFilePathArray(final String name, final String defaultValue) {
        return this.getFilePathArray(name, defaultValue, true);
    }

    public String[] getDirectoryPathArray(final String name) {
        return this.getFilePathArray(name, null, false);
    }

    public String[] getDirectoryPathArray(final String name, final String defaultValue) {
        return this.getFilePathArray(name, defaultValue, false);
    }

    private String getFilePath(final String name, final String defaultValue, boolean includeName) {
        String filePath = this.getToType(name, String.class);
        if (StringUtils.isBlank(filePath)) {
            return defaultValue;// 空
        }
        if (includeName) {
            String fileName = FilenameUtils.getName(filePath);
            if (StringUtils.isNotBlank(fileName)) {
                return FilenameUtils.getFullPath(filePath) + FilenameUtils.getName(filePath);
            } else {
                return StringUtils.isBlank(defaultValue) ? null : defaultValue;
            }
        } else {
            return FilenameUtils.getFullPath(filePath);
        }
    }

    private String[] getFilePathArray(final String name, final String defaultValue, boolean includeName) {
        ArrayList<String> filePaths = new ArrayList<>();
        for (String url : this.getSettingArray()) {
            Settings targetSettings = this.getSettings(url);
            String filePath = targetSettings.getString(name);
            if (StringUtils.isBlank(filePath)) {
                continue;// 空
            }
            //
            if (includeName) {
                String fileName = FilenameUtils.getName(filePath);
                if (StringUtils.isNotBlank(fileName)) {
                    filePaths.add(FilenameUtils.getFullPath(filePath) + FilenameUtils.getName(filePath));
                } else {
                    continue;
                }
            } else {
                filePaths.add(FilenameUtils.getFullPath(filePath));
            }
        }
        return filePaths.toArray(new String[0]);
    }

    /** 解析全局配置参数，并且返回其{@link SettingNode}形式对象。 */
    public SettingNode getNode(final String name) {
        return this.getToType(name, SettingNode.class, null);
    }

    public SettingNode[] getNodeArray(final String name) {
        return this.getToTypeArray(name, SettingNode.class, null);
    }

    public String toString() {
        return "Settings[" + this.getClass().getSimpleName() + "]";
    }
}
