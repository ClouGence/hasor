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
package net.hasor.core.context.factorys;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import net.hasor.core.ApiBinder;
import net.hasor.core.BindInfo;
import net.hasor.core.BindInfoFactory;
import net.hasor.core.Provider;
import net.hasor.core.context.AbstractAppContext;
import net.hasor.core.context.listener.ContextInitializeListener;
import net.hasor.core.context.listener.ContextStartListener;
import org.more.RepeateException;
import org.more.util.ArrayUtils;
import org.more.util.BeanUtils;
import org.more.util.Iterators;
import org.more.util.StringUtils;
/**
 * RegisterFactory接口的默认实现，包含了一些检查过程。
 * @version : 2014-5-10
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractBindInfoFactory implements BindInfoFactory, ContextInitializeListener, ContextStartListener {
    private Map<Class<?>, List<AbstractBindInfoProviderAdapter<?>>> registerDataSource = new HashMap<Class<?>, List<AbstractBindInfoProviderAdapter<?>>>();
    //
    /**注册一个类型*/
    public <T> AbstractBindInfoProviderAdapter<T> createTypeBuilder(final Class<T> bindType) {
        List<AbstractBindInfoProviderAdapter<?>> registerList = this.registerDataSource.get(bindType);
        if (registerList == null) {
            registerList = new ArrayList<AbstractBindInfoProviderAdapter<?>>();
            this.registerDataSource.put(bindType, registerList);
        }
        AbstractBindInfoProviderAdapter<T> adapter = this.createRegisterInfoAdapter(bindType);
        adapter.setFactory(this);
        adapter.setBindType(bindType);
        registerList.add(adapter);
        return adapter;
    }
    //
    /**为类型创建AbstractRegisterInfoAdapter适配器。*/
    protected <T> AbstractBindInfoProviderAdapter<T> createRegisterInfoAdapter(Class<T> bindingType) {
        return new DefaultBindInfoProviderAdapter<T>(bindingType);
    }
    //
    /**创建一个未绑定过的类型*/
    public <T> T getDefaultInstance(final Class<T> oriType) {
        if (oriType == null) {
            return null;
        }
        try {
            if (oriType.isInterface() || oriType.isEnum()) {
                return null;
            }
            if (oriType.isPrimitive()) {
                return (T) BeanUtils.getDefaultValue(oriType);
            }
            if (oriType.isArray()) {
                Class<?> comType = oriType.getComponentType();
                return (T) Array.newInstance(comType, 0);
            }
            return oriType.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**创建{@link RegisterInfo} 所表示的类型对象。*/
    public final <T> T getInstance(final BindInfo<T> oriType) {
        if (oriType instanceof AbstractBindInfoProviderAdapter) {
            AbstractBindInfoProviderAdapter<T> adapter = (AbstractBindInfoProviderAdapter<T>) oriType;
            Provider<T> provider = adapter.getCustomerProvider();
            if (provider != null) {
                return provider.get();
            }
        }
        return this.newInstance(oriType);
    };
    //
    /**创建 {@link RegisterInfo}所表示的那个类型。
     * @see #getInstance(RegisterInfo)*/
    protected abstract <T> T newInstance(BindInfo<T> bindInfo);
    //
    public String[] getNamesOfType(Class<?> bindType) {
        List<AbstractBindInfoProviderAdapter<?>> adapterList = this.registerDataSource.get(bindType);
        if (adapterList == null || adapterList.isEmpty()) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        List<String> names = new ArrayList<String>();
        for (AbstractBindInfoProviderAdapter<?> adapter : adapterList) {
            String name = adapter.getBindName();
            if (StringUtils.isBlank(name) == false) {
                names.add(name);
            }
        }
        return names.toArray(new String[names.size()]);
    }
    public <T> BindInfo<T> getRegister(String withName, Class<T> bindType) {
        List<AbstractBindInfoProviderAdapter<?>> adapterList = this.registerDataSource.get(bindType);
        if (adapterList == null || adapterList.isEmpty()) {
            return null;
        }
        for (AbstractBindInfoProviderAdapter<?> adapter : adapterList) {
            boolean eq = StringUtils.equals(adapter.getBindName(), withName);
            if (eq == true) {
                return (BindInfo<T>) adapter;
            }
        }
        return null;
    }
    /*测试register是否为匿名的*/
    private boolean ifAnonymity(final BindInfo<?> register) {
        return StringUtils.isBlank(register.getBindName());
    }
    //
    public void doInitialize(ApiBinder apiBinder) {
        // TODO Auto-generated method stub
    }
    //
    public void doInitializeCompleted(final AbstractAppContext appContext) {
        //check begin
        Set<Class<?>> anonymityTypes = new HashSet<Class<?>>();
        Map<Class<?>, Set<String>> checkMap = new HashMap<Class<?>, Set<String>>();
        for (Entry<Class<?>, List<AbstractBindInfoProviderAdapter<?>>> ent : this.registerDataSource.entrySet()) {
            Class<?> nowType = ent.getKey();
            Set<String> nowSet = checkMap.get(nowType);
            List<AbstractBindInfoProviderAdapter<?>> nowList = ent.getValue();
            //
            if (nowSet == null) {
                nowSet = new HashSet<String>();
                checkMap.put(nowType, nowSet);
            }
            //
            for (AbstractBindInfoProviderAdapter<?> e : nowList) {
                //
                //1.负责检查重复的匿名绑定
                if (anonymityTypes.contains(nowType) == true && this.ifAnonymity(e) == true) {
                    throw new RepeateException(String.format("repeate anonymity bind , type is %s", nowType));
                }
                if (anonymityTypes.contains(nowType) == false) {
                    anonymityTypes.add(nowType);
                }
                //
                //2.同类型绑定的重名检查
                String name = e.getBindName();
                if (nowSet.contains(name) == true) {
                    throw new RepeateException(String.format("repeate name bind ,name = %s. type is %s", name, nowType));
                }
                nowSet.add(name);
            }
            //
        }
        //check end
    }
    public void doStart(final AbstractAppContext appContext) {
        // TODO Auto-generated method stub
    }
    public void doStartCompleted(final AbstractAppContext appContext) {
        // TODO Auto-generated method stub
    }
    //
    /*---------------------------------------------------------------------------------------Util*/
    /**根据Type查找RegisterInfo迭代器*/
    public <T> Iterator<? extends AbstractBindInfoProviderAdapter<T>> getBindInfoIterator(final Class<T> bindType) {
        //原则1：避免对迭代器进行迭代，以减少时间复杂度。
        //
        List<AbstractBindInfoProviderAdapter<?>> bindingTypeAdapterList = this.registerDataSource.get(bindType);
        if (bindingTypeAdapterList == null) {
            return new ArrayList<AbstractBindInfoProviderAdapter<T>>(0).iterator();
        }
        Iterator<AbstractBindInfoProviderAdapter<?>> iterator = bindingTypeAdapterList.iterator();
        /*迭代器类型转换*/
        return Iterators.converIterator(iterator, new Iterators.Converter<AbstractBindInfoProviderAdapter<?>, AbstractBindInfoProviderAdapter<T>>() {
            public AbstractBindInfoProviderAdapter<T> converter(final AbstractBindInfoProviderAdapter<?> target) {
                return (AbstractBindInfoProviderAdapter<T>) target;
            }
        });
    }
    /**查找所有RegisterInfo迭代器*/
    public Iterator<AbstractBindInfoProviderAdapter<?>> getBindInfoIterator() {
        //原则1：避免对迭代器进行迭代，以减少时间复杂度。
        /*
         * 代码相当于：
         * List<List<Bean>> dataList =..
         * for (List<Bean> subList : dataList){
         *   for (List<Bean> item : subList){
         *     ......
         *   }
         * }
         * 但是不同于双for的是，下面代码返回的迭代器只有在迭代的时候才会产生双for的作用。并且可以跟随迭代过程随时终止。
         */
        final Collection<List<AbstractBindInfoProviderAdapter<?>>> adapterList = this.registerDataSource.values();
        final Iterator<List<AbstractBindInfoProviderAdapter<?>>> entIterator = adapterList.iterator();
        return new Iterator<AbstractBindInfoProviderAdapter<?>>() {
            private Iterator<AbstractBindInfoProviderAdapter<?>> regIterator = new ArrayList<AbstractBindInfoProviderAdapter<?>>(0).iterator();
            public AbstractBindInfoProviderAdapter<?> next() {
                while (true) {
                    if (this.regIterator.hasNext() == false) {
                        /*1.当前List迭代完了，并且没有可迭代的List了 --> break */
                        if (entIterator.hasNext() == false) {
                            break;
                        }
                        /*2.当前List迭代完了，迭代下一个List*/
                        this.regIterator = entIterator.next().iterator();
                    }
                    /*一定要在判断一遍，否则很可能下一个迭代器没内容而抛出NoSuchElementException异常，而这个时候抛出这个异常是不适当的。*/
                    if (this.regIterator.hasNext()) {
                        /*3.当前迭代器有内容*/
                        break;
                    }
                }
                //
                if (this.regIterator.hasNext() == false) {
                    throw new NoSuchElementException();
                }
                return this.regIterator.next();
            }
            public boolean hasNext() {
                if (entIterator.hasNext() == false && this.regIterator.hasNext() == false) {
                    return false;
                }
                return true;
            }
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}