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
import net.hasor.core.AppContext;
import net.hasor.core.EventListener;
import net.hasor.core.HasorUtils;
import net.hasor.core.binder.ApiBinderCreator;
import net.hasor.core.binder.ApiBinderWrap;
import net.hasor.dataql.DataQL;
import net.hasor.dataql.DataQL.ConfigOption;
import net.hasor.dataql.Finder;
import net.hasor.dataql.FragmentProcess;
import net.hasor.dataql.QueryApiBinder;

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
        private final InnerDataQLImpl innerDqlConfig = new InnerDataQLImpl();

        private QueryApiBinderImpl(ApiBinder apiBinder) {
            super(apiBinder);
            apiBinder.bindType(InnerDataQLImpl.class).toInstance(this.innerDqlConfig);
            apiBinder.bindType(DataQL.class).toInstance(this.innerDqlConfig);
            //
            HasorUtils.pushStartListener(getEnvironment(), (EventListener<AppContext>) (event, eventData) -> {
                innerDqlConfig.initConfig(eventData);
            });
        }

        @Override
        public String[] getHints() {
            return this.innerDqlConfig.getHints();
        }

        @Override
        public Object getHint(String optionKey) {
            return this.innerDqlConfig.getHint(optionKey);
        }

        @Override
        public void removeHint(String optionKey) {
            this.innerDqlConfig.removeHint(optionKey);
        }

        @Override
        public void setHint(String hintName, String value) {
            this.innerDqlConfig.setHint(hintName, value);
        }

        @Override
        public void setHint(String hintName, Number value) {
            this.innerDqlConfig.setHint(hintName, value);
        }

        @Override
        public void setHint(String hintName, boolean value) {
            this.innerDqlConfig.setHint(hintName, value);
        }

        @Override
        public void configOption(ConfigOption optionKey, Object value) {
            this.innerDqlConfig.configOption(optionKey, value);
        }

        @Override
        public <T> QueryApiBinder addShareVar(String name, Supplier<T> provider) {
            this.innerDqlConfig.addShareVar(name, provider);
            return this;
        }

        @Override
        public QueryApiBinder bindFinder(Supplier<? extends Finder> finderSupplier) {
            this.innerDqlConfig.setFinder(finderSupplier);
            return this;
        }

        @Override
        public <T extends FragmentProcess> QueryApiBinder bindFragment(String fragmentType, Supplier<T> provider) {
            HasorUtils.pushStartListener(getEnvironment(), (EventListener<AppContext>) (event, eventData) -> {
                innerDqlConfig.addFragmentProcess(fragmentType, provider);
            });
            return this;
        }
    }
}