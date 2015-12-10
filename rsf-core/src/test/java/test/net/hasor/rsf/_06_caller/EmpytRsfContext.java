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
package test.net.hasor.rsf._06_caller;
import java.net.URI;
import java.util.List;
import net.hasor.core.Provider;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfClient;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.address.InterAddress;
/**
 * 
 * @version : 2015年12月9日
 * @author 赵永春(zyc@hasor.net)
 */
public class EmpytRsfContext implements RsfContext {
    @Override
    public void shutdown() {
        // TODO Auto-generated method stub
    }
    @Override
    public RsfSettings getSettings() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public RsfClient getRsfClient() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public <T> T getBean(RsfBindInfo<T> bindInfo) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public <T> Provider<T> getServiceProvider(RsfBindInfo<T> bindInfo) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public <T> RsfBindInfo<T> getServiceInfo(String serviceID) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public <T> RsfBindInfo<T> getServiceInfo(Class<T> serviceType) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public <T> RsfBindInfo<T> getServiceInfo(String group, String name, String version) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public List<String> getServiceIDs() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public RsfClient getRsfClient(String target) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public RsfClient getRsfClient(URI target) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public RsfClient getRsfClient(InterAddress target) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}
