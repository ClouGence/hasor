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
package net.hasor.dataql.fx.db;
import net.hasor.dataql.Hints;
import net.hasor.dataql.Udf;
import net.hasor.dataql.UdfSourceAssembly;
import net.hasor.db.transaction.Propagation;
import net.hasor.db.transaction.TransactionTemplate;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * 数据库事务函数库。函数库引入 <code>import 'net.hasor.dataql.fx.db.TransactionUdfSource' as tran;</code>
 * @see Propagation
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-28
 */
@Singleton
public class TransactionUdfSource implements UdfSourceAssembly {
    @Inject
    private TransactionTemplate transactionTemplate;

    /**
     * 加入已有事务：REQUIRED
     * @see Propagation#REQUIRED
     */
    public Object required(final Udf udf, final Hints hints) throws Throwable {
        return this.transactionTemplate.execute(tranStatus -> {
            return udf.call(hints);
        }, Propagation.REQUIRED);
    }

    /**
     * 独立事务：REQUIRES_NEW
     * @see Propagation#REQUIRES_NEW
     */
    public Object requiresNew(final Udf udf, final Hints hints) throws Throwable {
        return this.transactionTemplate.execute(tranStatus -> {
            return udf.call(hints);
        }, Propagation.REQUIRES_NEW);
    }

    /**
     * 嵌套事务：NESTED
     * @see Propagation#NESTED
     */
    public Object nested(final Udf udf, final Hints hints) throws Throwable {
        return this.transactionTemplate.execute(tranStatus -> {
            return udf.call(hints);
        }, Propagation.NESTED);
    }

    /**
     * 跟随环境：SUPPORTS
     * @see Propagation#SUPPORTS
     */
    public Object supports(final Udf udf, final Hints hints) throws Throwable {
        return this.transactionTemplate.execute(tranStatus -> {
            return udf.call(hints);
        }, Propagation.SUPPORTS);
    }

    /**
     * 非事务方式：NOT_SUPPORTED
     * @see Propagation#NOT_SUPPORTED
     */
    public Object notSupported(final Udf udf, final Hints hints) throws Throwable {
        return this.transactionTemplate.execute(tranStatus -> {
            return udf.call(hints);
        }, Propagation.NOT_SUPPORTED);
    }

    /**
     * 排除事务：NEVER
     * @see Propagation#NEVER
     */
    public Object never(final Udf udf, final Hints hints) throws Throwable {
        return this.transactionTemplate.execute(tranStatus -> {
            return udf.call(hints);
        }, Propagation.NEVER);
    }

    /**
     * 要求环境中存在事务：MANDATORY
     * @see Propagation#MANDATORY
     */
    public Object tranMandatory(final Udf udf, final Hints hints) throws Throwable {
        return this.transactionTemplate.execute(tranStatus -> {
            return udf.call(hints);
        }, Propagation.MANDATORY);
    }
}