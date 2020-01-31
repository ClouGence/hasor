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
package net.hasor.dataql.domain;
import net.hasor.dataql.Hints;
import net.hasor.dataql.Udf;
import net.hasor.dataql.runtime.HintsSet;

/**
 * 函数调用
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class UdfModel implements DataModel, Udf {
    private Udf udf = null;

    UdfModel(Udf udf) {
        this.udf = udf;
    }

    @Override
    public Udf asOri() {
        return this.udf;
    }

    @Override
    public Udf unwrap() {
        return this.udf;
    }

    /** 判断是否为 UdfModel 类型值 */
    public boolean isUdfModel() {
        return true;
    }

    /** UDF 的返回值必须是一个 对象或者数组 */
    public DataModel call(Object[] params) throws Throwable {
        return call(new HintsSet(), params);
    }

    @Override
    public DataModel call(Hints readOnly, Object... params) throws Throwable {
        return DomainHelper.convertTo(this.udf.call(readOnly, params));
    }
}