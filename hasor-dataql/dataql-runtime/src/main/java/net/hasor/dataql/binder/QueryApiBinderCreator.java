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
import net.hasor.dataql.Finder;
import net.hasor.dataql.QueryApiBinder;
import net.hasor.utils.StringUtils;

import java.util.function.Supplier;

/**
 * DataQL 扩展接口。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class QueryApiBinderCreator implements ApiBinderCreator<QueryApiBinder> {
    @Override
    public QueryApiBinder createBinder(final ApiBinder apiBinder) {
        return new QueryApiBinderImpl(true, null, apiBinder);
    }

    private static class QueryApiBinderImpl extends ApiBinderWrap implements QueryApiBinder {
        private InnerDataQLImpl innerDqlConfig = new InnerDataQLImpl();

        private QueryApiBinderImpl(boolean isDefault, String contextName, ApiBinder apiBinder) {
            super(apiBinder);
            if (!isDefault) {
                if (StringUtils.isBlank(contextName)) {
                    throw new IllegalArgumentException("the context name is empty.");
                }
                apiBinder.bindType(InnerDataQLImpl.class).nameWith(contextName).toInstance(this.innerDqlConfig);
                apiBinder.bindType(DataQL.class).nameWith(contextName).toInstance(this.innerDqlConfig);
            } else {
                apiBinder.bindType(InnerDataQLImpl.class).toInstance(this.innerDqlConfig);
                apiBinder.bindType(DataQL.class).toInstance(this.innerDqlConfig);
            }
            //
            HasorUtils.pushStartListener(getEnvironment(), (EventListener<AppContext>) (event, eventData) -> {
                innerDqlConfig.initConfig(eventData);
            });
        }

        public QueryApiBinderImpl(String contextName, ApiBinder apiBinder) {
            this(false, contextName, apiBinder);
        }

        @Override
        public QueryApiBinder isolation(String contextName) {
            return new QueryApiBinderImpl(contextName, this);
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
        public <T> QueryApiBinder addShareVar(String name, Supplier<T> provider) {
            ShareVar shareVar = new ShareVar(name, provider);
            bindType(ShareVar.class).nameWith(name).toInstance(shareVar);
            return this;
        }

        @Override
        public QueryApiBinder bindFinder(Supplier<? extends Finder> finderSupplier) {
            this.innerDqlConfig.setFinder(finderSupplier);
            return this;
        }
    }
}