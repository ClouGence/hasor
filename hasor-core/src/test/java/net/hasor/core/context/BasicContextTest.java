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
package net.hasor.core.context;
import net.hasor.core.AppContext;
import net.hasor.core.AppContextWarp;
import net.hasor.core.Environment;
import net.hasor.core.environment.StandardEnvironment;
import net.hasor.core.spi.ContextInitializeListener;
import net.hasor.core.spi.ContextShutdownListener;
import net.hasor.core.spi.ContextStartListener;
import net.hasor.test.core.binder.TestBinder;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class BasicContextTest {
    private static boolean isWin() {
        // Runtime.getRuntime().exec("taskkill /pid "+pid+" /f");
        //Runtime.getRuntime().exec("kill -15 " + pid);
        String os = System.getProperty("os.name");
        return os.toLowerCase().startsWith("win".toLowerCase());
    }

    @Test
    public void waitSignal1() throws Throwable {
        Environment env = new StandardEnvironment();
        AppContext appContext = new AppContextWarp(new StatusAppContext(env));
        appContext.start();
        //
        final Object signal = new Object();
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(2000);
                synchronized (signal) {
                    signal.notifyAll();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        //
        long t = System.currentTimeMillis();
        appContext.waitSignal(signal, 5, TimeUnit.SECONDS);
        assert System.currentTimeMillis() - t < 5000;
    }

    @Test
    public void waitSignal2() throws Throwable {
        if (isWin()) {
            return;
        }
        Environment env = new StandardEnvironment();
        AppContext appContext = new AppContextWarp(new StatusAppContext(env));
        appContext.start();
        //
        Object signal = new Object();
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(10000);
                synchronized (signal) {
                    signal.notifyAll();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        //
        long t = System.currentTimeMillis();
        appContext.waitSignal(signal, 5, TimeUnit.SECONDS);
        assert System.currentTimeMillis() - t >= 5000;
    }

    @Test
    public void joinTest1() throws Throwable {
        Environment env = new StandardEnvironment();
        AppContext appContext = new AppContextWarp(new StatusAppContext(env));
        appContext.start();
        //
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(2000);
                atomicBoolean.set(true);
                appContext.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        //
        appContext.join();
        assert atomicBoolean.get();
    }

    @Test
    public void contentSpiListenerTest1() throws Throwable {
        Environment env = new StandardEnvironment(null, "/net_hasor_core_context/binder_exter.xml");
        AppContext appContext = new AppContextWarp(new StatusAppContext(env));
        //
        AtomicInteger atomic1 = new AtomicInteger(0);
        AtomicInteger atomic2 = new AtomicInteger(0);
        AtomicInteger atomic3 = new AtomicInteger(0);
        //
        appContext.start(apiBinder -> {
            apiBinder.bindSpiListener(ContextInitializeListener.class, templateAppContext -> {
                atomic1.incrementAndGet();
            });
            apiBinder.bindSpiListener(ContextStartListener.class, new ContextStartListener() {
                @Override
                public void doStart(AppContext appContext) {
                    atomic2.incrementAndGet();
                }

                @Override
                public void doStartCompleted(AppContext appContext) {
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) { /**/ }
                    atomic2.incrementAndGet();
                }
            });
            apiBinder.bindSpiListener(ContextShutdownListener.class, new ContextShutdownListener() {
                @Override
                public void doShutdown(AppContext appContext) {
                    atomic3.incrementAndGet();
                }

                @Override
                public void doShutdownCompleted(AppContext appContext) {
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) { /**/ }
                    atomic3.incrementAndGet();
                }
            });
        });
        //
        assert atomic1.get() == 1;
        assert atomic2.get() == 2;  // 同步处理因此不会马上变成2
        assert atomic3.get() == 0;
        //
        Thread.sleep(200);
        assert atomic2.get() == 2;
        //
        appContext.shutdown();
        assert atomic1.get() == 1;
        assert atomic2.get() == 2;
        assert atomic3.get() == 2;// 同步处理因此会马上变成2
    }

    @Test
    public void test1() {
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.isStart()).thenReturn(true);
        PowerMockito.doAnswer(invocationOnMock -> {
            if (atomicInteger.get() == 0) {
                atomicInteger.set(1);
            } else {
                atomicInteger.set(2);
                throw new Exception();
            }
            return null;
        }).when(appContext).shutdown();
        //
        ShutdownHook hook = new ShutdownHook(appContext);
        //
        hook.run();
        assert atomicInteger.get() == 1;
        //
        hook.run();
        assert atomicInteger.get() == 2;
    }

    @Test
    public void test4() throws Throwable {
        Environment env = new StandardEnvironment(null, "/net_hasor_core_context/binder_exter.xml");
        AppContext appContext = new AppContextWarp(new StatusAppContext(env));
        appContext.start(apiBinder -> {
            apiBinder.tryCast(TestBinder.class).hello();
        });
        //
        String instance = appContext.getInstance(String.class);
        assert "hello Binder".equals(instance);
    }

    @Test
    public void test5() throws Throwable {
        Environment env = new StandardEnvironment(null);
        AppContext appContext = new AppContextWarp(new StatusAppContext(env));
        //
        appContext.start();
        try {
            appContext.start();
            assert false;
        } catch (Exception e) {
            assert e.getMessage().equals("the container is already started");
        }
        //
        appContext.shutdown();
        try {
            appContext.shutdown();
            assert false;
        } catch (Exception e) {
            assert e.getMessage().equals("the container is not started yet.");
        }
    }
}
