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
package net.hasor.dataway.config;
import net.hasor.core.AppContext;
import net.hasor.core.spi.AppContextAware;
import net.hasor.dataway.dal.ApiDataAccessLayer;
import net.hasor.dataway.dal.ApiDataAccessLayerCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * ApiDataAccessLayerCreator 的代理类。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-09-18
 */
class InnerApiDalCreator implements Supplier<ApiDataAccessLayer>, ApiDataAccessLayerCreator, AppContextAware {
    protected static Logger                                     logger = LoggerFactory.getLogger(InnerApiDalCreator.class);
    private          AppContext                                 appContext;
    private final    Class<? extends ApiDataAccessLayerCreator> dalCreatorType;
    private          ApiDataAccessLayer                         target;

    public InnerApiDalCreator(Class<?> dalCreatorType) {
        this.dalCreatorType = (Class<? extends ApiDataAccessLayerCreator>) dalCreatorType;
    }

    @Override
    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
    }

    @Override
    public ApiDataAccessLayer get() {
        if (this.target == null) {
            this.target = this.create(this.appContext);
        }
        return this.target;
    }

    @Override
    public ApiDataAccessLayer create(AppContext appContext) {
        logger.info("create '" + this.dalCreatorType.getName() + "'");
        return appContext.getInstance(this.dalCreatorType).create(appContext);
    }
}