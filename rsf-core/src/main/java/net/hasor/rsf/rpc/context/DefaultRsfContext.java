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
package net.hasor.rsf.rpc.context;
import java.io.IOException;
import net.hasor.core.Settings;
import net.hasor.rsf.RsfSettings;
/**
 * 
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class DefaultRsfContext extends AbstractRsfContext {
    public DefaultRsfContext(Settings settings) throws IOException {
        this(null, new DefaultRsfSettings(settings));
    }
    public DefaultRsfContext(RsfSettings settings) throws IOException {
        this(null, new DefaultRsfSettings(settings));
    }
    public DefaultRsfContext(Object context, Settings settings) throws IOException {
        this(context, new DefaultRsfSettings(settings));
    }
    public DefaultRsfContext(Object context, RsfSettings settings) throws IOException {
        this.initContext(context, settings);
        //        this.bindCenter.bindFilter(InnerLocalWarpRsfFilter.class.getName(),//
        //                new InstanceProvider<InnerLocalWarpRsfFilter>(new InnerLocalWarpRsfFilter()));
    }
}