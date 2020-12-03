/*
 * Copyright 2002-2010 the original author or authors.
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
package net.hasor.db.jdbc.lambda.query;
import net.hasor.db.jdbc.lambda.LambdaOperations;
import net.hasor.db.jdbc.lambda.LambdaOperations.NestedQuery;
import net.hasor.db.jdbc.lambda.segment.MergeSqlSegment;
import net.hasor.db.jdbc.lambda.segment.Segment;

import java.util.function.Consumer;

import static net.hasor.db.jdbc.lambda.segment.SqlKeyword.LEFT;
import static net.hasor.db.jdbc.lambda.segment.SqlKeyword.RIGHT;

/**
 * 提供 lambda 方式生成 SQL。
 * @version : 2020-10-27
 * @author 赵永春 (zyc@hasor.net)
 */
class NestedQueryWrapper<T> extends AbstractCompareQuery<T, NestedQuery<T>> implements NestedQuery<T> {
    public NestedQueryWrapper(LambdaQueryWrapper<T> lambdaQuery) {
        super(lambdaQuery.exampleType(), lambdaQuery.getJdbcOperations(), lambdaQuery.dbType, lambdaQuery.dialect);
        this.queryTemplate = lambdaQuery.queryTemplate;
        this.paramNameSeq = lambdaQuery.paramNameSeq;
        this.queryParam = lambdaQuery.queryParam;
    }

    @Override
    protected NestedQuery<T> getSelf() {
        return this;
    }

    @Override
    public LambdaOperations.AbstractNestedQuery<T, NestedQuery<T>> and(Consumer<LambdaOperations.AbstractNestedQuery<T, NestedQuery<T>>> lambda) {
        Segment andBody = () -> {
            lambda.accept(this);
            return "";
        };
        this.addCondition(new MergeSqlSegment(LEFT, andBody, RIGHT));
        return this;
    }

    @Override
    public LambdaOperations.AbstractNestedQuery<T, NestedQuery<T>> or(Consumer<LambdaOperations.AbstractNestedQuery<T, NestedQuery<T>>> lambda) {
        Segment orBody = () -> {
            lambda.accept(this);
            return "";
        };
        this.or();
        this.addCondition(new MergeSqlSegment(LEFT, orBody, RIGHT));
        return this;
    }
}