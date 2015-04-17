/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package test.net.hasor.rsf.binder;
import java.io.IOException;
import java.net.URISyntaxException;
import net.hasor.core.setting.StandardContextSettings;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.RsfBinder.RegisterBuilder;
import net.hasor.rsf.RsfBinder.RegisterReference;
import net.hasor.rsf.binder.RsfBindCenter;
import net.hasor.rsf.rpc.context.DefaultRsfSettings;
import org.junit.Test;
/**
 * 
 * @version : 2015年4月17日
 * @author 赵永春(zyc@hasor.net)
 */
public class BinderTest {
    @Test
    public void bindeTest() throws IOException, URISyntaxException {
        DefaultRsfSettings rsfSettings = new DefaultRsfSettings(new StandardContextSettings());
        rsfSettings.refresh();
        TestRsfBindCenterContext tsf = new TestRsfBindCenterContext(rsfSettings, "etc2");
        RsfBindCenter center = tsf.getBindCenter();
        RsfBinder rsfBinder = center.getRsfBinder();
        //
        RegisterBuilder<?> builder = rsfBinder.rsfService(BinderTest.class)//
                .timeout(1000).group("Test")//
                .bindAddress("rsf://192.168.0.101:8000/etc2")//
                .bindAddress("rsf://192.168.0.102:8000/etc2")//
                .bindAddress("rsf://192.168.0.103:8000/etc2")//
                .bindAddress("rsf://192.168.1.3:8000/etc3")//
                .bindAddress("rsf://192.168.1.4:8000/etc3")//
                .bindAddress("rsf://192.168.1.5:8000/etc3");//
        //
        //
        System.err.println("register before");
        System.out.println("AddressPool : " + tsf.getAddressPool().allServicesSnapshot());
        System.out.println("BindCenter : " + tsf.getBindCenter().getServiceIDs());
        RegisterReference<?> ref = builder.register();
        //
        //
        System.err.println("unRegister before");
        System.out.println("AddressPool : " + tsf.getAddressPool().allServicesSnapshot());
        System.out.println("BindCenter : " + tsf.getBindCenter().getServiceIDs());
        ref.unRegister();
        //
        //
        System.err.println("unRegister after");
        System.out.println("AddressPool : " + tsf.getAddressPool().allServicesSnapshot());
        System.out.println("BindCenter : " + tsf.getBindCenter().getServiceIDs());
    }
}