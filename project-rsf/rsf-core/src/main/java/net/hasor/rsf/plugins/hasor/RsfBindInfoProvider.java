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
package net.hasor.rsf.plugins.hasor;
import net.hasor.core.AppContext;
import net.hasor.core.AppContextAware;
import net.hasor.core.Provider;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.constants.RsfException;
/**
 * RsfBindInfo 转  Provider。
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfBindInfoProvider<T> implements Provider<T>, AppContextAware {
    private RsfBindInfo<T> bindInfo;
    private RsfContext     rsfContext;
    //
    public RsfBindInfoProvider(RsfApiBinder apiBinder, RsfBindInfo<T> bindInfo) {
        this.bindInfo = bindInfo;
    }
    public void setAppContext(AppContext appContext) {
        this.rsfContext = appContext.getInstance(RsfContext.class);
    }
    public T get() {
        if (this.rsfContext == null)
            return null;
        try {
            T res = this.rsfContext.getRsfClient().wrapper(this.bindInfo, this.bindInfo.getBindType());
            return res;
        } catch (Throwable e) {
            throw new RsfException("wrapper remote services error. ", e);
        }
    }
}