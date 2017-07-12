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
package net.hasor.core;
import java.io.IOException;
import java.util.Date;
/**
 * <p>
 * 配置文件设置
 * </p>
 *
 * @version : 2013-4-23
 * @author 赵永春 (zyc@hasor.net)
 */
public interface Settings {
    public static final String DefaultNameSpace = "http://project.hasor.net/hasor/schema/main";
    public static final String DefaultCharset   = "UTF-8";

    /** @return 已解析的命名空间列表。 */
    public String[] getSettingArray();

    /** 获取指在某个特定命名空间下的Settings接口对象。 */
    public Settings getSettings(String namespace);

    /** 强制重新装载配置文件。 */
    public void refresh() throws IOException;
    //

    /** 设置参数，如果出现多个值，则会覆盖。 */
    public void setSetting(String key, Object value);

    /** 设置参数，如果出现多个值，则会覆盖。 */
    public void setSetting(String key, Object value, String namespace);

    /** 将整个配置项的多个值全部删除。 */
    public void removeSetting(String key, String namespace);

    /** 添加参数，如果参数名称相同则追加一项。 */
    public void addSetting(String key, Object var, String currentXmlns);
    //

    /** 解析全局配置参数，并且返回其{@link Character}形式对象。 */
    public Character getChar(String name);

    /** 解析全局配置参数，并且返回其{@link Character}形式对象。第二个参数为默认值。 */
    public Character getChar(String name, Character defaultValue);

    /** 解析全局配置参数，并且返回其{@link String}形式对象。 */
    public String getString(String name);

    /** 解析全局配置参数，并且返回其{@link String}形式对象。第二个参数为默认值。 */
    public String getString(String name, String defaultValue);

    /** 解析全局配置参数，并且返回其{@link Boolean}形式对象。 */
    public Boolean getBoolean(String name);

    /** 解析全局配置参数，并且返回其{@link Boolean}形式对象。第二个参数为默认值。 */
    public Boolean getBoolean(String name, Boolean defaultValue);

    /** 解析全局配置参数，并且返回其{@link Short}形式对象。 */
    public Short getShort(String name);

    /** 解析全局配置参数，并且返回其{@link Short}形式对象。第二个参数为默认值。 */
    public Short getShort(String name, Short defaultValue);

    /** 解析全局配置参数，并且返回其{@link Integer}形式对象。 */
    public Integer getInteger(String name);

    /** 解析全局配置参数，并且返回其{@link Integer}形式对象。第二个参数为默认值。 */
    public Integer getInteger(String name, Integer defaultValue);

    /** 解析全局配置参数，并且返回其{@link Long}形式对象。 */
    public Long getLong(String name);

    /** 解析全局配置参数，并且返回其{@link Long}形式对象。第二个参数为默认值。 */
    public Long getLong(String name, Long defaultValue);

    /** 解析全局配置参数，并且返回其{@link Float}形式对象。 */
    public Float getFloat(String name);

    /** 解析全局配置参数，并且返回其{@link Float}形式对象。第二个参数为默认值。 */
    public Float getFloat(String name, Float defaultValue);

    /** 解析全局配置参数，并且返回其{@link Double}形式对象。 */
    public Double getDouble(String name);

    /** 解析全局配置参数，并且返回其{@link Double}形式对象。第二个参数为默认值。 */
    public Double getDouble(String name, Double defaultValue);

    /** 解析全局配置参数，并且返回其{@link Date}形式对象。 */
    public Date getDate(String name);

    /** 解析全局配置参数，并且返回其{@link Date}形式对象。第二个参数为默认值。 */
    public Date getDate(String name, Date defaultValue);

    /** 解析全局配置参数，并且返回其{@link Date}形式对象。第二个参数为默认值。 */
    public Date getDate(String name, long defaultValue);

    /** 解析全局配置参数，并且返回其{@link Date}形式对象。 */
    public Date getDate(String name, String format);

    /** 解析全局配置参数，并且返回其{@link Date}形式对象。第三个参数为默认值。 */
    public Date getDate(String name, String format, Date defaultValue);

    /** 解析全局配置参数，并且返回其{@link Date}形式对象。第三个参数为默认值。 */
    public Date getDate(String name, String format, long defaultValue);

    /** 解析全局配置参数，并且返回其{@link Enum}形式对象。*/
    public <T extends Enum<?>> T getEnum(String name, Class<T> enmType);

    /** 解析全局配置参数，并且返回其{@link Enum}形式对象。第三个参数为默认值。 */
    public <T extends Enum<?>> T getEnum(String name, Class<T> enmType, T defaultValue);

    /** 解析全局配置参数，并且返回字符串（用于表示文件，结尾不带‘/’）。*/
    public String getFilePath(String name);

    /** 解析全局配置参数，并且返回字符串（用于表示文件，结尾不带‘/’）。第二个参数为默认值。 */
    public String getFilePath(String name, String defaultValue);

    /** 解析全局配置参数，并且返回字符串（用于表示目录，结尾带‘/’）。*/
    public String getDirectoryPath(String name);

    /** 解析全局配置参数，并且返回字符串（用于表示目录，结尾带‘/’）。第二个参数为默认值。 */
    public String getDirectoryPath(String name, String defaultValue);

    /** 解析全局配置参数，并且返回其{@link XmlNode}形式对象。 */
    public XmlNode getXmlNode(String name);
    //

    /** 解析全局配置参数，并且返回其{@link Character}形式对象。 */
    public Character[] getCharArray(String name);

    /** 解析全局配置参数，并且返回其{@link Character}形式对象。第二个参数为默认值。 */
    public Character[] getCharArray(String name, Character defaultValue);

    /** 解析全局配置参数，并且返回其{@link String}形式对象。 */
    public String[] getStringArray(String name);

    /** 解析全局配置参数，并且返回其{@link String}形式对象。第二个参数为默认值。 */
    public String[] getStringArray(String name, String defaultValue);

    /** 解析全局配置参数，并且返回其{@link Boolean}形式对象。 */
    public Boolean[] getBooleanArray(String name);

    /** 解析全局配置参数，并且返回其{@link Boolean}形式对象。第二个参数为默认值。 */
    public Boolean[] getBooleanArray(String name, Boolean defaultValue);

    /** 解析全局配置参数，并且返回其{@link Short}形式对象。 */
    public Short[] getShortArray(String name);

    /** 解析全局配置参数，并且返回其{@link Short}形式对象。第二个参数为默认值。 */
    public Short[] getShortArray(String name, Short defaultValue);

    /** 解析全局配置参数，并且返回其{@link Integer}形式对象。 */
    public Integer[] getIntegerArray(String name);

    /** 解析全局配置参数，并且返回其{@link Integer}形式对象。第二个参数为默认值。 */
    public Integer[] getIntegerArray(String name, Integer defaultValue);

    /** 解析全局配置参数，并且返回其{@link Long}形式对象。 */
    public Long[] getLongArray(String name);

    /** 解析全局配置参数，并且返回其{@link Long}形式对象。第二个参数为默认值。 */
    public Long[] getLongArray(String name, Long defaultValue);

    /** 解析全局配置参数，并且返回其{@link Float}形式对象。 */
    public Float[] getFloatArray(String name);

    /** 解析全局配置参数，并且返回其{@link Float}形式对象。第二个参数为默认值。 */
    public Float[] getFloatArray(String name, Float defaultValue);

    /** 解析全局配置参数，并且返回其{@link Double}形式对象。 */
    public Double[] getDoubleArray(String name);

    /** 解析全局配置参数，并且返回其{@link Double}形式对象。第二个参数为默认值。 */
    public Double[] getDoubleArray(String name, Double defaultValue);

    /** 解析全局配置参数，并且返回其{@link Date}形式对象。 */
    public Date[] getDateArray(String name);

    /** 解析全局配置参数，并且返回其{@link Date}形式对象。第二个参数为默认值。 */
    public Date[] getDateArray(String name, Date defaultValue);

    /** 解析全局配置参数，并且返回其{@link Date}形式对象。第二个参数为默认值。 */
    public Date[] getDateArray(String name, long defaultValue);

    /** 解析全局配置参数，并且返回其{@link Date}形式对象。 */
    public Date[] getDateArray(String name, String format);

    /** 解析全局配置参数，并且返回其{@link Date}形式对象。第三个参数为默认值。 */
    public Date[] getDateArray(String name, String format, Date defaultValue);

    /** 解析全局配置参数，并且返回其{@link Date}形式对象。第三个参数为默认值。 */
    public Date[] getDateArray(String name, String format, long defaultValue);

    /** 解析全局配置参数，并且返回其{@link Enum}形式对象。*/
    public <T extends Enum<?>> T[] getEnumArray(String name, Class<T> enmType);

    /** 解析全局配置参数，并且返回其{@link Enum}形式对象。第三个参数为默认值。 */
    public <T extends Enum<?>> T[] getEnumArray(String name, Class<T> enmType, T defaultValue);

    /** 解析全局配置参数，并且返回字符串（用于表示文件，结尾不带‘/’）。*/
    public String[] getFilePathArray(String name);

    /** 解析全局配置参数，并且返回字符串（用于表示文件，结尾不带‘/’）。第二个参数为默认值。 */
    public String[] getFilePathArray(String name, String defaultValue);

    /** 解析全局配置参数，并且返回字符串（用于表示目录，结尾带‘/’）。*/
    public String[] getDirectoryPathArray(String name);

    /** 解析全局配置参数，并且返回字符串（用于表示目录，结尾带‘/’）。第二个参数为默认值。 */
    public String[] getDirectoryPathArray(String name, String defaultValue);

    /** 解析全局配置参数，并且返回其{@link XmlNode}形式对象。 */
    public XmlNode[] getXmlNodeArray(String name);
}