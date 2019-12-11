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
package net.hasor.dataql.extend.binder;
import net.hasor.core.ApiBinder;
import net.hasor.core.binder.ApiBinderCreater;
import net.hasor.core.binder.ApiBinderWrap;
import net.hasor.utils.resource.ResourceLoader;

/**
 * DataQL 扩展接口。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class DataApiBinderCreater implements ApiBinderCreater {
    @Override
    public ApiBinder createBinder(final ApiBinder apiBinder) {
        return new DataApiBinderImpl(apiBinder);
    }

    private static class DataApiBinderImpl extends ApiBinderWrap implements DataApiBinder {
        private InnerDqlConfig innerDqlConfig = new InnerDqlConfig();

        public DataApiBinderImpl(ApiBinder apiBinder) {
            super(apiBinder);
            apiBinder.bindType(InnerDqlConfig.class).toInstance(innerDqlConfig);
            apiBinder.bindType(DataQL.class).toInstance(innerDqlConfig);
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
        public DataApiBinder bindResourceLoader(ResourceLoader resourceLoader) {
            this.innerDqlConfig.setResourceLoader(resourceLoader);
            return this;
        }
    }
}