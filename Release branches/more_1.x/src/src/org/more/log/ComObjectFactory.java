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
package org.more.log;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.more.util.StringConvert;
/**
 * 组建缓存，构造器
 * @version 2009-5-13
 * @author 赵永春 (zyc@byshell.org)
 */
class ComObjectFactory {
    private Map<String, Object> comCache   = new Hashtable<String, Object>(0);
    private ComFactory          comFactory = null;
    private final String[]      keys       = new String[1];                   // 存放属性保留字或者关键字。
    // ===============================================================
    /**
     * ComObjectFactory构造器
     * @param comFactory 日志系统工厂类。
     */
    public ComObjectFactory(ComFactory comFactory) {
        keys[0] = "class";
        this.comFactory = comFactory;
    }
    // ===============================================================
    /**
     * 从日志组建工厂中获得一个日志组建。
     * @param name 组建名。
     * @return 返回日志组建。
     * @throws LogException 如果发生日志异常。
     */
    public Object getComObject(String name, Object... params) throws LogException {
        // 获得组建。
        // 给组建注入属性
        // 缓存组建
        // -------------------------------------
        // class 类位置
        // name 名称
        // singleton 单态
        // formater 格式化对象 -- ILog 特有属性
        // allwrite 所有级别通用输出 -- ILog 特有属性
        // debugwrite 调试级别输出 -- ILog 特有属性
        // infowrite 信息级别输出 -- ILog 特有属性
        // errorwrite 错误级别输出 -- ILog 特有属性
        // warningwrite 警告级别输出 -- ILog 特有属性
        // -------------------------------------
        name = name.trim();
        ComBean cb = this.comFactory.getComBean(name);
        if (cb == null)
            throw new LogException("找不到组建[" + name + "]定义");
        //
        if (this.comCache.containsKey(name))
            return this.comCache.get(name);
        //
        if (cb.getCommand("class") == null)
            throw new LogException("组建[" + name + "]没有定义必须的属性class");
        Object obj = null;
        try {
            Class<?> cls = Class.forName(cb.getCommand("class").getValue());
            obj = cls.newInstance();
            //
            if (cb.getName() == null)
                throw new LogException("组建没有定义名称属性");
            //
            List<CommandAttribut> calist = cb.getCommand();
            for (CommandAttribut ca : calist) {
                boolean mack = false;// 标记
                for (String key : this.keys)
                    if (ca.getName().equals(key))
                        mack = true;
                if (mack)
                    continue;
                //
                if (ca.getName().equals("formater") && ILog.class.isAssignableFrom(cls)) {// 格式化对象
                    // -- ILog 特有属性
                    try {
                        Method method = cls.getMethod("setFormater", ILogFormater.class);
                        method.invoke(obj, (ILogFormater) this.getComObject(ca.getValue(), params));
                    } catch (Exception e) {
                        throw new LogException("在向formater特有属性输出时出现异常，" + e.getMessage());
                    }
                } else if (ca.getName().equals("allwrite") && ILog.class.isAssignableFrom(cls))// 所有级别通用输出
                    // -- ILog 特有属性
                    this.putWrite(obj, cls, ca.getValue(), ILog.LogLevel_ALL, params);
                else if (ca.getName().equals("debugwrite") && ILog.class.isAssignableFrom(cls))// 调试级别输出
                    // -- ILog 特有属性
                    this.putWrite(obj, cls, ca.getValue(), ILog.LogLevel_Debug, params);
                else if (ca.getName().equals("infowrite") && ILog.class.isAssignableFrom(cls))// 信息级别输出
                    // -- ILog 特有属性
                    this.putWrite(obj, cls, ca.getValue(), ILog.LogLevel_Info, params);
                else if (ca.getName().equals("errorwrite") && ILog.class.isAssignableFrom(cls))// 错误级别输出
                    // -- ILog 特有属性
                    this.putWrite(obj, cls, ca.getValue(), ILog.LogLevel_Error, params);
                else if (ca.getName().equals("warningwrite") && ILog.class.isAssignableFrom(cls))// 警告级别输出
                    // -- ILog 特有属性
                    this.putWrite(obj, cls, ca.getValue(), ILog.LogLevel_Warning, params);
                else {
                    PropertyDescriptor pd = new PropertyDescriptor(ca.getName(), cls);
                    Object o = null;
                    if (pd.getPropertyType().isAssignableFrom(String.class)) {
                        String pStr_1 = "\\x20*\\{\\$\\d+\\}(\\\\.+)*\\\\?";// 1.{$n}
                        // param
                        String value = ca.getValue();
                        if (Pattern.matches(pStr_1, value) == true) {
                            // {$n}
                            Matcher ma_tem = Pattern.compile(pStr_1).matcher(value);
                            ma_tem.find();
                            int paramIndex = StringConvert.parseInt(ma_tem.group(1), 0);
                            Object tem_o = ((paramIndex < params.length) ? params[paramIndex] : null);
                            o = tem_o.toString() + ca.getValue().split("\\{\\$" + paramIndex + "\\}")[1];
                        }
                    } else
                        o = ca.getValue();
                    pd.getWriteMethod().invoke(obj, o);
                }
            }
        } catch (ClassNotFoundException e) {
            throw new LogException("无法找到类[" + cb.getCommand("class").getValue() + "]");
        } catch (InstantiationException e) {
            throw new LogException("构造类[" + cb.getCommand("class").getValue() + "]对象失败，对象接口或是一个抽象类。");
        } catch (IllegalAccessException e) {
            throw new LogException("调用失败[" + e.getMessage() + "]对象失败，无法调用目标方法。");
        } catch (IntrospectionException e) {
            throw new LogException("创建属性读写器PropertyDescriptor失败，" + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new LogException("向写属性时候不合法或不正确的参数，" + e.getMessage());
        } catch (InvocationTargetException e) {
            throw new LogException("成功调用目标，但目标抛出异常，" + e.getMessage());
        }
        //
        if (cb.getCommand("singleton") == null || StringConvert.parseBoolean(cb.getCommand("singleton").getValue()) == true)
            this.comCache.put(name, obj);
        //
        return obj;
    }
    /**
     * 向日志输出对象中设置不同级别日志输出对象。
     * @param obj 要输出属性的日志组建对象
     * @param clas 日志组建对象的所对应Class类对象。
     * @param value 日志输出对象在level级别上使用的输出组建名称列表。
     * @param level 要输出的级别。
     * @param params 对象参数。
     * @throws LogException 如果发生日志异常。
     */
    private void putWrite(Object obj, Class<?> clas, String value, String level, Object... params) throws LogException {
        try {
            Method method = clas.getMethod("addWrite", ILogWrite.class, String.class);
            String[] names = value.split(",");
            for (String str : names)
                method.invoke(obj, this.getComObject(str, params), level);
        } catch (Exception e) {
            throw new LogException("在向allwrite，debugwrite，infowrite，errorwrite，warningwrite特有属性输出时出现异常，" + e.getMessage());
        }
    }
    /** 清空缓存 */
    public void clear() {
        this.comCache.clear();
    }
}