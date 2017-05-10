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
package net.hasor.data.ql.ctx;
import net.hasor.core.ApiBinder;
import net.hasor.core.BindInfo;
import net.hasor.core.Hasor;
import net.hasor.core.Provider;
import net.hasor.core.binder.ApiBinderCreater;
import net.hasor.core.binder.ApiBinderWrap;
import net.hasor.data.ql.GraphApiBinder;
import net.hasor.data.ql.UDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * DataQL 扩展接口。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class GraphQLApiBinderCreater implements ApiBinderCreater {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public ApiBinder createBinder(final ApiBinder apiBinder) {
        return new GraphApiBinderImpl(apiBinder);
    }
    private static class GraphApiBinderImpl extends ApiBinderWrap implements GraphApiBinder {
        protected Logger logger = LoggerFactory.getLogger(getClass());
        public GraphApiBinderImpl(ApiBinder apiBinder) {
            super(apiBinder);
        }
        //
        @Override
        public GraphApiBinder addUDF(String name, Class<? extends UDF> udfType) {
            return this.addUDF(name, bindType(UDF.class).uniqueName().to(udfType).toInfo());
        }
        @Override
        public GraphApiBinder addUDF(String name, UDF dataUDF) {
            return this.addUDF(name, bindType(UDF.class).uniqueName().toInstance(dataUDF).toInfo());
        }
        @Override
        public GraphApiBinder addUDF(String name, Provider<? extends UDF> udfProvider) {
            return this.addUDF(name, bindType(UDF.class).uniqueName().toProvider(udfProvider).toInfo());
        }
        @Override
        public GraphApiBinder addUDF(String name, BindInfo<? extends UDF> udfInfo) {
            UDFDefine define = Hasor.autoAware(getEnvironment(), new UDFDefine(name, udfInfo));
            this.bindType(UDFDefine.class).uniqueName().toInstance(define);
            return this;
        }
    }
}