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
package net.hasor.plugins.datachain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.more.util.ClassUtils;
/**
 * 数据对象转换工具，提供 A 类型对象到 B 类型对象转换功能。并使开发者在转换过程中可以实现更加高级别的控制协调能力。
 * 使用场景：
 *  如，DO 到 TO or VO，以及各种 O 之间的数据转换，这些数据对象随着业务和团队组成，无法简单的 Bean copy 去解决数据转换问题。
 *  另外，随着业务模型的复杂度增加，类型转换可能会遍布应用程序的各个角落，DataChain可以帮你归类整理类型转换。使其可以从用复用。
 * @version : 2016年5月6日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class DataChainContext<I, O> implements ConvertChain<I, O> {
    private final List<DataFilter<I, O>>        dataFilterList = new ArrayList<DataFilter<I, O>>();
    private final Map<String, DataFilter<I, O>> dataFilterMap  = new HashMap<String, DataFilter<I, O>>();
    private final DataCreater<I>                inputCreater;
    private final DataCreater<O>                outputCreater;
    //
    public DataChainContext() {
        this(null, null);
    }
    public DataChainContext(DataCreater<I> inputCreater, DataCreater<O> outputCreater) {
        this.inputCreater = inputCreater;
        this.outputCreater = outputCreater;
    }
    //
    protected I newInputObject() throws Throwable {
        return (I) ClassUtils.getSuperClassGenricType(this.getClass(), 0).newInstance();
    }
    protected O newOutputObject() throws Throwable {
        return (O) ClassUtils.getSuperClassGenricType(this.getClass(), 1).newInstance();
    }
    //
    public void addDataFilter(String key, DataFilter<I, O> dataFilter) {
        if (key == null || dataFilter == null) {
            return;
        }
        if (this.dataFilterMap.containsKey(key)) {
            return;
        }
        this.dataFilterList.add(0, dataFilter);
        this.dataFilterMap.put(key, dataFilter);
    }
    //
    private ConvertChain<I, O> forwardChain() {
        final DataFilter<I, O>[] dataFilterArrays = this.dataFilterList.toArray(new DataFilter[this.dataFilterList.size()]);
        final DataCreater<O> defaultOutputCreater = new DataCreater<O>() {
            public O newObject() throws Throwable {
                return newOutputObject();
            }
        };
        return new ConvertChain<I, O>() {
            @Override
            public O doChain(I inputData) throws Throwable {
                return this.doChain(inputData, null);
            }
            @Override
            public O doChain(I inputData, O defaultOut) throws Throwable {
                DataCreater<O> outCreater = (defaultOut != null) ? new InnerDataCreater<O>(defaultOut) : outputCreater != null ? outputCreater : defaultOutputCreater;
                InnerDataFilterChain<I, O> innerChain = new InnerDataFilterChain<I, O>(new InnerDataCreater<I>(inputData), outCreater);
                final InnerDataFilterHandler<I, O> handler = new InnerDataFilterHandler<I, O>(dataFilterArrays, innerChain);
                return handler.doForward(new InnerDataDomain<I>(inputData));
            }
        };
    }
    //
    public O doChain(I inputData) throws Throwable {
        return this.forwardChain().doChain(inputData);
    }
    public O doChain(I inputData, final O defaultOut) throws Throwable {
        return this.forwardChain().doChain(inputData, defaultOut);
    }
    //
    public ConvertChain<O, I> reverse() {
        final DataFilter<I, O>[] dataFilterArrays = this.dataFilterList.toArray(new DataFilter[this.dataFilterList.size()]);
        final DataCreater<I> defaultInputCreater = new DataCreater<I>() {
            public I newObject() throws Throwable {
                return newInputObject();
            }
        };
        return new ConvertChain<O, I>() {
            @Override
            public I doChain(O inputData) throws Throwable {
                return this.doChain(inputData, null);
            }
            @Override
            public I doChain(O inputData, I defaultOut) throws Throwable {
                DataCreater<I> outCreater = (defaultOut != null) ? new InnerDataCreater<I>(defaultOut) : inputCreater != null ? inputCreater : defaultInputCreater;
                InnerDataFilterChain<I, O> innerChain = new InnerDataFilterChain<I, O>(outCreater, new InnerDataCreater<O>(inputData));
                final InnerDataFilterHandler<I, O> handler = new InnerDataFilterHandler<I, O>(dataFilterArrays, innerChain);
                return handler.doBackward(new InnerDataDomain<O>(inputData));
            }
        };
    }
}