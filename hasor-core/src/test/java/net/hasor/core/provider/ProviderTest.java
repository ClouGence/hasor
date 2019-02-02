package net.hasor.core.provider;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.core.Provider;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
public class ProviderTest {
    //
    @Test
    public void providerTest0() throws Throwable {
        //
        AppContext mock = PowerMockito.mock(AppContext.class);
        PowerMockito.when(mock.getInstance(ArrayList.class)).thenReturn(new ArrayList());
        //
        ClassAwareProvider<List> provider = new ClassAwareProvider<List>(ArrayList.class);
        //
        try {
            provider.get();
            assert false;
        } catch (Exception e) {
            assert "has not been initialized".equalsIgnoreCase(e.getMessage());
        }
        //
        provider.setAppContext(mock);
        provider.toString();
        //
        assert provider.get() instanceof ArrayList;
    }
    //
    @Test
    public void providerTest1() {
        Provider<List> ofList = InstanceProvider.of(new ArrayList());
        ClassLoaderSingleProvider<List> provider = new ClassLoaderSingleProvider<List>(ofList);
        provider.toString();
        //
        assert provider.get() != null;
        assert provider.get() == provider.get();
        //
        ArrayList obj = new ArrayList();
        Provider<ArrayList> ofListWrap = InstanceProvider.wrap(obj);
        assert ofListWrap.get() == obj;
    }
    //
    @Test
    public void providerTest2() throws Throwable {
        //
        Provider<List> listProvider = InstanceProvider.of(new ArrayList());
        SingleProvider<List> singleProvider = new SingleProvider<List>(listProvider);
        singleProvider.toString();
        //
        assert singleProvider.get() == singleProvider.get();
    }
    //
    @Test
    public void providerTest3() throws Throwable {
        //
        InstanceProvider<List> listProvider = new InstanceProvider<List>(new ArrayList());
        assert listProvider.get() instanceof ArrayList;
        //
        listProvider.set(new LinkedList());
        assert listProvider.get() instanceof LinkedList;
    }
    //
    @Test
    public void providerTest4() throws Throwable {
        BindInfo<List> info = PowerMockito.mock(BindInfo.class);
        PowerMockito.when(info.getBindID()).thenReturn("TEST");
        //
        AppContext mock = PowerMockito.mock(AppContext.class);
        PowerMockito.when(mock.getInstance(info)).thenReturn(new ArrayList());
        //
        InfoAwareProvider<List> provider = new InfoAwareProvider<List>(info);
        //
        try {
            provider.get();
            assert false;
        } catch (Exception e) {
            assert "has not been initialized".equalsIgnoreCase(e.getMessage());
        }
        //
        provider.setAppContext(mock);
        provider.toString();
        //
        assert provider.get() instanceof ArrayList;
    }
    //
    @Test
    public void providerTest5() throws Throwable {
        //
        final ThreadSingleProvider<Object> listProvider = new ThreadSingleProvider<Object>(new Provider<Object>() {
            @Override
            public Object get() {
                return new Object();
            }
        });
        final ArrayList<Object> result = new ArrayList<Object>();
        //
        //
        final AtomicInteger atomicInteger = new AtomicInteger();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                result.add(listProvider.get());
                if (listProvider.get() == listProvider.get()) { // 线程内单例
                    atomicInteger.incrementAndGet();
                }
            }
        };
        //
        runnable.run();
        new Thread(runnable).start();
        Thread.sleep(500);
        //
        assert atomicInteger.get() == 2;
        assert result.get(0) != result.get(1); // 跨线程不相等
        //
        listProvider.toString();
    }
}
