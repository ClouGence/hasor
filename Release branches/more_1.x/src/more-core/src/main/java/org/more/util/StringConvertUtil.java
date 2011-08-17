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
package org.more.util;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.more.core.error.TransformException;
/**
 * 字符串数据类型转换工具类
 * @version 2009-4-29
 * @author 赵永春 (zyc@byshell.org)
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class StringConvertUtil {
    private static final Character DefaultValue_Character = ' ';
    private static final Boolean   DefaultValue_Boolean   = false;
    private static final Byte      DefaultValue_Byte      = 0;
    private static final Short     DefaultValue_Short     = 0;
    private static final Integer   DefaultValue_Integer   = 0;
    private static final Long      DefaultValue_Long      = 0l;
    private static final Float     DefaultValue_Float     = 0f;
    private static final Double    DefaultValue_Double    = 0d;
    /**
    * 解析字符串为最小长度的数字类型。如果value保存的是0~255之间的数则该方法回返回byte类型，如果是256则返回short类型。
    * 在比方说字符串值为125.6则返回值为float。
    * @param value 要解析为数字的字符串
    * @param defaultValue 如果数据错误取的默认值。
    * @return 返回解析的字符串，如果解吸失败则返回0或者默认值
    */
    public static Number parseNumber(final String value, final Number... defaultValue) {
        try {
            if (value.indexOf(".") != -1)
                try {
                    return Float.parseFloat(value);
                } catch (Exception e) {
                    return Double.parseDouble(value);
                }
            else
                try {
                    return Byte.parseByte(value);
                } catch (Exception e) {
                    try {
                        return Short.parseShort(value);
                    } catch (Exception e1) {
                        try {
                            return Integer.parseInt(value);
                        } catch (Exception e2) {
                            return Long.parseLong(value);
                        }
                    }
                }
        } catch (Exception e) {
            return (defaultValue.length >= 1) ? defaultValue[0] : 0;
        }
    }
    /**
     * 数据类型转换，只支持如下数据类型：String，StringBuffer，Integer，Byte，Character，Short，Long，Float，Double，Boolean，Date。
     * 示例：DataType.changeType("12",Integer.class,-1);返回值为12。DataType.changeType("aa",Integer.class,-1);返回值为-1。
     * 注意：如果不指定转换类型默认类型是转换到String类型。并且默认值是null。
     * @param value 要转换的数据。
     * @param toType 要转换的目标数据类型。
     * @param defaultValue 可变的参数第一个参数是要转换的类型， 第二个参数是转换到目标类型时如果失败采用的默认值。
     * @return 返回转换之后的值。
     */
    public static Object changeType(final Object value, final Class<?> toType, final Object... defaultValue) {
        if (value == null || toType == null)
            return null;
        String valueString = value.toString();
        Object defaultVar = (defaultValue.length >= 1) ? defaultValue[0] : null;
        // -----------可以直接转换
        if (toType.isAssignableFrom(value.getClass()) == true)
            return value;
        // -----------String形式
        else if (String.class == toType)
            return valueString;
        else if (StringBuffer.class == toType)
            return new StringBuffer(valueString);
        else if (Integer.class == toType || int.class == toType)
            return StringConvertUtil.parseInt(valueString, (Integer) defaultVar);
        else if (Byte.class == toType || byte.class == toType)
            return StringConvertUtil.parseByte(valueString, (Byte) defaultVar);
        else if (Character.class == toType || char.class == toType) {
            if (valueString.equals("") == true)
                return DefaultValue_Character;
            return Character.valueOf(valueString.charAt(0));
        } else if (Short.class == toType || short.class == toType)
            return StringConvertUtil.parseShort(valueString, (Short) defaultVar);
        else if (Long.class == toType || long.class == toType)
            return StringConvertUtil.parseLong(valueString, (Long) defaultVar);
        else if (Float.class == toType || float.class == toType)
            return StringConvertUtil.parseFloat(valueString, (Float) defaultVar);
        else if (Double.class == toType || double.class == toType)
            return StringConvertUtil.parseDouble(valueString, (Double) defaultVar);
        else if (Boolean.class == toType || boolean.class == toType)
            return StringConvertUtil.parseBoolean(valueString);
        else if (Date.class.isAssignableFrom(toType) == true) {
            if (value instanceof Date == true)
                return value;
            return StringConvertUtil.parseDate(valueString);
        }
        // -----------处理枚举
        else if (Enum.class.isAssignableFrom(toType) == true) {
            Class<Enum<?>> e = (Class<Enum<?>>) toType;
            for (Enum<?> item : e.getEnumConstants()) {
                String enumValue = item.name().toLowerCase();
                if (enumValue.equals(valueString.toLowerCase()) == true)
                    return item;
            }
            return defaultVar;
        } else
            throw new TransformException("from [" + value.getClass() + "] to [" + toType + "]不支持的转换类型。");
    }
    /**
     * 将字符类型数据转换成int类型数据。如果字符串格式非法其默认值为0。示例：
     * DataType.getInt("aa",0);返回0或者DataType.getInt("12",0);返回12
     * @param value 数据字符串。
     * @param defaultValue 如果数据错误取的默认值。
     * @return 返回int的转换结果。
     */
    public static Integer parseInt(final String value, final Integer... defaultValue) {
        try {
            return (value == null || value.equals("") == false) ? Integer.valueOf(value) : defaultValue[0];
        } catch (Exception e) {
            return (defaultValue.length >= 1) ? defaultValue[0] : StringConvertUtil.DefaultValue_Integer;
        }
    }
    /**
     * 将字符类型数据转换成float类型数据。如果字符串格式非法其默认值为0.0。示例：
     * DataType.getFloat("aa",0);返回0或者DataType.getFloat("12.8",0);返回12.8
     * @param value 数据字符串。
     * @param defaultValue 如果数据错误取的默认值。
     * @return 返回float的转换结果。
     */
    public static Float parseFloat(final String value, final Float... defaultValue) {
        try {
            float var = (value == null || value.equals("") == false) ? Float.valueOf(value) : defaultValue[0];
            if (Float.isNaN(var) == true || Float.isInfinite(var) == true)
                return (defaultValue.length >= 1) ? defaultValue[0] : StringConvertUtil.DefaultValue_Float;
            return var;
        } catch (Exception e) {
            return (defaultValue.length >= 1) ? defaultValue[0] : StringConvertUtil.DefaultValue_Float;
        }
    }
    /**
     * 将字符类型数据转换成double类型数据。如果字符串格式非法其默认值为0.0。示例：
     * DataType.getDouble("aa",0);返回0或者DataType.getDouble("12.8",0);返回12.8
     * @param value 数据字符串。
     * @param defaultValue 如果数据错误取的默认值。
     * @return 返回double的转换结果。
     */
    public static Double parseDouble(final String value, final Double... defaultValue) {
        try {
            return (value == null || value.equals("") == false) ? Double.valueOf(value) : defaultValue[0];
        } catch (Exception e) {
            return (defaultValue.length >= 1) ? defaultValue[0] : StringConvertUtil.DefaultValue_Double;
        }
    }
    /**
     * 将字符类型数据转换成boolean类型数据。(0,no,N)表示false，(1,yes,Y)表示yes
     * @param value 数据字符串。
     * @return 返回boolean的转换结果。
     */
    public static Boolean parseBoolean(final String value, final Boolean... defaultValue) {
        if (value == null)
            return (defaultValue.length >= 1) ? defaultValue[0] : StringConvertUtil.DefaultValue_Boolean; //false;
        else if (value.equals("0") == true || value.equals("no") == true || value.equals("N") == true)
            return (defaultValue.length >= 1) ? defaultValue[0] : StringConvertUtil.DefaultValue_Boolean; //false;
        else if (value.equals("1") == true || value.equals("yes") == true || value.equals("Y") == true)
            return (defaultValue.length >= 1) ? defaultValue[0] : !StringConvertUtil.DefaultValue_Boolean; //true;
        else
            return Boolean.parseBoolean(value);
    }
    /**
     * 将字符类型数据转换成long类型数据。如果字符串格式非法其默认值为0。示例：
     * DataType.getLong("aa",0);返回0或者DataType.getLong("123",0);返回123
     * @param value 数据字符串。
     * @param defaultValue 如果数据错误取的默认值。
     * @return 返回long的转换结果。
     */
    public static Long parseLong(final String value, final Long... defaultValue) {
        try {
            return (value == null || value.equals("") == false) ? Long.valueOf(value) : defaultValue[0];
        } catch (Exception e) {
            return (defaultValue.length >= 1) ? defaultValue[0] : StringConvertUtil.DefaultValue_Long;
        }
    }
    /**
     * 将字符类型数据转换成byte类型数据。如果字符串格式非法其默认值为0。示例：
     * DataType.getByte("aa",0);返回0或者DataType.getByte("123",0);返回123
     * @param value 数据字符串。
     * @param defaultValue 如果数据错误取的默认值。
     * @return 返回byte的转换结果。
     */
    public static Byte parseByte(final String value, final Byte... defaultValue) {
        try {
            return (value == null || value.equals("") == false) ? Byte.valueOf(value) : defaultValue[0];
        } catch (Exception e) {
            return (defaultValue.length >= 1) ? defaultValue[0] : StringConvertUtil.DefaultValue_Byte;
        }
    }
    /**
     * 将字符类型数据转换成short类型数据。如果字符串格式非法其默认值为0。示例：
     * DataType.getShort("aa",0);返回0或者DataType.getShort("123",0);返回123
     * @param value 数据字符串。
     * @param defaultValue 如果数据错误取的默认值。
     * @return 返回short的转换结果。
     */
    public static Short parseShort(final String value, final Short... defaultValue) {
        try {
            return (value == null || value.equals("") == false) ? Short.valueOf(value) : defaultValue[0];
        } catch (Exception e) {
            return (defaultValue.length >= 1) ? defaultValue[0] : StringConvertUtil.DefaultValue_Short;
        }
    }
    /**
     * 将字符串转化为集合类型。在转化过程中可以指定分割符转换类型以及相应类型的默认转换值。
     * 类型的默认转换值是指当原数据在像目标转换时发生异常而采用的默认值取代。 语法如下：<br/>
     * 1。DataType.getList("a,b,c,3,4,5");默认转换，该种方式转换是将原始数据按照逗号分割 转换结果是字符串集合<br/>
     * 2。DataType.getList("a;b;c;3;4;5",";");指定分割符转换，该种类型转换是在默认转换之上使调用的程序对转换时使用
     * 的分割符拥有了设置权利。<br/>
     * 3。DataType.getList("a;b;c;3;4;5",";",Integer.class);指定类型转换，该种转换是在第二种
     * 转换之上得来，次类型转换的返回集合结果使用指定类型。<br/>
     * 4。DataType.getList("a;b;c;3;4;5",";",Integer.class,-1);带默认值的指定类型转换。
     * 就上述例子来看返回结果应该是-1,-1,3,4,5。集合中存放的类型是Integer。<br/>
     * 5。DataType.getList("a;b;c;3;4;5",";",Integer.class,-1,newArrayList());
     * 第5种方式与第四种方式一样，不同的是第四种方式是函数本身内部会创建一个集合对象，而第5种方式 由用户提供这个集合对象。
     * 6。DataType.getList("a;b;c;3;4;5",";",Integer.class,-1,newArrayList(),true|false);
     * 第6种方式与第五种方式一样，第六种工作方式与第五种的区别仅仅在于。如果解析的List结果集合中在用户传递的List集合中出现冲突，是否替换原有数据取决于用户
     * true表示替换，false表示不替换。
     * @param in_value 数据字符串。
     * @param param 参数，详细查看函数说明。
     * @return 返回转换的集合对象。
     */
    public static List parseList(final String in_value, final Object... param) {
        String value = (in_value == null) ? "" : in_value;
        // -------------------
        String split = ",";// 默认分割符。
        Class<?> toType = String.class;// 默认String类型
        Object defaultValue = null;// 默认值是null。
        List array = null;
        boolean replay = true;// 默认值是true 替换。
        // -------------------
        if (param.length == 0) {
            // 没有参数
            array = new ArrayList<Object>(0);
        } else if (param.length == 1) {
            // 一个参数
            split = (String) param[0];
            array = new ArrayList<Object>(0);
        } else if (param.length == 2) {
            // 两个参数
            split = (String) param[0];
            toType = (Class<?>) param[1];
            array = new ArrayList<Object>(0);
        } else if (param.length == 3) {
            // 三个参数
            split = (String) param[0];
            toType = (Class<?>) param[1];
            defaultValue = param[2];
            array = new ArrayList<Object>(0);
        } else if (param.length == 4) {
            // 四个参数
            split = (String) param[0];
            toType = (Class<?>) param[1];
            defaultValue = param[2];
            array = (List<?>) param[3];
        } else {
            // 五个参数
            split = (String) param[0];
            toType = (Class<?>) param[1];
            defaultValue = param[2];
            array = (List<?>) param[3];
            replay = StringConvertUtil.parseBoolean(param[4].toString());
        }
        // -------------------
        String[] temp_split = value.split(split);
        for (String var : temp_split)
            if (array.contains(var) == true)
                if (replay == true) {
                    array.remove(var);
                    array.add(StringConvertUtil.changeType(var, toType, defaultValue));
                } else {}
            else
                array.add(StringConvertUtil.changeType(var, toType, defaultValue));
        return array;
    }
    /**
     * 将字符串转化为数组类型。用法参照parseList方法。不同的是该方法返回的是List的数组<br/> List array =
     * parseList(value, param); array.toArray();
     * @param in_value 数据字符串。
     * @param param 参数，详细查看函数说明。
     * @return 返回转换的数组对象。
     */
    public static Object[] parseArray(final String in_value, final Object... param) {
        String value = (in_value == null) ? "" : in_value;
        // -------------------
        List array = parseList(value, param);
        return array.toArray();
    }
    /**
     * 将字符串转化为集合类型。在转化过程中可以指定分割符转换类型以及相应类型的默认转换值。
     * 类型的默认转换值是指当原数据在像目标转换时发生异常而采用的默认值取代。 语法如下：<br/>
     * 1。DataType.getMap("key=value;key1=value1;key2=value2;");默认转换，该种方式
     * 转换是将原始数据按照逗号分割转换结果是字符串集合<br/>
     * 2。DataType.getMap("key=value&key1=value1&key2=value2","=&");指定分割符转换，
     * 该种类型转换是在默认转换之上使调用的程序对转换时使用的分割符拥有了设置权利。<br/>
     * 3。DataType.getMap("key=1&key1=2&key2=3","=&",String.class,Integer.class);
     * 指定类型转换，该种转换是在第二种转换之上得来，次类型转换的返回集合结果使用指定类型。<br/>
     * 4。DataType.getMap("key=1&key1=a&key2=3","=&",String.class,Integer.class,-1);
     * 带默认值的指定类型转换。就上述例子来看返回结果应该是key=1,key1=-1,key2=3。集合中存放
     * 的类型是String,Integer。注意：此处的默认值是key=value，value的默认值。<br/>
     * 5。DataType.getMap("key=1&key1=a&key2=3","=&",String.class,Integer.class,-1,newHashtable());
     * 第5种方式与第四种方式一样，不同的是第四种方式是函数本身内部会创建一个集合对象，而第5种方式 由用户提供这个集合对象。
     * 提示：如果解析的Map结果集合中在用户传递的Map集合中出现key冲突，则第5种方式将使用解析之后的结果替换原有的key和value。
     * <br/>默认该函数效果如下：DataType.getMap("key=value;key1=value1","=;",String.class,String.class,null,newHashMap<String,String>());
     * 6。DataType.getMap("key=1&key1=a&key2=3","=&",String.class,Integer.class,-1,newHashtable(),true|false);
     * 第6种方式与第五种方式一样，第六种工作方式与第五种的区别仅仅在于。如果解析的Map结果集合中在用户传递的Map集合中出现key冲突，是否替换原有数据取决于用户
     * true表示替换，false表示不替换。<br/>默认该函数效果如下：DataType.getMap("key=value;key1=value1","=;",String.class,String.class,null,newHashMap<String,String>(),false);
     * @param in_value 数据字符串。
     * @param param 参数，详细查看函数说明。
     * @return 返回转换的集合对象。
     */
    public static Map parseMap(final String in_value, final Object... param) {
        String value = (in_value == null) ? "" : in_value;
        // -------------------
        String split_key = "=";// 默认分割符1。
        String split_val = ";";// 默认分割符2。
        Class<?> toType_key = String.class;// key默认String类型
        Class<?> toType_val = String.class;// val默认String类型
        Object defaultValue = null;// 默认值是null。
        Map array = null;
        boolean replay = true;// 默认值是true 替换。
        // -------------------
        if (param.length == 0) {
            // 没有参数
            array = new HashMap<String, String>(0);
        } else if (param.length == 1) {
            // 一个参数
            String split = (String) param[0];
            if (split.length() == 1)
                split_key = String.valueOf(split.charAt(0));
            else {
                split_key = String.valueOf(split.charAt(0));
                split_val = String.valueOf(split.charAt(1));
            }
            array = new HashMap<String, String>();
        } else if (param.length == 2) {
            // 两个参数
            String split = (String) param[0];
            if (split.length() == 1)
                split_key = String.valueOf(split.charAt(0));
            else {
                split_key = String.valueOf(split.charAt(0));
                split_val = String.valueOf(split.charAt(1));
            }
            toType_key = (Class<?>) param[1];
            array = new HashMap<String, String>();
        } else if (param.length == 3) {
            // 三个参数
            String split = (String) param[0];
            if (split.length() == 1)
                split_key = String.valueOf(split.charAt(0));
            else {
                split_key = String.valueOf(split.charAt(0));
                split_val = String.valueOf(split.charAt(1));
            }
            toType_key = (Class<?>) param[1];
            toType_val = (Class<?>) param[2];
            array = new HashMap<String, String>();
        } else if (param.length == 4) {
            // 四个参数
            String split = (String) param[0];
            if (split.length() == 1)
                split_key = String.valueOf(split.charAt(0));
            else {
                split_key = String.valueOf(split.charAt(0));
                split_val = String.valueOf(split.charAt(1));
            }
            toType_key = (Class<?>) param[1];
            toType_val = (Class<?>) param[2];
            defaultValue = param[3];
            array = new HashMap<String, String>();
        } else if (param.length == 5) {
            // 五个参数
            String split = (String) param[0];
            if (split.length() == 1)
                split_key = String.valueOf(split.charAt(0));
            else {
                split_key = String.valueOf(split.charAt(0));
                split_val = String.valueOf(split.charAt(1));
            }
            toType_key = (Class<?>) param[1];
            toType_val = (Class<?>) param[2];
            defaultValue = param[3];
            array = (Map) param[4];
        } else {
            // 六个参数
            String split = (String) param[0];
            if (split.length() == 1)
                split_key = String.valueOf(split.charAt(0));
            else {
                split_key = String.valueOf(split.charAt(0));
                split_val = String.valueOf(split.charAt(1));
            }
            toType_key = (Class<?>) param[1];
            toType_val = (Class<?>) param[2];
            defaultValue = param[3];
            array = (Map) param[4];
            replay = StringConvertUtil.parseBoolean(param[4].toString());
        }
        // -------------------
        String[] temp_split = value.split(split_val);// key=value
        for (String var : temp_split) {
            String[] over_split = var.split(split_key);
            if (over_split.length != 2)
                continue;
            Object ov_key = StringConvertUtil.changeType(over_split[0], toType_key);
            Object ov_var = StringConvertUtil.changeType(over_split[1], toType_val, defaultValue);
            if (array.containsKey(ov_key) == true)
                if (replay == true) {
                    array.remove(ov_key);
                    array.put(ov_key, ov_var);
                } else {}
            else
                array.put(ov_key, ov_var);
        }
        return array;
    }
    /**
     * 此方法用于字符串转换成时间类型。默认格式：yyyy/MM/dd-hh:mm:ss 默认时间：系统当前时间 时间格式表示说明：yyyy:表示年
     * MM：表示月 dd 表示天 hh:表示时mm:表示分 ss：表示秒
     * 示例：Convert.parseDate("2007/05/05","yyyy/MM/dd");
     * @param value 数据字符串。
     * @param patam 时间格式化格式字符串。
     * @return 返回Date的转换结果。
     * @throws ParseException
     */
    public static Date parseDate(String value, String... patam) {
        String formatString = null;
        Date defaultValue = null;
        // -------------------
        if (patam.length == 0) {
            defaultValue = new Date();
            formatString = "yyyy/MM/dd-hh:mm:ss";
        } else if (patam.length == 1)
            formatString = (patam[0] == null) ? "yyyy/MM/dd-hh:mm:ss" : (String) patam[0];
        // -------------------
        if (value == null || value.equals(""))
            return defaultValue;
        try {
            SimpleDateFormat sf = new SimpleDateFormat(formatString);
            Date res = sf.parse(value);
            return res;
        } catch (Exception e) {
            return defaultValue;
        }
    }
    /**
     * 将字符类型数据转换成指定的Enum类型数据，如果转换失败则返回null。
     * @param value 数据字符串。
     * @param forEnum 要转换的枚举类型。
     * @param defaultValue 默认值
     * @return 返回enum的转换结果。
     */
    public static <T extends Enum> T parseEnum(final String value, final Class<? extends Enum<?>> forEnum, Enum<?>... defaultValue) {
        for (Enum<?> item : forEnum.getEnumConstants()) {
            String enumValue = item.name().toLowerCase();
            if (enumValue.equals(value.toLowerCase()) == true)
                return (T) item;
        }
        return (T) ((defaultValue.length >= 1) ? defaultValue[0] : null);
    }
}