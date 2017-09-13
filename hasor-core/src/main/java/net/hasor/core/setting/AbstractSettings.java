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
import net.hasor.utils.StringUtils;
import net.hasor.utils.convert.ConverterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * Settings接口的抽象实现。
 *
 * @version : 2013-4-2
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractSettings implements Settings {
    protected            Logger         logger              = LoggerFactory.getLogger(getClass());
    private static final SettingValue[] EMPTY_SETTING_VALUE = new SettingValue[0];
    private DecSpaceMap<String, SettingValue> dataMap;
    public AbstractSettings() {
        this.dataMap = new DecSpaceMap<String, SettingValue>();
    }
    //
    //
    protected DecSpaceMap<String, SettingValue> allSettingValue() {
        return dataMap;
    }
    /**使用UpdateValue接口,遍历所有属性值,将它们重新计算并设置新的参数值。<p>
     * 注意:该过程不可逆,一旦重新设置了属性值,那么原有从配置文件中读取的属性值将会被替换。
     * 一个典型的应用场景是配置文件模版化。*/
    public void resetValues(UpdateValue updateValue) {
        if (updateValue == null) {
            return;
        }
        Set<SettingValue> valueSet = this.allSettingValue().valueSet();
        if (valueSet != null) {
            for (SettingValue sv : valueSet) {
                updateValue.update(sv, this);
            }
        }
    }
    @Override
    public void refresh() throws IOException {
    }
    //
    //
    /** 获取可用的命名空间。 */
    public String[] getSettingArray() {
        Set<String> nsSet = this.allSettingValue().spaceSet();
        return nsSet.toArray(new String[nsSet.size()]);
    }
    /** 获取指在某个特定命名空间下的Settings接口对象。 */
    public final AbstractSettings getSettings(final String namespace) {
        final DecSpaceMap<String, SettingValue> localData = this.allSettingValue().space(namespace);
        if (localData == null) {
            return null;
        }
        return new AbstractSettings() {
            public DecSpaceMap<String, SettingValue> allSettingValue() {
                return localData;
            }
        };
    }
    /** 将整个配置项的多个值全部删除。 */
    public void removeSetting(String key, String namespace) {
        String lowerKey = StringUtils.isBlank(key) ? "" : key.toLowerCase();
        this.allSettingValue().remove(namespace, lowerKey);// 所有命名空间的数据
    }
    /**
     *  设置参数，如果出现多个值，则会覆盖。(使用默认命名空间 : DefaultNameSpace)
     * @see #DefaultNameSpace
     */
    @Override
    public void setSetting(String key, Object value) {
        this.setSetting(key, value, DefaultNameSpace);
    }
    /** 设置参数，如果出现多个值，则会覆盖。 */
    public void setSetting(final String key, final Object value, final String namespace) {
        String lowerKey = StringUtils.isBlank(key) ? "" : key.toLowerCase();
        this.removeSetting(lowerKey, namespace);
        this.addSetting(lowerKey, value, namespace);
    }
    /** 添加参数，如果参数名称相同则追加一项。 */
    public void addSetting(final String key, final Object value, final String namespace) {
        String lowerKey = StringUtils.isBlank(key) ? "" : key.toLowerCase();
        DecSpaceMap<String, SettingValue> dataMap = this.allSettingValue();
        SettingValue val = dataMap.get(namespace, lowerKey);
        if (val == null) {
            val = new SettingValue(namespace);
            dataMap.put(namespace, lowerKey, val);
        }
        val.newValue(value);
    }
    //
    //
    /**清空已经装载的所有数据。*/
    protected void cleanData() {
        logger.info("cleanData -> clear all data.");
        this.allSettingValue().deleteAllSpace();
    }
    protected SettingValue[] findSettingValue(String name) {
        name = StringUtils.isBlank(name) ? "" : name.toLowerCase();
        List<SettingValue> svList = this.allSettingValue().get(name);
        if (svList == null || svList.isEmpty()) {
            return EMPTY_SETTING_VALUE;
        }
        //
        Collections.sort(svList, new Comparator<SettingValue>() {
            @Override
            public int compare(SettingValue o1, SettingValue o2) {
                int o1Index = DefaultNameSpace.equalsIgnoreCase(o1.getSpace()) ? 0 : 1;
                int o2Index = DefaultNameSpace.equalsIgnoreCase(o2.getSpace()) ? 0 : 1;
                return o1Index < o2Index ? -1 : o1Index == o2Index ? 0 : 1;
            }
        });
        return svList.toArray(new SettingValue[svList.size()]);
    }
    protected <T> T converTo(Object oriObject, final Class<T> toType, final T defaultValue) {
        if (oriObject == null) {
            return defaultValue;
        }
        T var = null;
        if (oriObject instanceof String) {
            // 原始数据是字符串经过Eval过程
            var = (T) ConverterUtils.convert(toType, oriObject);
        } else if (oriObject instanceof FieldProperty) {
            // 原始数据是GlobalProperty直接get
            var = ((FieldProperty) oriObject).getValue(toType, defaultValue);
        } else {
            // 其他类型不予处理（数据就是要的值）
            var = (T) oriObject;
        }
        return var == null ? defaultValue : var;
    }
    /** 解析全局配置参数，并且返回toType参数指定的类型。 */
    public final <T> T getToType(final String name, final Class<T> toType, final T defaultValue) {
        SettingValue[] settingvar = this.findSettingValue(name);
        if (settingvar == null || settingvar.length == 0) {
            return defaultValue;
        }
        return converTo(settingvar[0].getDefaultVar(), toType, defaultValue);
    }
    public <T> T[] getToTypeArray(final String name, final Class<T> toType, final T defaultValue) {
        SettingValue[] varArrays = this.findSettingValue(name);
        if (varArrays == null) {
            return (T[]) Array.newInstance(toType, 0);
        }
        List<T> targetObjects = new ArrayList<T>();
        for (SettingValue var : varArrays) {
            for (Object item : var.getVarList()) {
                T finalItem = converTo(item, toType, defaultValue);
                targetObjects.add(finalItem);
            }
        }
        return targetObjects.toArray((T[]) Array.newInstance(toType, targetObjects.size()));
    }
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
        return this.getToType(name, Date.class);
    }
    /** 解析全局配置参数，并且返回其{@link Date}形式对象。第二个参数为默认值。 */
    public Date getDate(final String name, final Date defaultValue) {
        return this.getToType(name, Date.class, defaultValue);
    }
    /** 解析全局配置参数，并且返回其{@link Date}形式对象。第二个参数为默认值。 */
    public Date getDate(final String name, final long defaultValue) {
        return this.getToType(name, Date.class, new Date(defaultValue));
    }
    /** 解析全局配置参数，并且返回其{@link Date}形式对象。 */
    public Date getDate(final String name, final String format) {
        return this.getDate(name, format, null);
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
    /** 解析全局配置参数，并且返回其{@link Date}形式对象。第二个参数为默认值。 */
    public Date getDate(final String name, final String format, final long defaultValue) {
        return this.getDate(name, format, new Date(defaultValue));
    }
    public Date[] getDateArray(final String name) {
        return this.getDateArray(name, null, null);
    }
    public Date[] getDateArray(final String name, final Date defaultValue) {
        return this.getDateArray(name, null, defaultValue);
    }
    public Date[] getDateArray(final String name, final long defaultValue) {
        return this.getDateArray(name, null, defaultValue);
    }
    public Date[] getDateArray(final String name, final String format) {
        return this.getDateArray(name, format, null);
    }
    public Date[] getDateArray(final String name, final String format, final Date defaultValue) {
        String[] oriDataArray = this.getToTypeArray(name, String.class);
        if (oriDataArray == null || oriDataArray.length == 0) {
            return null;
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
    public Date[] getDateArray(final String name, final String format, final long defaultValue) {
        String[] oriDataArray = this.getToTypeArray(name, String.class);
        if (oriDataArray == null || oriDataArray.length == 0) {
            return null;
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
                parsedDate[i] = new Date(defaultValue);
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
        return this.getFilePath(name, null);
    }
    /** 解析全局配置参数，并且返回其{@link Date}形式对象（用于表示文件）。第二个参数为默认值。 */
    public String getFilePath(final String name, final String defaultValue) {
        String filePath = this.getToType(name, String.class);
        if (filePath == null || filePath.length() == 0) {
            return defaultValue;// 空
        } //
        int length = filePath.length();
        if (filePath.charAt(length - 1) == File.separatorChar) {
            return filePath.substring(0, length - 1);
        } else {
            return filePath;
        }
    }
    public String[] getFilePathArray(final String name) {
        return this.getFilePathArray(name, null);
    }
    public String[] getFilePathArray(final String name, final String defaultValue) {
        ArrayList<String> filePaths = new ArrayList<String>();
        for (String url : this.getSettingArray()) {
            Settings targetSettings = this.getSettings(url);
            if (targetSettings == null) {
                continue;
            }
            String filePath = targetSettings.getFilePath(name, defaultValue);
            if (filePath == null || filePath.length() == 0) {
                continue;// 空
            } //
            int length = filePath.length();
            if (filePath.charAt(length - 1) == File.separatorChar) {
                filePaths.add(filePath.substring(0, length - 1));
            } else {
                filePaths.add(filePath);
            }
        }
        return filePaths.toArray(new String[filePaths.size()]);
    }
    /** 解析全局配置参数，并且返回其{@link File}形式对象（用于表示目录）。第二个参数为默认值。 */
    public String getDirectoryPath(final String name) {
        return this.getDirectoryPath(name, null);
    }
    /** 解析全局配置参数，并且返回其{@link File}形式对象（用于表示目录）。第二个参数为默认值。 */
    public String getDirectoryPath(final String name, final String defaultValue) {
        String filePath = this.getToType(name, String.class);
        if (filePath == null || filePath.length() == 0) {
            return defaultValue;// 空
        } //
        int length = filePath.length();
        if (filePath.charAt(length - 1) == File.separatorChar) {
            return filePath;
        } else {
            return filePath + File.separatorChar;
        }
    }
    public String[] getDirectoryPathArray(final String name) {
        return this.getDirectoryPathArray(name, null);
    }
    public String[] getDirectoryPathArray(final String name, final String defaultValue) {
        ArrayList<String> directoryPaths = new ArrayList<String>();
        for (String url : this.getSettingArray()) {
            Settings targetSettings = this.getSettings(url);
            if (targetSettings == null) {
                continue;
            }
            String filePath = targetSettings.getDirectoryPath(name, defaultValue);
            if (filePath == null || filePath.length() == 0) {
                continue;// 空
            } //
            int length = filePath.length();
            if (filePath.charAt(length - 1) == File.separatorChar) {
                directoryPaths.add(filePath.substring(0, length - 1));
            } else {
                directoryPaths.add(filePath);
            }
        }
        return directoryPaths.toArray(new String[directoryPaths.size()]);
    }
    /** 解析全局配置参数，并且返回其{@link XmlNode}形式对象。 */
    public XmlNode getXmlNode(final String name) {
        return this.getToType(name, XmlNode.class, null);
    }
    public XmlNode[] getXmlNodeArray(final String name) {
        return this.getToTypeArray(name, XmlNode.class, null);
    }
    public String toString() {
        return "Settings[" + this.getClass().getSimpleName() + "]";
    }
}