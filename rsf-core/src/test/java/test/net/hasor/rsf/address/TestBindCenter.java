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
package test.net.hasor.rsf.address;
import java.util.List;
import net.hasor.core.Provider;
import net.hasor.rsf.BindCenter;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.domain.ServiceDomain;
import org.more.util.StringUtils;
/**
 * 
 * @version : 2015年4月16日
 * @author 赵永春(zyc@hasor.net)
 */
public class TestBindCenter<T> implements BindCenter {
    private ServiceDomain<T> rsfBindInfo = null;
    private Class<T>         target      = null;
    private String           name        = null;
    //
    //
    public TestBindCenter(Class<T> target) {
        this.target = target;
        this.rsfBindInfo = new ServiceDomain<T>(target);
        this.rsfBindInfo.setBindName(target.getSimpleName());
    }
    @Override
    public RsfBinder getRsfBinder() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public <T> RsfBindInfo<T> getService(String serviceID) {
        if (StringUtils.equalsBlankIgnoreCase(serviceID, this.rsfBindInfo.getBindID()))
            return (RsfBindInfo<T>) rsfBindInfo;
        return null;
    }
    @Override
    public <T> RsfBindInfo<T> getService(Class<T> serviceType) {
        if (this.target == serviceType)
            return (RsfBindInfo<T>) rsfBindInfo;
        return null;
    }
    @Override
    public <T> RsfBindInfo<T> getService(String group, String name, String version) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public List<String> getServiceIDs() {
        return null;
    }
    @Override
    public void recoverService(RsfBindInfo<?> bindInfo) {
        // TODO Auto-generated method stub
    }
    @Override
    public <T> Provider<T> getProvider(RsfBindInfo<T> bindInfo) {
        // TODO Auto-generated method stub
        return null;
    }
}
