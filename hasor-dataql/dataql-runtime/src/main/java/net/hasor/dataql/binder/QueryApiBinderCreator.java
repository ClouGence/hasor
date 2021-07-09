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
package net.hasor.dataql.binder;
import net.hasor.core.ApiBinder;
import net.hasor.core.BindInfo;
import net.hasor.core.binder.ApiBinderCreator;
import net.hasor.core.binder.ApiBinderWrap;
import net.hasor.dataql.DataQL;
import net.hasor.dataql.DataQL.ConfigOption;
import net.hasor.dataql.FragmentProcess;
import net.hasor.dataql.Udf;
import net.hasor.dataql.service.DataQLContext;
import net.hasor.dataql.service.DefaultFinder;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * DataQL 扩展接口。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class QueryApiBinderCreator implements ApiBinderCreator<QueryApiBinder> {
    @Override
    public QueryApiBinder createBinder(final ApiBinder apiBinder) {
        return new QueryApiBinderImpl(apiBinder);
    }

    private static class QueryApiBinderImpl extends ApiBinderWrap implements QueryApiBinder {
        private final DataQLContext dqlConfig;
        private final DefaultFinder defaultFinder;

        private QueryApiBinderImpl(ApiBinder apiBinder) {
            super(apiBinder);
            this.defaultFinder = new DefaultFinder();
            this.dqlConfig = new DataQLContext(this.defaultFinder);
            //
            apiBinder.bindType(DataQLContext.class).toInstance(this.dqlConfig);
            apiBinder.bindType(DataQL.class).toInstance(this.dqlConfig);
            apiBinder.lazyLoad(appContext -> {
                List<BindInfo<FragmentProcess>> fragmentInfos = appContext.findBindingRegister(FragmentProcess.class);
                for (BindInfo<FragmentProcess> fragmentInfo : fragmentInfos) {
                    Supplier<? extends FragmentProcess> fragmentProcess = appContext.getProvider(fragmentInfo);
                    this.defaultFinder.addFragmentProcess(fragmentInfo.getBindName().toLowerCase(), fragmentProcess);
                }
                List<UdfSourceDefine> udfSourceInfos = appContext.findBindingBean(UdfSourceDefine.class);
                for (UdfSourceDefine udfSourceInfo : udfSourceInfos) {
                    Supplier<Map<String, Udf>> udfResource = udfSourceInfo.getUdfResource(this.defaultFinder);
                    this.dqlConfig.addShareVarValue(udfSourceInfo.getVarName(), udfResource);
                }
            });
        }

        @Override
        public String[] getHints() {
            return this.dqlConfig.getHints();
        }

        @Override
        public Object getHint(String optionKey) {
            return this.dqlConfig.getHint(optionKey);
        }

        @Override
        public void removeHint(String optionKey) {
            this.dqlConfig.removeHint(optionKey);
        }

        @Override
        public void setHint(String hintName, String value) {
            this.dqlConfig.setHint(hintName, value);
        }

        @Override
        public void setHint(String hintName, Number value) {
            this.dqlConfig.setHint(hintName, value);
        }

        @Override
        public void setHint(String hintName, boolean value) {
            this.dqlConfig.setHint(hintName, value);
        }

        @Override
        public void configOption(ConfigOption optionKey, Object value) {
            this.dqlConfig.configOption(optionKey, value);
        }

        @Override
        public <T> QueryApiBinder addShareVar(String name, Supplier<T> provider) {
            this.dqlConfig.addShareVar(name, provider);
            return this;
        }

        @Override
        public <T extends FragmentProcess> QueryApiBinder bindFragment(String fragmentType, Supplier<T> provider) {
            this.defaultFinder.addFragmentProcess(fragmentType, provider);
            return this;
        }
    }
}