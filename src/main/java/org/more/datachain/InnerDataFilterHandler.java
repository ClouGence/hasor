/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.more.datachain;
/**
 * 
 * @version : 2016年5月6日
 * @author 赵永春(zyc@hasor.net)
 */
class InnerDataFilterHandler<I, O> implements DataFilterChain<I, O> {
    private static final DataFilter[]   EMPTY_FILTER = new DataFilter[0];
    private final DataFilter<I, O>[]    filters;
    private int                         index;
    private final DataFilterChain<I, O> innerDataFilterChain;
    //
    public InnerDataFilterHandler(final DataFilter<I, O>[] filters, DataFilterChain<I, O> innerDataFilterChain) {
        this.index = -1;
        this.filters = (filters != null && filters.length != 0) ? filters : EMPTY_FILTER;
        this.innerDataFilterChain = innerDataFilterChain;
    }
    @Override
    public O doForward(Domain<I> domain) throws Throwable {
        this.index++;
        if (this.index < this.filters.length) {
            return this.filters[index].doForward(domain, this);
        } else {
            return this.innerDataFilterChain.doForward(domain);
        }
    }
    @Override
    public I doBackward(Domain<O> domain) throws Throwable {
        this.index++;
        if (this.index < this.filters.length) {
            return this.filters[index].doBackward(domain, this);
        } else {
            return this.innerDataFilterChain.doBackward(domain);
        }
    }
}