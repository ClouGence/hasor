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
package org.more.core.copybean;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.more.core.error.InitializationException;
import org.more.core.error.SupportException;
import org.more.util.ResourcesUtil;
import org.more.util.StringConvertUtil;
import org.more.util.attribute.IAttribute;
/**
 * Bean拷贝工具类，这个工具是实现了可以将Bean属性拷贝到其他bean中或者拷贝到map中。
 * 开发者可以通过扩展BeanType类以接受更多的bean类型。系统中已经支持了
 * Map,Object,IAttribute,ServletRequest.getParameterMap()。
 * 提示：默认的拷贝方式是深拷贝(value)，如果需要浅拷贝则需要调用changeDefaultCopy方法
 * 改变其拷贝类型。可选的拷贝类型有两个一个是深拷贝(value)，一个是浅拷贝(ref)。
 * 实际上深拷贝会处理java8个基本类型加上string以及date。一共10个类型。这10个类型会在深拷贝中以浅拷贝方式执行。
 * @version 2009-5-20
 * @author 赵永春 (zyc@byshell.org)
 */
@SuppressWarnings("unchecked")
public abstract class CopyBeanUtil {
    /*顺序是优先级顺序*/
    public static final String[]              configs            = new String[] { "META-INF/resource/core/copybean_config.properties", "META-INF/copybean_config.properties", "copybean_config.properties" };
    private ArrayList<Convert<Object>>        convertList        = new ArrayList<Convert<Object>>();
    private ArrayList<PropertyReader<Object>> propertyReaderList = new ArrayList<PropertyReader<Object>>();
    private ArrayList<PropertyWrite<Object>>  propertyWriteList  = new ArrayList<PropertyWrite<Object>>();
    private boolean                           nullValueCP        = true;
    //
    //
    /**创建JsonUtil对象，字符串序列化使用双引号环抱。 */
    protected CopyBeanUtil() throws IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        for (String cfg : configs) {
            IAttribute<String> attList = ResourcesUtil.getPropertys(cfg);
            String convertList = attList.getAttribute("ConvertList");
            if (convertList != null) {
                String[] $convertList = convertList.split(",");
                for (String $con : $convertList)
                    this.convertList.add((Convert<Object>) this.createObject($con, loader));
            }
            String propertyReaderList = attList.getAttribute("PropertyReaderList");
            if (propertyReaderList != null) {
                String[] $propertyReaderList = propertyReaderList.split(",");
                for (String $prop : $propertyReaderList)
                    this.propertyReaderList.add((PropertyReader<Object>) this.createObject($prop, loader));
            }
            String propertyWriteList = attList.getAttribute("PropertyWriteList");
            if (propertyWriteList != null) {
                String[] $propertyWriteList = propertyWriteList.split(",");
                for (String $prop : $propertyWriteList)
                    this.propertyWriteList.add((PropertyWrite<Object>) this.createObject($prop, loader));
            }
        }
        IAttribute<String> attList = ResourcesUtil.getPropertys(configs);
        nullValueCP = StringConvertUtil.parseBoolean(attList.getAttribute("NullValueCP"), true);
    };
    private Object createObject(String className, ClassLoader loader) {
        try {
            Class<?> cls = loader.loadClass(className);
            return cls.newInstance();
        } catch (Exception e) {
            throw new InitializationException(e);
        }
    }
    public int copyPropertys(Object fromObject, Object toObject) {
        if (fromObject == null || toObject == null)
            return 0;//不执行拷贝。
        //
        for (PropertyReader<Object> reader : this.propertyReaderList) {
            Class<?> fromClass = fromObject.getClass();
            if (reader.getTargetClass().isAssignableFrom(fromClass) == false)
                continue;
            List<String> propertysNames = reader.getPropertyNames(fromObject);
            return this.copyPropertys(fromObject, toObject, propertysNames);
        }
        return 0;//没有拷贝到东西.
    };
    public int copyPropertys(Object fromObject, Object toObject, List<String> propertysNames) {
        if (fromObject == null || toObject == null || propertysNames == null)
            return 0;//不执行拷贝。
        HashMap<String, String> propertysMapping = new HashMap<String, String>();
        for (String name : propertysNames)
            propertysMapping.put(name, name);
        return this.copyPropertys(fromObject, toObject, propertysMapping);
    };
    public int copyPropertys(Object fromObject, Object toObject, Map<String, String> propertysMapping) {
        PropertyReader<Object> readerObject = null;
        PropertyWrite<Object> writeObject = null;
        //得到readerObject,writeObject
        for (PropertyReader<Object> reader : this.propertyReaderList)
            if (reader.getTargetClass().isAssignableFrom(fromObject.getClass()) == true) {
                readerObject = reader;
                break;
            }
        for (PropertyWrite<Object> write : this.propertyWriteList)
            if (write.getTargetClass().isAssignableFrom(toObject.getClass()) == true) {
                writeObject = write;
                break;
            }
        //
        if (readerObject == null || writeObject == null)
            throw new SupportException("Don't support Object " + fromObject + " or " + toObject);
        //copy
        int i = 0;
        for (String key : propertysMapping.keySet())
            try {
                String fromProp = key;
                String toProp = propertysMapping.get(key);
                //1.是否能读写
                if (readerObject.canReader(fromProp, fromObject) == false)
                    continue;
                Object newValue = readerObject.readProperty(fromProp, fromObject);
                if (this.nullValueCP == false)
                    continue;
                if (writeObject.canWrite(toProp, toObject, newValue) == false)
                    continue;
                //2.类型转换
                Class<?> toType = writeObject.getTargetClass();
                Convert<Object> userConv = null;
                for (Convert<Object> conv : convertList)
                    if (conv.checkConvert(toType) == true) {
                        userConv = conv;
                        break;
                    }
                //3.拷贝
                if (userConv != null) {
                    newValue = userConv.convert(newValue);
                    if (writeObject.writeProperty(toProp, toObject, newValue) == true)
                        i++;
                }
            } catch (Exception e) {/*不处理异常*/}
        return i;
    };
    /**
     * 读取目标属性值，该方法应当由子类实现。
     * @return 返回目标属性值，该方法应当由子类实现。
     */
    public Object readProperty(String propertyName, Object target) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        for (PropertyReader<Object> reader : this.propertyReaderList)
            if (reader.getTargetClass().isAssignableFrom(target.getClass()) == true)
                if (reader.canReader(propertyName, target) == true)
                    return reader.readProperty(propertyName, target);
        return null;
    };
    /**
     * 读取目标属性值，该方法应当由子类实现。
     * @param propertyName 要写入的名称
     * @param target 要写入的目标bean
     * @param newValue 写入的新值
     * @return 返回目标属性值，该方法应当由子类实现。
     */
    public boolean writeProperty(String propertyName, Object target, Object newValue) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        for (PropertyWrite<Object> write : this.propertyWriteList)
            if (write.getTargetClass().isAssignableFrom(target.getClass()) == true)
                if (write.canWrite(propertyName, target, newValue) == true)
                    return write.writeProperty(propertyName, target, newValue);
        return false;
    }
    /*---------------------------------------------------------------------------------*/
    private static CopyBeanUtil defaultUtil = null;
    /**无论如何都创建一个新的CopyBeanUtil实例。*/
    public static CopyBeanUtil newInstance() {
        try {
            return new CopyBeanUtil() {};
        } catch (Exception e) {
            if (e instanceof InitializationException == true)
                throw (InitializationException) e;
            if (e instanceof RuntimeException == true)
                throw (RuntimeException) e;
            throw new InitializationException(e);
        }
    };
    /**获取一个CopyBeanUtil实例，该方法返回上一次调用该方法创建的实例对象。*/
    public static CopyBeanUtil getCopyBeanUtil() {
        if (defaultUtil == null)
            defaultUtil = newInstance();
        return defaultUtil;
    };
    public static int copyTo(Object fromObject, Object toObject) {
        return getCopyBeanUtil().copyPropertys(fromObject, toObject);//没有拷贝到东西.
    };
    public static int copyTo(Object fromObject, Object toObject, List<String> propertysNames) {
        return getCopyBeanUtil().copyPropertys(fromObject, toObject, propertysNames);
    };
    public static int copyTo(Object fromObject, Object toObject, Map<String, String> propertysMapping) {
        return getCopyBeanUtil().copyPropertys(fromObject, toObject, propertysMapping);
    };
    /**
     * 读取目标属性值，该方法应当由子类实现。
     * @return 返回目标属性值，该方法应当由子类实现。
     */
    public static Object getProperty(String propertyName, Object target) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        return getCopyBeanUtil().readProperty(propertyName, target);
    };
    /**
     * 读取目标属性值，该方法应当由子类实现。
     * @param propertyName 要写入的名称
     * @param target 要写入的目标bean
     * @param newValue 写入的新值
     * @return 返回目标属性值，该方法应当由子类实现。
     */
    public static boolean setProperty(String propertyName, Object target, Object newValue) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        return getCopyBeanUtil().writeProperty(propertyName, target, newValue);
    };
}