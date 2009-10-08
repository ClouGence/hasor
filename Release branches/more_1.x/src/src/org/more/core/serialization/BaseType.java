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
package org.more.core.serialization;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.more.CastException;
import org.more.RepeateException;
/**
 * more对象序列化类型抽象类，该类主要功能是定义子类型的公共方法。也负责装载子类型到静态字段中。
 * 使用getOriginalName()方法可以获得到类型的原类型名称，原类型名与平台无关。使用toString和
 * toObject方法可以实现对象的相互转化功能。在more序列化组建中类型的优先级是如下安排的。<br/>
 * NullType > BooleanType > NumberType > StringType > ArrayType > UserType > TableType
 * Date : 2009-7-7
 * @author 赵永春
 */
public abstract class BaseType {
    //===================================================================================================================全局静态属性，常量，方法
    /** more系统的类型原类型名前缀 */
    private static String                          Prefix_Type = "More";
    /** 该集合保存的是more序列化组建所支持的基本类型列表，more序列化中只有：1数字，2字符串，3布尔，4空值，5数组，6表、7自定义类型。七种基本类型。 */
    private static LinkedHashMap<String, BaseType> types       = new LinkedHashMap<String, BaseType>();
    /** 该代码块的目的是装载系统已经定义的六个类型。并且初始化types静态字段 */
    static {
        BaseType type = null;
        //
        type = new NullType();
        BaseType.types.put(type.getOriginalName(), type);
        //
        type = new BooleanType();
        BaseType.types.put(type.getOriginalName(), type);
        //
        type = new NumberType();
        BaseType.types.put(type.getOriginalName(), type);
        //
        type = new StringType();
        BaseType.types.put(type.getOriginalName(), type);
        //
        type = new ArrayType();
        BaseType.types.put(type.getOriginalName(), type);
        //
        type = new TableType();
        BaseType.types.put(type.getOriginalName(), type);
        //
        InputStream in = BaseType.class.getResourceAsStream("/org/more/core/serialization/type/config.properties");
        BufferedReader sr = new BufferedReader(new InputStreamReader(in));
        while (true) {
            try {
                String readString = sr.readLine();
                if (readString == null)
                    break;
                Class<?> userType = Class.forName(readString);
                BaseType.regeditType((UserType) userType.newInstance());
            } catch (Exception e) {}
        }
    }
    /**
     * 移除指定原始类型名的自定义类型注册。如果企图移除的类型不存在或者企图删除非自定义类型则错误将被忽略。
     * @param originalName 要删除的类型原始类型名。
     */
    public synchronized static void removeRegeditType(String originalName) {
        BaseType type = BaseType.types.get(originalName);
        if (type == null || type instanceof UserType == false) {} else
            BaseType.types.remove(originalName);
    }
    /**
     * 注册一个自定义类型以兼容特殊要求序列化的对象。一种自定义类型只能注册一次，如果注册多次会引发RepeateException异常。
     * 类型的重复判断是依靠类型的原始类型名进行的。
     * @param type 要注册的类型。
     * @throws RepeateException 如果对一个类型进行了重复注册则会引发该异常。
     */
    public synchronized static void regeditType(UserType type) throws RepeateException {
        if (BaseType.types.containsKey(type.getOriginalName()) == false) {
            BaseType.types.put(type.getOriginalName(), type);
            BaseType last = BaseType.types.get("[More Table]");
            BaseType.types.remove(last.getOriginalName());
            BaseType.types.put(last.getOriginalName(), last);
        } else
            throw new RepeateException("不能重复注册类型:" + type.getOriginalName());
    }
    /**
     * 获取已经注册的自定义类型列表。
     * @return 返回已经注册的自定义类型列表。
     */
    public synchronized static UserType[] userTypeList() {
        ArrayList<UserType> list = new ArrayList<UserType>();
        for (BaseType type : BaseType.types.values())
            if (type instanceof UserType)
                list.add((UserType) type);
        //
        UserType[] r = new UserType[list.size()];
        list.toArray(r);
        return r;
    }
    /**
     * 查找一个可以处理对象到字符串转换的more序列化类型对象以处理数据。返回可以处理这个类型的序列化类型对象。
     * 如果找不到则返回null。
     * @param object 要被查找类型的序列化对象。
     * @return 返回查找一个可以处理对象到字符串转换的more序列化类型对象以处理数据。返回可以处理这个类型的序列化类型对象。
     */
    public static BaseType findType(Object object) {
        for (BaseType type : BaseType.types.values()) {
            if (type.testObject(object) == true)
                return type;
        }
        return null;
    }
    /**
     * 查找一个可以处理字符串到对象的more序列化类型对象以处理数据。返回可以处理这个类型的反序列化类型对象。
     * 如果找不到则返回null。
     * @param string 要被查找类型的序列化对象。
     * @return 返回查找一个可以处理字符串到对象的more序列化类型对象以处理数据。返回可以处理这个类型的反序列化类型对象。
     */
    public static BaseType findType(String string) {
        for (BaseType type : BaseType.types.values()) {
            if (type.testString(string) == true)
                return type;
        }
        return null;
    }
    /**
     * 根据指定的原始类型名查找这个类型对象并且返回。如果找不到指定类型则返回null。
     * @param originalName 要查找类型的原始类型名。
     * @return 返回根据指定的原始类型名查找这个类型对象并且返回。如果找不到指定类型则返回null。
     */
    public static BaseType findTypeByOriginalName(String originalName) {
        return BaseType.types.get(originalName);
    }
    //===================================================================================================================类型特有的属性方法
    /**
     * 获得类型的原始类型名。原始类型名的格式是“[Prefix Type]”。原始类型名是与语言平台无关的一种类型名，这种类型是more序列化组建特有的类型。
     * more序列化组建为了达到对象在开发语言平台上统一所以采用自己的独立数据类型。这些数据类型只有六种，其中任意bean对象在more序列化组建中是以
     * 表这种类型存在的。more序列化组建所支持的类型有1数字，2字符串，3布尔，4空值，5数组，6表。
     * @return 返回类型的原始类型名。
     */
    public String getOriginalName() {
        return "[" + BaseType.Prefix_Type + " " + this.getShortOriginalName() + "]";
    }
    /**
     * 测试目标字符串是否可以使用当前类型做转换。如果支持返回true，否则返回false。
     * @param string 要被测试的目标对象。
     * @return 测试目标字符串是否可以使用当前类型做转换。如果支持返回true，否则返回false。
     */
    public abstract boolean testString(String string);
    /**
     * 测试目标对象是否可以使用当前类型做转换。如果支持返回true，否则返回false。
     * @param object 要被测试的目标对象。
     * @return 测试目标对象是否可以使用当前类型做转换。如果支持返回true，否则返回false。
     */
    public abstract boolean testObject(Object object);
    /**
     * 执行序列化操作并且返回序列化对象之后的字符串。
     * @param object 要序列化的对象。
     * @return 返回序列化对象的字符串表现形式。
     * @throws CastException 当执行数据转换时发生异常。
     */
    public abstract String toString(Object object) throws CastException;
    /**
     * 对序列化之后的字符串进行反序列化，以还原其类型。
     * @param string 要反序列化的字符串。
     * @return 对序列化之后的字符串进行反序列化，以还原其类型。
     * @throws CastException 当执行数据转换时发生异常。
     */
    public abstract Object toObject(String string) throws CastException;
    /**
     * 该方法是被保护的方法，该方法返回类型的具体类型名。该方法的返回值将被getOriginalName调用。
     * getOriginalName方法会自动组合类型原始名。该方法的返回值是以下格式String,Integer,Short...
     * @return 返回类型的具体类型名。
     */
    protected abstract String getShortOriginalName();
}