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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
/**
 * 执行属性拷贝的类，该类扩充了PropertyReaderWrite类的功能增加了对属性拷贝到另外一个
 * PropertyReaderWrite对象的方法。使属性拷贝成为可能。其子类决定是如何进行属性拷贝的。
 * @version 2009-5-15
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class Copy extends PropertyReaderWrite<Object> implements Serializable, Cloneable {
    /**  */
    private static final long serialVersionUID = -4040330892099587195L;
    /** 该字段是存放执行当前拷贝的CopyBeanUtil对象 */
    protected CopyBeanUtil    copyBeanUtil     = null;
    /**
     * 设置执行当前拷贝的CopyBeanUtil对象
     * @param copyBeanUtil 执行当前拷贝的CopyBeanUtil对象
     */
    void setCopyBeanUtil(CopyBeanUtil copyBeanUtil) {
        this.copyBeanUtil = copyBeanUtil;
    }
    /** Bean在拷贝过程中所支持的类型转换。 */
    private Collection<ConvertType>     convertType = null;
    /** 属性读写器，属性拷贝使用该属性的值拷贝到其他AbstractReaderWrite中的 */
    private PropertyReaderWrite<Object> rw          = null;
    /**
     * 设置拷贝过程中所支持的类型转换集合。
     * @param convertType 拷贝过程中所支持的类型转换集合。
     */
    void setConvertType(Collection<ConvertType> convertType) {
        this.convertType = convertType;
    }
    /**
     * 获得某一个属性类别可支持的转换类别。
     * @param from 从什么类型
     * @param to 转换到什么类型
     * @return 返回某一个属性类别可支持的转换类别。
     */
    protected ConvertType getConvertType(Class<?> from, Class<?> to) {
        for (ConvertType type : this.convertType)
            if (type.checkType(from, to) == true)
                return type;
        return null;
    }
    /**
     * 拷贝属性到目标AbstractReaderWrite属性读写器中。如果拷贝成功则返回true否则返回fale。
     * 该类的子类决定如何进行拷贝。
     * @param toObject 准备拷贝的目标对象。
     * @return 如果拷贝成功则返回true否则返回fale。
     */
    public abstract boolean copyTo(PropertyReaderWrite<Object> toObject);
    protected Object clone() throws CloneNotSupportedException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            oos.flush();
            //
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            return ois.readObject();
        } catch (Exception e) {
            throw new CloneNotSupportedException("在克隆时发生异常 msg=" + e.getMessage());
        }
    }
    public boolean canReader() {
        return this.rw.canReader();
    }
    public boolean canWrite() {
        return this.rw.canWrite();
    }
    public Object get() {
        return this.rw.get();
    }
    public String getName() {
        return this.rw.getName();
    }
    public Object getObject() {
        return this.rw.getObject();
    }
    public void set(Object value) {
        this.rw.set(value);
    }
    public void setName(String name) {
        this.rw.setName(name);
    }
    public void setObject(Object object) {
        this.rw.setObject(object);
    }
    public PropertyReaderWrite<Object> getRw() {
        return rw;
    }
    public void setRw(PropertyReaderWrite<Object> rw) {
        this.rw = rw;
    }
    public Class<?> getPropertyClass() {
        return this.rw.getPropertyClass();
    }
}