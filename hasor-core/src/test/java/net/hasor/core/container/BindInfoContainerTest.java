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
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
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
        BindInfoBuilder<PojoBean> beanBuilder = infoContainer.createInfoAdapter(PojoBean.class, null);
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
        infoContainer.createInfoAdapter(PojoBean.class, null);
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
        BindInfoBuilder<PojoBean> beanBuilder1 = infoContainer.createInfoAdapter(PojoBean.class, null);
        beanBuilder1.setBindName(beanName1);
        BindInfoBuilder<PojoBean> beanBuilder2 = infoContainer.createInfoAdapter(PojoBean.class, null);
        beanBuilder2.setBindName(beanName2);
        BindInfoBuilder<PojoBean> beanBuilder3 = infoContainer.createInfoAdapter(PojoBean.class, null);
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
        infoContainer.createInfoAdapter(PojoBean.class, null);
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
        infoContainer.createInfoAdapter(PojoBean.class, null);
        infoContainer.init();
        //
        try {
            infoContainer.createInfoAdapter(PojoBean.class, null);
            assert false;
        } catch (Exception e) {
            assert e.getMessage().equals("container has been started.");
        }
        //
        infoContainer.close();
    }

    @Test
    public void infoTest6() {
        try {
            Hasor.create().build(apiBinder -> {
                apiBinder.bindType(PojoBean.class).nameWith("");
                apiBinder.bindType(PojoBean.class).nameWith("");
            });
            assert false;
        } catch (Exception e) {
            assert e.getMessage().equals("duplicate bind -> bindName '' conflict with bindType='" + PojoBean.class.getName() + "', bindID='" + PojoBean.class.getName() + "'");
        }
    }

    @Test
    public void infoTest7() {
        PojoBean pojoBeanA = new PojoBean();
        PojoBean pojoBeanB = new PojoBean();
        AppContext appContext = Hasor.create().build(apiBinder -> {
            apiBinder.bindType(PojoBean.class).idWith("a").toInstance(pojoBeanA);
            apiBinder.bindType(PojoBean.class).idWith("b").toInstance(pojoBeanB);
        });
        //
        assert appContext.getInstance("a") == pojoBeanA;
        assert appContext.getInstance("b") == pojoBeanB;
    }

    @Test
    public void infoTest8() {
        try {
            Hasor.create().build(apiBinder -> {
                apiBinder.bindType(PojoBean.class).idWith("a").toInstance(new PojoBean());
                apiBinder.bindType(PojoBean.class).idWith("a").toInstance(new PojoBean());
            });
            assert false;
        } catch (Exception e) {
            assert e.getMessage().equals("duplicate bind -> id value is a");
        }
    }

    @Test
    public void infoTest9() {
        PojoBean pojoBeanA = new PojoBean();
        PojoBean pojoBeanB = new PojoBean();
        AppContext appContext = Hasor.create().build(apiBinder -> {
            apiBinder.bindType(PojoBean.class).nameWith("a").toInstance(pojoBeanA);
            apiBinder.bindType(PojoBean.class).nameWith("b").toInstance(pojoBeanB);
        });
        //
        assert appContext.findBindingBean("a", PojoBean.class) == pojoBeanA;
        assert appContext.findBindingBean("b", PojoBean.class) == pojoBeanB;
    }

    @Test
    public void infoTest10() {
        try {
            Hasor.create().build(apiBinder -> {
                apiBinder.bindType(PojoBean.class).nameWith("a").toInstance(new PojoBean());
                apiBinder.bindType(PojoBean.class).nameWith("a").toInstance(new PojoBean());
            });
            assert false;
        } catch (Exception e) {
            assert e.getMessage().startsWith("duplicate bind -> bindName 'a' conflict with bindType='net.hasor.test.core.basic.pojo.PojoBean', bindID='");
        }
    }

    @Test
    public void infoConflictTest1() {
        SpiCallerContainer spiCallerContainer = new SpiCallerContainer();
        BindInfoContainer infoContainer = new BindInfoContainer(spiCallerContainer);
        //
        infoContainer.createInfoAdapter(PojoBean.class, null).setBindName("abc_1");
        //
        try {
            infoContainer.createInfoAdapter(PojoBean.class, null).setBindName("abc_1");
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
        infoContainer.createInfoAdapter(PojoBean.class, null).setBindID("abc_1");
        //
        try {
            infoContainer.createInfoAdapter(PojoBean.class, null).setBindID("abc_1");
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
            infoContainer.createInfoAdapter(PojoBean.class, null).setBindType(PojoBean.class);
            assert true;
        } catch (Exception e) {
            assert false;
        }
        //
        try {
            // 反射的方式设置一个新值，引发异常；
            DefaultBindInfoProviderAdapter<SampleFace> adapter = infoContainer.createInfoAdapter(SampleFace.class, null);
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
