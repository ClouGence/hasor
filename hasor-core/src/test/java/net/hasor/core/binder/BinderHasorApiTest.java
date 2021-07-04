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
package net.hasor.core.binder;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.core.Hasor;
import net.hasor.utils.supplier.TypeSupplier;
import net.hasor.core.exts.aop.Aop;
import net.hasor.core.scope.SingletonScope;
import net.hasor.test.core.basic.pojo.*;
import net.hasor.test.core.scope.AnnoMyBean;
import net.hasor.test.core.scope.My;
import net.hasor.utils.ExceptionUtils;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

public class BinderHasorApiTest {
    @Test
    public void scopeTest1() {
        Hasor.create().build(apiBinder -> {
            try {
                apiBinder.bindScope(Aop.class, new SingletonScope());
                assert false;
            } catch (Exception e) {
                assert e.getMessage().endsWith(" is not javax.inject.Scope");
            }
        });
    }

    @Test
    public void scopeTest2() {
        SingletonScope myScope = new SingletonScope();
        AppContext appContext = Hasor.create().build(apiBinder -> {
            apiBinder.bindScope("my", myScope);
            apiBinder.bindType(PojoBean.class).idWith("abc").toScope("my");
        });
        //
        PojoBean pojoBean1 = appContext.getInstance(PojoBean.class);
        PojoBean pojoBean2 = appContext.getInstance(PojoBean.class);
        //
        assert pojoBean1 == pojoBean2;
        assert myScope.getSingletonData().size() == 1;
        assert myScope.getSingletonData().get("BIND-abc").get() == pojoBean1;
    }

    @Test
    public void scopeTest3() {
        SingletonScope myScope = new SingletonScope();
        AppContext appContext = Hasor.create().build(apiBinder -> {
            apiBinder.bindScope(My.class, myScope);
        });
        //
        AnnoMyBean pojoBean1 = appContext.getInstance(AnnoMyBean.class);
        AnnoMyBean pojoBean2 = appContext.getInstance(AnnoMyBean.class);
        //
        assert pojoBean1 == pojoBean2;
        assert myScope.getSingletonData().size() == 1;
        assert myScope.getSingletonData().get("TYPE-" + AnnoMyBean.class.getName()).get() == pojoBean1;
    }

    @Test
    public void scopeTest4() {
        AppContext appContext = Hasor.create().build(apiBinder -> {
            apiBinder.bindType(PojoBean.class).asEagerSingleton();
        });
        //
        PojoBean pojoBean1 = appContext.getInstance(PojoBean.class);
        PojoBean pojoBean2 = appContext.getInstance(PojoBean.class);
        assert pojoBean1 == pojoBean2;
    }

    @Test
    public void scopeTest5() {
        AppContext appContext1 = Hasor.create().build(apiBinder -> {
            apiBinder.bindType(SingletonSampleBean.class).asEagerPrototype();
        });
        SingletonSampleBean pojoBean1 = appContext1.getInstance(SingletonSampleBean.class);
        SingletonSampleBean pojoBean2 = appContext1.getInstance(SingletonSampleBean.class);
        assert pojoBean1 != pojoBean2;
        //
        AppContext appContext2 = Hasor.create().build();
        SingletonSampleBean pojoBean3 = appContext2.getInstance(SingletonSampleBean.class);
        SingletonSampleBean pojoBean4 = appContext2.getInstance(SingletonSampleBean.class);
        assert pojoBean3 == pojoBean4;
    }

    @Test
    public void providerTest1() {
        AppContext appContext = Hasor.create().build(apiBinder -> {
            apiBinder.bindType(PojoBean.class);
            apiBinder.bindType(PojoBeanRef.class)//
                    .injectValue("name", "providerTest1") //
                    .inject("pojoBean", apiBinder.getProvider(PojoBean.class));
            //
            try {
                apiBinder.getProvider(PojoBean.class).get();
                assert false;
            } catch (Exception e) {
                assert e.getMessage().equals("the current state is not ready.");
            }
        });
        //
        PojoBeanRef pojoBeanRef = appContext.getInstance(PojoBeanRef.class);
        //
        assert pojoBeanRef.getName().equals("providerTest1");
        assert pojoBeanRef.getPojoBean() != null;
    }

    @Test
    public void providerTest2() {
        AppContext appContext = Hasor.create().build(apiBinder -> {
            apiBinder.bindType(PojoBeanRef.class)//
                    .injectValue("name", "providerTest2") //
                    .inject("pojoBean", apiBinder.getProvider(PojoBean.class));
            //
            try {
                apiBinder.getProvider(PojoBean.class).get();
                assert false;
            } catch (Exception e) {
                assert e.getMessage().equals("the current state is not ready.");
            }
        });
        //
        PojoBeanRef pojoBeanRef = appContext.getInstance(PojoBeanRef.class);
        //
        assert pojoBeanRef.getName().equals("providerTest2");
        assert pojoBeanRef.getPojoBean() != null;
    }

    @Test
    public void providerTest3() {
        AppContext appContext = Hasor.create().build(apiBinder -> {
            BindInfo<PojoBean> bindInfo = apiBinder.bindType(PojoBean.class)//
                    .injectValue("uuid", "uuid")//
                    .toInfo();
            apiBinder.bindType(PojoBeanRef.class)//
                    .injectValue("name", "providerTest3") //
                    .inject("pojoBean", bindInfo);
            //
            //
            try {
                apiBinder.getProvider(bindInfo).get();
                assert false;
            } catch (Exception e) {
                assert e.getMessage().equals("the current state is not ready.");
            }
        });
        //
        PojoBeanRef pojoBeanRef = appContext.getInstance(PojoBeanRef.class);
        //
        assert pojoBeanRef.getName().equals("providerTest3");
        assert pojoBeanRef.getPojoBean() != null;
        assert pojoBeanRef.getPojoBean().getUuid().equals("uuid");
    }

    @Test
    public void typeSupplier1() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        TypeSupplier typeSupplier = new TypeSupplier() {
            @Override
            public <T> T get(Class<? extends T> targetType) {
                try {
                    atomicBoolean.set(true);
                    return targetType.newInstance();
                } catch (Exception e) {
                    throw ExceptionUtils.toRuntime(e);
                }
            }
        };
        AppContext appContext = Hasor.create().build(apiBinder -> {
            apiBinder.bindType(PojoBean.class).toTypeSupplier(typeSupplier);
        });
        //
        assert !atomicBoolean.get();
        PojoBean pojoBeanRef = appContext.getInstance(PojoBean.class);
        assert pojoBeanRef.getUuid() == null;
        assert atomicBoolean.get();
    }

    @Test
    public void typeSupplier2() {
        AtomicBoolean testBoolean = new AtomicBoolean(false);
        TypeSupplier typeSupplier = new TypeSupplier() {
            public <T> T get(Class<? extends T> targetType) {
                if (targetType == SampleFace.class) {
                    testBoolean.set(true);
                }
                return null;
            }
        };
        AppContext appContext = Hasor.create().build(apiBinder -> {
            apiBinder.bindType(SampleFace.class).idWith("faceA").toTypeSupplier(typeSupplier);
        });
        //
        assert !testBoolean.get();
        appContext.getInstance("faceA");
        assert testBoolean.get();
    }

    @Test
    public void typeSupplier3() {
        AtomicBoolean testBoolean = new AtomicBoolean(false);
        TypeSupplier typeSupplier = new TypeSupplier() {
            public <T> T get(Class<? extends T> targetType) {
                if (targetType == SampleBean.class) {
                    testBoolean.set(true);
                }
                return null;
            }
        };
        AppContext appContext = Hasor.create().build(apiBinder -> {
            apiBinder.bindType(SampleFace.class).to(SampleBean.class).toTypeSupplier(typeSupplier);
        });
        //
        assert !testBoolean.get();
        appContext.getInstance(SampleFace.class);
        assert testBoolean.get();
    }
}

