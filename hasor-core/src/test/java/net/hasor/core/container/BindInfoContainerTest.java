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
package net.hasor.core.container;
import net.hasor.core.binder.BindInfoBuilder;
import net.hasor.core.info.DefaultBindInfoProviderAdapter;
import net.hasor.test.core.basic.pojo.PojoBean;
import net.hasor.test.core.basic.pojo.SampleBean;
import net.hasor.test.core.basic.pojo.SampleFace;
import net.hasor.utils.BeanUtils;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class BindInfoContainerTest {
    @Test
    public void infoTest1() {
        SpiCallerContainer spiCallerContainer = new SpiCallerContainer();
        BindInfoContainer infoContainer = new BindInfoContainer(spiCallerContainer);
        //
        String beanID = "abc";
        assert infoContainer.findBindInfo(beanID) == null;
        BindInfoBuilder<PojoBean> beanBuilder = infoContainer.createInfoAdapter(PojoBean.class);
        beanBuilder.setBindID(beanID);
        assert infoContainer.findBindInfo(beanID) != null;
        assert infoContainer.findBindInfo(beanID).getBindType().equals(PojoBean.class);
    }

    @Test
    public void infoTest2() {
        SpiCallerContainer spiCallerContainer = new SpiCallerContainer();
        BindInfoContainer infoContainer = new BindInfoContainer(spiCallerContainer);
        //
        assert infoContainer.findBindInfoList(List.class).isEmpty();
        assert infoContainer.findBindInfoList(PojoBean.class).isEmpty();
        //
        infoContainer.createInfoAdapter(PojoBean.class);
        //
        assert infoContainer.findBindInfoList(List.class).isEmpty();
        assert infoContainer.findBindInfoList(PojoBean.class).size() == 1;
    }

    @Test
    public void infoTest3() {
        SpiCallerContainer spiCallerContainer = new SpiCallerContainer();
        BindInfoContainer infoContainer = new BindInfoContainer(spiCallerContainer);
        String beanName1 = "abc_1";
        String beanName2 = "abc_2";
        //
        assert infoContainer.findBindInfo(beanName1, PojoBean.class) == null;
        assert infoContainer.findBindInfo(beanName2, PojoBean.class) == null;
        assert infoContainer.findBindInfo(null, PojoBean.class) == null;
        //
        BindInfoBuilder<PojoBean> beanBuilder1 = infoContainer.createInfoAdapter(PojoBean.class);
        beanBuilder1.setBindName(beanName1);
        BindInfoBuilder<PojoBean> beanBuilder2 = infoContainer.createInfoAdapter(PojoBean.class);
        beanBuilder2.setBindName(beanName2);
        BindInfoBuilder<PojoBean> beanBuilder3 = infoContainer.createInfoAdapter(PojoBean.class);
        //
        assert infoContainer.findBindInfo(beanName1, PojoBean.class) == beanBuilder1;
        assert infoContainer.findBindInfo(beanName2, PojoBean.class) == beanBuilder2;
        assert infoContainer.findBindInfo(null, PojoBean.class) == beanBuilder3;
    }

    @Test
    public void infoTest4() {
        SpiCallerContainer spiCallerContainer = new SpiCallerContainer();
        BindInfoContainer infoContainer = new BindInfoContainer(spiCallerContainer);
        //
        ArrayList<Object> objs1 = new ArrayList<>();
        infoContainer.forEach(objs1::add);
        assert objs1.isEmpty();
        //
        infoContainer.createInfoAdapter(PojoBean.class);
        //
        ArrayList<Object> objs2 = new ArrayList<>();
        infoContainer.forEach(objs2::add);
        assert objs2.size() == 1;
    }

    @Test
    public void infoTest5() {
        SpiCallerContainer spiCallerContainer = new SpiCallerContainer();
        BindInfoContainer infoContainer = new BindInfoContainer(spiCallerContainer);
        //
        infoContainer.createInfoAdapter(PojoBean.class);
        infoContainer.init();
        //
        try {
            infoContainer.createInfoAdapter(PojoBean.class);
            assert false;
        } catch (Exception e) {
            assert e.getMessage().equals("container has been started.");
        }
        //
        infoContainer.close();
    }

    @Test
    public void infoTest6() {
        SpiCallerContainer spiCallerContainer = new SpiCallerContainer();
        BindInfoContainer infoContainer = new BindInfoContainer(spiCallerContainer);
        //
        infoContainer.createInfoAdapter(PojoBean.class);
        infoContainer.createInfoAdapter(PojoBean.class);
        //
        try {
            infoContainer.init();
            assert false;
        } catch (Exception e) {
            assert e.getMessage().equals("conflict type '" + PojoBean.class.getName() + "' of same name ''");
        }
    }

    @Test
    public void infoConflictTest1() {
        SpiCallerContainer spiCallerContainer = new SpiCallerContainer();
        BindInfoContainer infoContainer = new BindInfoContainer(spiCallerContainer);
        //
        infoContainer.createInfoAdapter(PojoBean.class).setBindName("abc_1");
        //
        try {
            infoContainer.createInfoAdapter(PojoBean.class).setBindName("abc_1");
            assert false;
        } catch (Exception e) {
            assert e.getMessage().startsWith("duplicate bind -> bindName '");
        }
    }

    @Test
    public void infoConflictTest2() {
        SpiCallerContainer spiCallerContainer = new SpiCallerContainer();
        BindInfoContainer infoContainer = new BindInfoContainer(spiCallerContainer);
        //
        infoContainer.createInfoAdapter(PojoBean.class).setBindID("abc_1");
        //
        try {
            infoContainer.createInfoAdapter(PojoBean.class).setBindID("abc_1");
            assert false;
        } catch (Exception e) {
            assert e.getMessage().startsWith("duplicate bind -> id value is ");
        }
    }

    @Test
    public void infoConflictTest3() {
        SpiCallerContainer spiCallerContainer = new SpiCallerContainer();
        BindInfoContainer infoContainer = new BindInfoContainer(spiCallerContainer);
        //
        try {
            // 因为原始值和新值相同因此不会引发异常
            infoContainer.createInfoAdapter(PojoBean.class).setBindType(PojoBean.class);
            assert true;
        } catch (Exception e) {
            assert false;
        }
        //
        try {
            // 反射的方式设置一个新值，引发异常；
            DefaultBindInfoProviderAdapter<SampleFace> adapter = infoContainer.createInfoAdapter(SampleFace.class);
            Method writeMethod = BeanUtils.getWriteMethod("bindType", DefaultBindInfoProviderAdapter.class);
            writeMethod.invoke(adapter, SampleBean.class);
            assert false;
        } catch (Exception e) {
            if (e instanceof InvocationTargetException == false) {
                e.printStackTrace();
                assert false;
            }
            assert ((InvocationTargetException) e).getTargetException().getMessage().equals("'bindType' are not allowed to be changed");
        }
    }
}