/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
import java.io.File;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import net.hasor.core.Settings;
import net.hasor.core.XmlNode;
import org.more.convert.ConverterUtils;
import org.more.util.ScanClassPath;
import org.more.util.StringUtils;
/**
 * Settings接口的抽象实现。
 * @version : 2013-4-2
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractSettings implements Settings {
    protected abstract Map<String, Object> getSettingsMap();
    /**获取指在某个特定命名空间下的Settings接口对象。*/
    @Override
    public abstract AbstractSettings getSettings(String namespace);
    //
    /**在框架扫描包的范围内查找具有特征类集合。（特征可以是继承的类、标记某个注解的类）*/
    @Override
    public Set<Class<?>> findClass(final Class<?> featureType, String[] loadPackages) {
        if (featureType == null) {
            return null;
        }
        if (loadPackages == null) {
            loadPackages = new String[] { "" };
        }
        return ScanClassPath.getClassSet(loadPackages, featureType);
    }
    /**在框架扫描包的范围内查找具有特征类集合。（特征可以是继承的类、标记某个注解的类）*/
    @Override
    public Set<Class<?>> findClass(final Class<?> featureType, String loadPackages) {
        if (featureType == null) {
            return null;
        }
        loadPackages = loadPackages == null ? "" : loadPackages;
        String[] spanPackage = loadPackages.split(",");
        return this.findClass(featureType, spanPackage);
    }
    /**解析全局配置参数，并且返回toType参数指定的类型。*/
    public final <T> T getToType(final String name, final Class<T> toType, final T defaultValue) {
        Object oriObject = this.getSettingsMap().get(StringUtils.isBlank(name) ? "" : name.toLowerCase());
        if (oriObject == null) {
            return defaultValue;
        }
        //
        T var = null;
        if (oriObject instanceof String) {
            //原始数据是字符串经过Eval过程
            var = (T) ConverterUtils.convert(toType, oriObject);
        } else if (oriObject instanceof FieldProperty) {
            //原始数据是GlobalProperty直接get
            var = ((FieldProperty) oriObject).getValue(toType, defaultValue);
        } else {
            //其他类型不予处理（数据就是要的值）
            var = (T) oriObject;
        }
        return var == null ? defaultValue : var;
    };
    public <T> T[] getToTypeArray(final String name, final Class<T> toType) {
        return this.getToTypeArray(name, toType, null);
    }
    public <T> T[] getToTypeArray(final String name, final Class<T> toType, final T defaultValue) {
        ArrayList<T> targetObjects = new ArrayList<T>();
        for (String url : this.getSettingArray()) {
            T targetObject = this.getSettings(url).getToType(name, toType, defaultValue);
            if (targetObject == null) {
                continue;//空
            } //
            targetObjects.add(targetObject);
        }
        if (targetObjects.isEmpty() && defaultValue != null) {
            targetObjects.add(defaultValue);
        }
        return targetObjects.toArray((T[]) Array.newInstance(toType, targetObjects.size()));
    }
    /**解析全局配置参数，并且返回toType参数指定的类型。*/
    public final <T> T getToType(final String name, final Class<T> toType) {
        return this.getToType(name, toType, null);
    };
    /**解析全局配置参数，并且返回其{@link Object}形式对象。*/
    public Object getObject(final String name) {
        return this.getToType(name, Object.class);
    };
    /**解析全局配置参数，并且返回其{@link Object}形式对象。第二个参数为默认值。*/
    public Object getObject(final String name, final Object defaultValue) {
        return this.getToType(name, Object.class, defaultValue);
    };
    /**解析全局配置参数，并且返回其{@link Character}形式对象。*/
    @Override
    public Character getChar(final String name) {
        return this.getToType(name, Character.class);
    };
    /**解析全局配置参数，并且返回其{@link Character}形式对象。第二个参数为默认值。*/
    @Override
    public Character getChar(final String name, final Character defaultValue) {
        return this.getToType(name, Character.class, defaultValue);
    };
    @Override
    public Character[] getCharArray(final String name) {
        return this.getToTypeArray(name, Character.class);
    }
    @Override
    public Character[] getCharArray(final String name, final Character defaultValue) {
        return this.getToTypeArray(name, Character.class, defaultValue);
    }
    /**解析全局配置参数，并且返回其{@link String}形式对象。*/
    @Override
    public String getString(final String name) {
        return this.getToType(name, String.class);
    };
    /**解析全局配置参数，并且返回其{@link String}形式对象。第二个参数为默认值。*/
    @Override
    public String getString(final String name, final String defaultValue) {
        return this.getToType(name, String.class, defaultValue);
    };
    @Override
    public String[] getStringArray(final String name) {
        return this.getToTypeArray(name, String.class);
    }
    @Override
    public String[] getStringArray(final String name, final String defaultValue) {
        return this.getToTypeArray(name, String.class, defaultValue);
    }
    /**解析全局配置参数，并且返回其{@link Boolean}形式对象。*/
    @Override
    public Boolean getBoolean(final String name) {
        return this.getToType(name, Boolean.class);
    };
    /**解析全局配置参数，并且返回其{@link Boolean}形式对象。第二个参数为默认值。*/
    @Override
    public Boolean getBoolean(final String name, final Boolean defaultValue) {
        return this.getToType(name, Boolean.class, defaultValue);
    };
    @Override
    public Boolean[] getBooleanArray(final String name) {
        return this.getToTypeArray(name, Boolean.class);
    }
    @Override
    public Boolean[] getBooleanArray(final String name, final Boolean defaultValue) {
        return this.getToTypeArray(name, Boolean.class, defaultValue);
    }
    /**解析全局配置参数，并且返回其{@link Short}形式对象。*/
    @Override
    public Short getShort(final String name) {
        return this.getToType(name, Short.class);
    };
    /**解析全局配置参数，并且返回其{@link Short}形式对象。第二个参数为默认值。*/
    @Override
    public Short getShort(final String name, final Short defaultValue) {
        return this.getToType(name, Short.class, defaultValue);
    };
    @Override
    public Short[] getShortArray(final String name) {
        return this.getToTypeArray(name, Short.class);
    }
    @Override
    public Short[] getShortArray(final String name, final Short defaultValue) {
        return this.getToTypeArray(name, Short.class, defaultValue);
    }
    /**解析全局配置参数，并且返回其{@link Integer}形式对象。*/
    @Override
    public Integer getInteger(final String name) {
        return this.getToType(name, Integer.class);
    };
    /**解析全局配置参数，并且返回其{@link Integer}形式对象。第二个参数为默认值。*/
    @Override
    public Integer getInteger(final String name, final Integer defaultValue) {
        return this.getToType(name, Integer.class, defaultValue);
    };
    @Override
    public Integer[] getIntegerArray(final String name) {
        return this.getToTypeArray(name, Integer.class);
    }
    @Override
    public Integer[] getIntegerArray(final String name, final Integer defaultValue) {
        return this.getToTypeArray(name, Integer.class, defaultValue);
    }
    /**解析全局配置参数，并且返回其{@link Long}形式对象。*/
    @Override
    public Long getLong(final String name) {
        return this.getToType(name, Long.class);
    };
    /**解析全局配置参数，并且返回其{@link Long}形式对象。第二个参数为默认值。*/
    @Override
    public Long getLong(final String name, final Long defaultValue) {
        return this.getToType(name, Long.class, defaultValue);
    };
    @Override
    public Long[] getLongArray(final String name) {
        return this.getToTypeArray(name, Long.class);
    }
    @Override
    public Long[] getLongArray(final String name, final Long defaultValue) {
        return this.getToTypeArray(name, Long.class, defaultValue);
    }
    /**解析全局配置参数，并且返回其{@link Float}形式对象。*/
    @Override
    public Float getFloat(final String name) {
        return this.getToType(name, Float.class);
    };
    /**解析全局配置参数，并且返回其{@link Float}形式对象。第二个参数为默认值。*/
    @Override
    public Float getFloat(final String name, final Float defaultValue) {
        return this.getToType(name, Float.class, defaultValue);
    };
    @Override
    public Float[] getFloatArray(final String name) {
        return this.getToTypeArray(name, Float.class);
    }
    @Override
    public Float[] getFloatArray(final String name, final Float defaultValue) {
        return this.getToTypeArray(name, Float.class, defaultValue);
    }
    /**解析全局配置参数，并且返回其{@link Double}形式对象。*/
    @Override
    public Double getDouble(final String name) {
        return this.getToType(name, Double.class);
    };
    /**解析全局配置参数，并且返回其{@link Double}形式对象。第二个参数为默认值。*/
    @Override
    public Double getDouble(final String name, final Double defaultValue) {
        return this.getToType(name, Double.class, defaultValue);
    };
    @Override
    public Double[] getDoubleArray(final String name) {
        return this.getToTypeArray(name, Double.class);
    }
    @Override
    public Double[] getDoubleArray(final String name, final Double defaultValue) {
        return this.getToTypeArray(name, Double.class, defaultValue);
    }
    /**解析全局配置参数，并且返回其{@link Date}形式对象。*/
    @Override
    public Date getDate(final String name) {
        return this.getToType(name, Date.class);
    };
    /**解析全局配置参数，并且返回其{@link Date}形式对象。第二个参数为默认值。*/
    @Override
    public Date getDate(final String name, final Date defaultValue) {
        return this.getToType(name, Date.class, defaultValue);
    };
    /**解析全局配置参数，并且返回其{@link Date}形式对象。第二个参数为默认值。*/
    @Override
    public Date getDate(final String name, final long defaultValue) {
        return this.getToType(name, Date.class, new Date(defaultValue));
    };
    /**解析全局配置参数，并且返回其{@link Date}形式对象。*/
    @Override
    public Date getDate(final String name, final String format) {
        return this.getDate(name, format, null);
    };
    /**解析全局配置参数，并且返回其{@link Date}形式对象。第二个参数为默认值。*/
    @Override
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
    };
    /**解析全局配置参数，并且返回其{@link Date}形式对象。第二个参数为默认值。*/
    @Override
    public Date getDate(final String name, final String format, final long defaultValue) {
        return this.getDate(name, format, new Date(defaultValue));
    };
    @Override
    public Date[] getDateArray(final String name) {
        return this.getDateArray(name, null, (Date) null);
    }
    @Override
    public Date[] getDateArray(final String name, final Date defaultValue) {
        if (defaultValue == null) {
            return this.getDateArray(name, null, (Date) null);
        } else {
            return this.getDateArray(name, null, defaultValue);
        }
    }
    @Override
    public Date[] getDateArray(final String name, final long defaultValue) {
        return this.getDateArray(name, null, defaultValue);
    }
    @Override
    public Date[] getDateArray(final String name, final String format) {
        return this.getDateArray(name, format, null);
    }
    @Override
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
    @Override
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
    /**解析全局配置参数，并且返回其{@link Enum}形式对象。第二个参数为默认值。*/
    @Override
    public <T extends Enum<?>> T getEnum(final String name, final Class<T> enmType) {
        return this.getToType(name, enmType, null);
    };
    /**解析全局配置参数，并且返回其{@link Enum}形式对象。第二个参数为默认值。*/
    @Override
    public <T extends Enum<?>> T getEnum(final String name, final Class<T> enmType, final T defaultValue) {
        return this.getToType(name, enmType, defaultValue);
    };
    @Override
    public <T extends Enum<?>> T[] getEnumArray(final String name, final Class<T> enmType) {
        return this.getToTypeArray(name, enmType, null);
    }
    @Override
    public <T extends Enum<?>> T[] getEnumArray(final String name, final Class<T> enmType, final T defaultValue) {
        return this.getToTypeArray(name, enmType, defaultValue);
    }
    /**解析全局配置参数，并且返回其{@link Date}形式对象（用于表示文件）。第二个参数为默认值。*/
    @Override
    public String getFilePath(final String name) {
        return this.getFilePath(name, null);
    };
    /**解析全局配置参数，并且返回其{@link Date}形式对象（用于表示文件）。第二个参数为默认值。*/
    @Override
    public String getFilePath(final String name, final String defaultValue) {
        String filePath = this.getToType(name, String.class);
        if (filePath == null || filePath.length() == 0) {
            return defaultValue;//空
        } //
        int length = filePath.length();
        if (filePath.charAt(length - 1) == File.separatorChar) {
            return filePath.substring(0, length - 1);
        } else {
            return filePath;
        }
    };
    @Override
    public String[] getFilePathArray(final String name) {
        return this.getFilePathArray(name, null);
    }
    @Override
    public String[] getFilePathArray(final String name, final String defaultValue) {
        ArrayList<String> filePaths = new ArrayList<String>();
        for (String url : this.getSettingArray()) {
            String filePath = this.getSettings(url).getFilePath(name, defaultValue);
            if (filePath == null || filePath.length() == 0) {
                continue;//空
            }//
            int length = filePath.length();
            if (filePath.charAt(length - 1) == File.separatorChar) {
                filePaths.add(filePath.substring(0, length - 1));
            } else {
                filePaths.add(filePath);
            }
        }
        return filePaths.toArray(new String[filePaths.size()]);
    }
    /**解析全局配置参数，并且返回其{@link File}形式对象（用于表示目录）。第二个参数为默认值。*/
    @Override
    public String getDirectoryPath(final String name) {
        return this.getDirectoryPath(name, null);
    };
    /**解析全局配置参数，并且返回其{@link File}形式对象（用于表示目录）。第二个参数为默认值。*/
    @Override
    public String getDirectoryPath(final String name, final String defaultValue) {
        String filePath = this.getToType(name, String.class);
        if (filePath == null || filePath.length() == 0) {
            return defaultValue;//空
        }//
        int length = filePath.length();
        if (filePath.charAt(length - 1) == File.separatorChar) {
            return filePath;
        } else {
            return filePath + File.separatorChar;
        }
    }
    @Override
    public String[] getDirectoryPathArray(final String name) {
        return this.getDirectoryPathArray(name, null);
    }
    @Override
    public String[] getDirectoryPathArray(final String name, final String defaultValue) {
        ArrayList<String> directoryPaths = new ArrayList<String>();
        for (String url : this.getSettingArray()) {
            String filePath = this.getSettings(url).getDirectoryPath(name, defaultValue);
            if (filePath == null || filePath.length() == 0) {
                continue;//空
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
    /**解析全局配置参数，并且返回其{@link XmlNode}形式对象。*/
    @Override
    public XmlNode getXmlNode(final String name) {
        return this.getToType(name, XmlNode.class, null);
    }
    @Override
    public XmlNode[] getXmlNodeArray(final String name) {
        return this.getToTypeArray(name, XmlNode.class, null);
    }
}