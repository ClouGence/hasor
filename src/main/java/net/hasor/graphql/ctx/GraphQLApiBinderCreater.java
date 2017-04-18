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
package net.hasor.graphql.ctx;
import net.hasor.core.*;
import net.hasor.core.binder.ApiBinderCreater;
import net.hasor.core.binder.ApiBinderWrap;
import net.hasor.core.utils.StringUtils;
import net.hasor.graphql.GraphApiBinder;
import net.hasor.graphql.GraphUDF;
import net.hasor.graphql.UDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
/**
 * GraphQL 扩展接口。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class GraphQLApiBinderCreater implements ApiBinderCreater {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public ApiBinder createBinder(final ApiBinder apiBinder) {
        return new GraphApiBinderImpl(apiBinder);
    }
}
class GraphApiBinderImpl extends ApiBinderWrap implements GraphApiBinder {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    public GraphApiBinderImpl(ApiBinder apiBinder) {
        super(apiBinder);
    }
    //
    //
    //
    @Override
    public GraphApiBinder addUDF(String name, Class<? extends UDF> udfType) {
        return this.addUDF(name, bindType(UDF.class).uniqueName().to(udfType).toInfo());
    }
    @Override
    public GraphApiBinder addUDF(String name, UDF udf) {
        return this.addUDF(name, bindType(UDF.class).uniqueName().toInstance(udf).toInfo());
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
    @Override
    public GraphApiBinder addUDF(Class<? extends UDF> udfType) {
        this.loadUDF(this, udfType);
        return this;
    }
    //
    @Override
    public void scanUDF() {
        this.scanUDF(new Matcher<Class<?>>() {
            @Override
            public boolean matches(Class<?> target) {
                return true;
            }
        });
    }
    @Override
    public void scanUDF(String... packages) {
        this.scanUDF(new Matcher<Class<?>>() {
            @Override
            public boolean matches(Class<?> target) {
                return true;
            }
        });
    }
    @Override
    public void scanUDF(Matcher<Class<?>> matcher, String... packages) {
        String[] defaultPackages = this.getEnvironment().getSpanPackage();
        String[] scanPackages = (packages == null || packages.length == 0) ? defaultPackages : packages;
        //
        Set<Class<?>> serviceSet = this.findClass(UDF.class, scanPackages);
        serviceSet = (serviceSet == null) ? new HashSet<Class<?>>() : new HashSet<Class<?>>(serviceSet);
        serviceSet.remove(UDF.class);
        if (serviceSet.isEmpty()) {
            logger.warn("scanUDF -> exit , not found any @GraphUDF.");
        }
        for (Class<?> type : serviceSet) {
            loadUDF(this, type);
        }
    }
    private void loadUDF(GraphApiBinder apiBinder, Class<?> udfType) {
        GraphUDF udfAnno = udfType.getAnnotation(GraphUDF.class);
        if (udfAnno == null) {
            return;
        }
        String udfName = udfAnno.value();
        if (StringUtils.isBlank(udfName)) {
            udfName = udfType.getName();
        }
        apiBinder.addUDF(udfName, (BindInfo<? extends UDF>) apiBinder.bindType(udfType).toInfo());
    }
}