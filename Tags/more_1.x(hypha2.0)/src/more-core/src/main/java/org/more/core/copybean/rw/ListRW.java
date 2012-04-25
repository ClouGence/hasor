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
package org.more.core.copybean.rw;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.more.core.copybean.PropertyReader;
import org.more.core.copybean.PropertyWrite;
import org.more.util.BeanUtil;
import org.more.util.StringConvertUtil;
/**
 * List类读写器。使用该类作为读写器可以实现从List对象中拷贝属性或者向List中拷贝属性。<br/>
 * @see {@link #writeProperty(String, List, Object)}<br/>
 * @see {@link #readProperty(String, List)}<br/>
 * @version : 2011-12-24
 * @author 赵永春 (zyc@byshell.org)
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ListRW implements PropertyReader<List>, PropertyWrite<List> {
    public List<String> getPropertyNames(List target) {
        ArrayList<String> names = new ArrayList<String>();
        for (int i = 0; i < names.size(); i++)
            names.add(String.valueOf(i));
        return names;
    }
    public boolean canWrite(String propertyName, List target, Object newValue) {
        return true;
    }
    public boolean canReader(String propertyName, List target) {
        return true;
    }
    /**
     * 在向List执行写入操作时，如果指定了propertyName属性（数字）那么在添加的时候会写入到指定的位置上，
     * 但是如果写入的位置超出范围会引发{@link IndexOutOfBoundsException}异常。
     * Integer.MAX_VALUE值会导致追加操作。Integer.MIN_VALUE值会导致在首条前插入操作。
     * 如果没有指定propertyName属性（数字），则写入操作会将newValue值添加到list中。此时如果newValue是一个集合类型或者数组那么将会执行批量添加。
     */
    public boolean writeProperty(String propertyName, List target, Object newValue) throws IndexOutOfBoundsException {
        if (newValue == null)
            return false;
        if (BeanUtil.isNone(propertyName) == true) {
            if (newValue.getClass().isArray()) {
                //数组操作
                Object[] objs = (Object[]) newValue;
                for (Object obj : objs)
                    target.add(obj);
            } else if (newValue instanceof Collection)
                //集合操作 
                target.addAll((Collection) newValue);
            else
                target.add(newValue);
            return true;
        }
        /**这里不使用负数表示是因为propertyName在转换成int之后可能是一个负数。*/
        int setIndex = StringConvertUtil.parseInt(propertyName, Integer.MAX_VALUE);
        if (setIndex < 0)
            throw new IndexOutOfBoundsException();//超出可以设置的范围，小于
        if (setIndex != Integer.MAX_VALUE && setIndex >= target.size())
            throw new IndexOutOfBoundsException();//超出可以设置的范围，大于
        //加入操作
        if (setIndex == Integer.MAX_VALUE)
            target.add(newValue);//追加
        if (setIndex == Integer.MIN_VALUE)
            //这里没有传入Integer.MIN_VALUE是因为其值有可能是一个负数
            target.add(0, newValue);//插入
        else
            target.add(setIndex, newValue);//添家
        return true;
    }
    /**
     * 在向List执行读取操作时，如果指定了propertyName属性（数字）那么会返回指定位置上的值，
     * 但是如果读取的位置超出范围会引发{@link IndexOutOfBoundsException}异常。propertyName为空回返回list本身。
     * Integer.MAX_VALUE值会读取首条。Integer.MIN_VALUE值会读取尾条。
     */
    public Object readProperty(String propertyName, List target) {
        if (BeanUtil.isNone(propertyName) == true)
            return target;
        //
        /**这里不使用负数表示是因为propertyName在转换成int之后可能是一个负数。*/
        int getIndex = StringConvertUtil.parseInt(propertyName, Integer.MAX_VALUE);
        if (getIndex < 0)
            throw new IndexOutOfBoundsException();//超出可以设置的范围，小于
        if (getIndex != Integer.MAX_VALUE && getIndex >= target.size())
            throw new IndexOutOfBoundsException();//超出可以设置的范围，大于
        //读取操作
        if (target.size() == 0)
            return null;
        if (getIndex == Integer.MAX_VALUE)
            return target.get(target.size() - 1);//尾条
        else if (getIndex == Integer.MIN_VALUE)
            return target.get(0);//首条
        else
            return target.get(getIndex);//首条
    }
    /**支持对List的读写操作。*/
    public Class<?> getTargetClass() {
        return List.class;
    }
}