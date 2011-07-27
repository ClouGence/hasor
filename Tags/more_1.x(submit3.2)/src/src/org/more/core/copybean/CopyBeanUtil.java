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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.more.core.io.AutoCloseInputStream;
import org.more.core.log.ILog;
import org.more.core.log.LogFactory;
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
public final class CopyBeanUtil implements Serializable, Cloneable {
    /**  */
    private static final long             serialVersionUID = 2607862244142020076L;
    /** 输出日志 */
    private transient static ILog         log              = LogFactory.getLog("org_more_core_copybean");
    /** Bean拷贝工具原形 */
    private transient static CopyBeanUtil utilPrototype    = null;
    //=======================================================================================
    /** Bean拷贝工具所支持的Bean类型。 */
    private LinkedList<BeanType>          typeList         = new LinkedList<BeanType>();
    /** Bean拷贝工具所支持的Bean类型。 */
    private Map<String, Copy>             copyList         = new Hashtable<String, Copy>();
    /** Bean在拷贝过程中所支持的类型转换。 */
    private LinkedList<ConvertType>       convertType      = new LinkedList<ConvertType>();
    /** 默认的Bean拷贝类型 */
    private Copy                          defaultCopy      = null;
    /** 私有化 */
    private CopyBeanUtil() {}
    /** 该代码快用于初始化，默认Bean类型。 */
    static {
        CopyBeanUtil.log.debug("BeginInit CopyBeanUtil file = config.properties");
        try {
            CopyBeanUtil utilPrototype = new CopyBeanUtil();
            //获得存放支持Bean类型的属性文件
            //config.properties属性文件属性排列顺序是最上面的在列表最后面
            InputStream is_1 = CopyBeanUtil.class.getResourceAsStream("/org/more/core/copybean/config.properties");
            Properties pro_1 = new Properties();
            pro_1.load(new AutoCloseInputStream(is_1));//装载属性文件
            //
            for (Object n : pro_1.keySet()) {
                String key = n.toString();
                String value = pro_1.get(n).toString();
                CopyBeanUtil.log.debug("see config.properties key=" + key + " value=" + value);
                //
                if (Pattern.matches(" *defaultCopy *", key) == true) {
                    //初始化defaultCopy
                    Class<?> cls = Class.forName(value);
                    utilPrototype.defaultCopy = (Copy) cls.newInstance();
                    utilPrototype.defaultCopy.setConvertType(utilPrototype.convertType);//注入类型转换支持集合
                } else if (Pattern.matches(" *rw\\..*", key) == true) {
                    //初始化typeList
                    Class<?> cls = Class.forName(value);
                    BeanType obj = (BeanType) cls.newInstance();
                    utilPrototype.typeList.addLast(obj);
                } else if (Pattern.matches(" *copy\\..*", key) == true) {
                    Matcher ma = Pattern.compile(" *copy\\.(.*)").matcher(key);
                    ma.find();
                    //初始化copyList
                    Class<?> cls = Class.forName(value);
                    Copy obj = (Copy) cls.newInstance();
                    obj.setConvertType(utilPrototype.convertType);//注入类型转换支持集合
                    utilPrototype.copyList.put(ma.group(1), obj);
                } else if (Pattern.matches(" *type\\..*", key) == true) {
                    //所支持的类型 初始化copyList
                    Class<?> cls = Class.forName(value);
                    ConvertType obj = (ConvertType) cls.newInstance();
                    utilPrototype.convertType.addLast(obj);
                }
            }
            //设置默认拷贝对象
            CopyBeanUtil.utilPrototype = utilPrototype;
            CopyBeanUtil.log.debug("EndInit CopyBeanUtil file = config.properties");
        } catch (Exception e) {
            CopyBeanUtil.log.debug("InitError config.properties message=" + e.getMessage());
        }
    }
    /**
     * 注册一个新的Bean拷贝所支持的类型，新注册的Bean类型要比系统内置的优先级高。通过该方法注册的类型将在以后所有CopyBeanUtil实例中得到支持。
     * @param type 新的Bean类型
     */
    public static void regeditStaticType(BeanType type) {
        CopyBeanUtil.utilPrototype.typeList.addFirst(type);
    }
    /**
     * 获得某个类型转换对象。还方法获得的ConvertType对象是CopyBeanUtil的原形中的数据。修改该对象将会影响以后的所有CopyBeanUtil实例。
     * @param typeClass 获得预获得转换格式。
     * @return 返回某个类型转换对象。
     */
    public static ConvertType getStaticConvertType(Class<? extends ConvertType> typeClass) {
        for (ConvertType type : CopyBeanUtil.utilPrototype.convertType)
            if (typeClass.isInstance(type) == true)
                return type;
        return null;
    }
    /**
     * 通过克隆方式创建一个新的CopyBeanUtil对象进行Bean拷贝。如果克隆失败则返回null。
     * @return 返回克隆方式创建一个新的CopyBeanUtil对象进行Bean拷贝。
     */
    public static CopyBeanUtil newInstance() {
        try {
            return (CopyBeanUtil) CopyBeanUtil.utilPrototype.clone();
        } catch (Exception e) {
            return null;
        }
    }
    /**
     * 执行Bean拷贝，将object对象中的属性拷贝到to中。返回成功拷贝几个属性，如果返回-1表示拷贝不被支持，如果返回0表示没有属性被拷贝。
     * @param object 要拷贝的原对象。
     * @param to 要拷贝的目的对象。
     * @param mode 拷贝模式，该参数相当于调用CopyBeanUtil对象的changeDefaultCopy方法。
     * @return 返回成功拷贝几个属性，如果返回-1表示拷贝不被支持，如果返回0表示没有属性被拷贝。
     */
    public int copy(Object object, Object to, String... mode) {
        if (object == null || to == null)
            return -1;
        //
        if (mode != null && mode.length >= 1)
            this.changeDefaultCopy(mode[0]);
        //
        //来源Bean属性集合引用
        Map<String, PropertyReaderWrite> o_1 = null;
        BeanType o_2_type = null;
        //将拷贝和被拷贝的对象中获取出其需要拷贝的属性集合
        CopyBeanUtil.log.debug("copybean obj=" + object + " to=" + to + " begin find BeanType");
        for (BeanType type : this.typeList) {
            //检查来源Bean是否被支持
            if (type.checkObject(object) == true)
                if (o_1 == null)
                    o_1 = type.getPropertys(object);//from
            //检查拷贝目标Bean是否被支持
            if (type.checkObject(to) == true)
                if (o_2_type == null)
                    o_2_type = type; //to
            if (o_1 != null && o_2_type != null)
                break;
        }
        CopyBeanUtil.log.debug("copybean end find BeanType objType=" + o_1);
        //如果属性集合中有空则返回-1
        if (o_1 == null)
            return -1;
        //
        CopyBeanUtil.log.debug("copybean begin copy ...");
        int i = 0;
        for (String key : o_1.keySet()) {
            PropertyReaderWrite prw = o_2_type.getPropertyRW(to, key);
            Copy copy_1 = null;
            try {
                copy_1 = (Copy) this.defaultCopy.clone();
            } catch (CloneNotSupportedException e) {
                continue;
            }
            copy_1.setCopyBeanUtil(this);
            copy_1.setRw(o_1.get(key));
            //
            if (prw.canWrite() == true && copy_1.canReader() == true) {
                copy_1.copyTo(prw);
                i++;
                CopyBeanUtil.log.debug("copybean copy name=" + key);
            } else
                CopyBeanUtil.log.debug("copybean copy Ignore name=" + key);
        }
        CopyBeanUtil.log.debug("copybean end copy copy count =" + i);
        return i;
    }
    /**
     * 注册一个新的Bean拷贝所支持的类型，新注册的Bean类型要比系统内置的优先级高。通过该方法注册的类型只有当前实例中得到支持。
     * @param type 新的Bean类型
     */
    public void regeditType(BeanType type) {
        this.typeList.addFirst(type);
    }
    /**
     * 获得某个类型转换对象。
     * @param typeClass 获得预获得转换格式。
     * @return 返回某个类型转换对象。
     */
    public ConvertType getConvertType(Class<? extends ConvertType> typeClass) {
        for (ConvertType type : this.convertType)
            if (typeClass.isInstance(type) == true)
                return type;
        return null;
    }
    protected Object clone() throws CloneNotSupportedException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            oos.flush();
            //
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            CopyBeanUtil util = (CopyBeanUtil) ois.readObject();
            return util;
        } catch (Exception e) {
            throw new CloneNotSupportedException("在克隆时发生异常 msg=" + e.getMessage());
        }
    }
    /**
     * 注册一个新的Bean拷贝，该拷贝对象决定了如何进行拷贝，由于系统默认使用的是浅拷贝，因此深拷贝需要用户自行实现。
     * 该方法可以使用户参与拷贝过程，并得到扩展。当注册新的拷贝对象之后还需要改变当前拷贝对象到注册的新copy才可以生效。
     * 修改该对象将会影响以后的所有CopyBeanUtil实例。
     * @param copy 新的拷贝类型
     */
    public static void regeditStaticCopy(Copy copy) {
        CopyBeanUtil.utilPrototype.copyList.put(copy.getName(), copy);
    }
    /**
     * 获取CopyBeanUtil的静态拷贝对象。通过该方法可以设置默认拷贝对象的一些属性。这个方法对自定义拷贝对象的设置是很有帮助的。
     * 注意修改该对象将会影响以后的所有CopyBeanUtil实例。
     * @return 返回CopyBeanUtil的静态默认拷贝对象。
     */
    public static Copy getStaticDefaultCopy() {
        return CopyBeanUtil.utilPrototype.defaultCopy;
    }
    /**
     * 改变当前默认拷贝对象为指定名称的拷贝对象。参数是拷贝对象名。该名称可以通过copy.getName()方式获得。
     * 通过该方法改变的默认拷贝对象只有当前实例中得到支持。注意修改该对象将会影响以后的所有CopyBeanUtil实例。
     * 如果要改变的目标拷贝对象不存在则忽略该拷贝。
     * @param copyName 改变的目标拷贝对象名。该名称可以通过copy.getName()方式获得。
     */
    public static void changeStaticDefaultCopy(String copyName) {
        CopyBeanUtil.utilPrototype.changeDefaultCopy(copyName);
    }
    /**
     * 注册一个新的Bean拷贝，该拷贝对象决定了如何进行拷贝，由于系统默认使用的是浅拷贝，因此深拷贝需要用户自行实现。
     * 该方法可以使用户参与拷贝过程，并得到扩展。当注册新的拷贝对象之后还需要改变当前拷贝对象到注册的新copy才可以生效。
     * 通过该方法注册的拷贝对象只有当前实例中得到支持。
     * @param copy 新的拷贝类型
     */
    public void regeditCopy(Copy copy) {
        if (this.copyList.containsKey(copy.getName()) == false)
            this.copyList.put(copy.getName(), copy);
    }
    /**
     * 获取当前的默认拷贝对象。通过该方法可以设置默认拷贝对象的一些属性。这个方法对自定义拷贝对象的设置是很有帮助的。
     * @return 返回当前的默认拷贝对象。
     */
    public Copy getDefaultCopy() {
        return defaultCopy;
    }
    /**
     * 改变当前默认拷贝对象为指定名称的拷贝对象。参数是拷贝对象名。该名称可以通过copy.getName()方式获得。
     * 通过该方法改变的默认拷贝对象只有当前实例中得到支持。如果要改变的目标拷贝对象不存在则忽略该拷贝。
     * @param copyName 改变的目标拷贝对象名。该名称可以通过copy.getName()方式获得。
     */
    public void changeDefaultCopy(String copyName) {
        if (this.copyList.containsKey(copyName) == true)
            this.defaultCopy = this.copyList.get(copyName);
    }
}