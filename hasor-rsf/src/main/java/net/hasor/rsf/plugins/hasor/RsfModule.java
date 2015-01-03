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
import net.hasor.core.ApiBinder;
import net.hasor.core.EventContext;
import net.hasor.core.Module;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.adapter.AbstractRsfContext;
import net.hasor.rsf.plugins.local.LocalPrefPlugin;
import net.hasor.rsf.plugins.qps.QPSPlugin;
/**
 * Rsf 制定 Hasor Module。
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class RsfModule implements Module {
    public final void loadModule(ApiBinder apiBinder) throws Throwable {
        final AbstractRsfContext rsfContext = null;
        EventContext eventContext = apiBinder.getEnvironment().getEventContext();
        //
        apiBinder.bindType(RsfContext.class, rsfContext);
        //
        rsfBinder.bindFilter("QPS", new QPSPlugin());
        rsfBinder.bindFilter("LocalPre", new LocalPrefPlugin());
        //
        this.loadModule(new RsfApiBinder(apiBinder, rsfContext));
    }
    //
    public abstract void loadModule(RsfApiBinder apiBinder) throws Throwable;
}