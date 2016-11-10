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
package net.hasor.core.provider;
import net.hasor.core.*;
/**
 * 用法：Hasor.autoAware(env,new InfoAwareProvider(...));
 * 注意事项：只可以在 AppContext init 期间使用。
 * @version : 2015年12月18日
 * @author 赵永春(zyc@hasor.net)
 */
public class InfoAwareProvider<T> implements Provider<T>, AppContextAware {
    private BindInfo<? extends T> info;
    private AppContext            appContext;
    public InfoAwareProvider(BindInfo<? extends T> info) {
        this.info = Hasor.assertIsNotNull(info);
    }
    @Override
    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
    }
    @Override
    public T get() {
        if (this.appContext != null && this.info != null) {
            return this.appContext.getInstance(this.info);
        }
        throw new IllegalStateException("has not been initialized");
    }
}