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
import net.hasor.core.ApiBinder;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.graphql.GraphApiBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 提供 <code>GraphQL</code> 功能支持。
 * @version : 2013-9-13
 * @author 赵永春 (zyc@byshell.org)
 */
public class GraphQLModule implements Module {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        //
        GraphApiBinder graphApiBinder = apiBinder.tryCast(GraphApiBinder.class);
        if (graphApiBinder == null) {
            logger.error("GraphQL support failed.");
            return;
        }
        apiBinder.bindType(GraphContext.class).toInstance(//
                Hasor.autoAware(apiBinder.getEnvironment(), new GraphContext())//
        );
    }
}