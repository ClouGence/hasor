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
package net.hasor.tconsole.launcher;
import net.hasor.core.ApiBinder;
import net.hasor.core.BindInfo;
import net.hasor.core.HasorUtils;
import net.hasor.core.binder.ApiBinderCreater;
import net.hasor.core.binder.ApiBinderWrap;
import net.hasor.tconsole.CommandExecutor;
import net.hasor.tconsole.ConsoleApiBinder;
/**
 * DataQL 扩展接口。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class ConsoleApiBinderCreater implements ApiBinderCreater {
    @Override
    public ApiBinder createBinder(final ApiBinder apiBinder) {
        return new ConsoleApiBinderImpl(apiBinder);
    }
    //
    private static class ConsoleApiBinderImpl extends ApiBinderWrap implements ConsoleApiBinder {
        public ConsoleApiBinderImpl(ApiBinder apiBinder) {
            super(apiBinder);
        }
        @Override
        public void addCommand(String[] names, BindInfo<? extends CommandExecutor> instructInfo) {
            if (names == null || names.length == 0) {
                throw new NullPointerException("command names undefined.");
            }
            ExecutorDefine define = HasorUtils.autoAware(getEnvironment(), new ExecutorDefine(names, instructInfo));
            this.bindType(ExecutorDefine.class).uniqueName().toInstance(define);
        }
    }
}