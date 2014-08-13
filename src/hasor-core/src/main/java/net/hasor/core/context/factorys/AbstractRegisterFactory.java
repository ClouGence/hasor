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
import net.hasor.core.Provider;
import net.hasor.core.RegisterInfo;
import net.hasor.core.binder.RegisterInfoBuilder;
import net.hasor.core.context.AbstractAppContext;
import net.hasor.core.context.adapter.RegisterFactory;
import net.hasor.core.context.listener.ContextInitializeListener;
import net.hasor.core.context.listener.ContextStartListener;
import org.more.RepeateException;
import org.more.util.BeanUtils;
import org.more.util.Iterators;
import org.more.util.StringUtils;
/**
 * RegisterFactory接口的默认实现，包含了一些检查过程。
 * @version : 2014-5-10
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractRegisterFactory implements RegisterFactory, ContextInitializeListener, ContextStartListener {
    private Map<Class<?>, List<AbstractRegisterInfoAdapter<?>>> registerDataSource = new HashMap<Class<?>, List<AbstractRegisterInfoAdapter<?>>>();
    //
    @Override
    public <T> AbstractRegisterInfoAdapter<T> createTypeBuilder(final Class<T> bindType) {
        List<AbstractRegisterInfoAdapter<?>> registerList = this.registerDataSource.get(bindType);
        if (registerList == null) {
            registerList = new ArrayList<AbstractRegisterInfoAdapter<?>>();
            this.registerDataSource.put(bindType, registerList);
        }
        AbstractRegisterInfoAdapter<T> adapter = this.createRegisterInfoAdapter(bindType);
        adapter.setFactory(this);
        adapter.setBindType(bindType);
        registerList.add(adapter);
        return adapter;
    }
    //
    /**为类型创建AbstractRegisterInfoAdapter适配器。*/
    protected abstract <T> AbstractRegisterInfoAdapter<T> createRegisterInfoAdapter(Class<T> bindType);
    //
    @Override
    public <T> T getDefaultInstance(final Class<T> oriType) {
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
            return null;
        }
    }
    //
    @Override
    public final <T> T getInstance(final RegisterInfo<T> oriType) {
        if (oriType instanceof AbstractRegisterInfoAdapter) {
            AbstractRegisterInfoAdapter<T> adapter = (AbstractRegisterInfoAdapter<T>) oriType;
            Provider<T> provider = adapter.getCustomerProvider();
            if (provider != null) {
                return provider.get();
            }
        }
        return this.newInstance(oriType);
    };
    //
    protected abstract <T> T newInstance(RegisterInfo<T> oriType);
    //
    @Override
    public <T> Iterator<? extends RegisterInfoBuilder<T>> getRegisterIterator(final Class<T> bindType) {
        //原则1：避免对迭代器进行迭代，以减少时间复杂度。
        //
        List<AbstractRegisterInfoAdapter<?>> bindingTypeAdapterList = this.registerDataSource.get(bindType);
        if (bindingTypeAdapterList == null) {
            return new ArrayList<RegisterInfoBuilder<T>>(0).iterator();
        }
        Iterator<AbstractRegisterInfoAdapter<?>> iterator = bindingTypeAdapterList.iterator();
        /*迭代器类型转换*/
        return Iterators.converIterator(iterator, new Iterators.Converter<AbstractRegisterInfoAdapter<?>, RegisterInfoBuilder<T>>() {
            @Override
            public RegisterInfoBuilder<T> converter(final AbstractRegisterInfoAdapter<?> target) {
                return (RegisterInfoBuilder<T>) target;
            }
        });
    }
    @Override
    public Iterator<AbstractRegisterInfoAdapter<?>> getRegisterIterator() {
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
        final Collection<List<AbstractRegisterInfoAdapter<?>>> adapterList = this.registerDataSource.values();
        final Iterator<List<AbstractRegisterInfoAdapter<?>>> entIterator = adapterList.iterator();
        return new Iterator<AbstractRegisterInfoAdapter<?>>() {
            private Iterator<AbstractRegisterInfoAdapter<?>> regIterator = new ArrayList<AbstractRegisterInfoAdapter<?>>(0).iterator();
            @Override
            public AbstractRegisterInfoAdapter<?> next() {
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
            @Override
            public boolean hasNext() {
                if (entIterator.hasNext() == false && this.regIterator.hasNext() == false) {
                    return false;
                }
                return true;
            }
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    //
    /*测试register是否为匿名的*/
    private boolean ifAnonymity(final RegisterInfo<?> register) {
        return StringUtils.isBlank(register.getBindName());
    }
    @Override
    public void doInitialize(final ApiBinder apiBinder) {
        // TODO Auto-generated method stub
    }
    @Override
    public void doInitializeCompleted(final AbstractAppContext appContext) {
        //check begin
        Set<Class<?>> anonymityTypes = new HashSet<Class<?>>();
        Map<Class<?>, Set<String>> checkMap = new HashMap<Class<?>, Set<String>>();
        for (Entry<Class<?>, List<AbstractRegisterInfoAdapter<?>>> ent : this.registerDataSource.entrySet()) {
            Class<?> nowType = ent.getKey();
            Set<String> nowSet = checkMap.get(nowType);
            List<AbstractRegisterInfoAdapter<?>> nowList = ent.getValue();
            //
            if (nowSet == null) {
                nowSet = new HashSet<String>();
                checkMap.put(nowType, nowSet);
            }
            //
            for (AbstractRegisterInfoAdapter<?> e : nowList) {
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
    @Override
    public void doStart(final AbstractAppContext appContext) {
        // TODO Auto-generated method stub
    }
    @Override
    public void doStartCompleted(final AbstractAppContext appContext) {
        // TODO Auto-generated method stub
    }
}