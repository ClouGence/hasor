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
package net.hasor.dataql.runtime.mem;
import net.hasor.dataql.FragmentProcess;
import net.hasor.dataql.Hints;
import net.hasor.dataql.Udf;

/**
 * 代理 Fragment 使其成为 UDF.
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class RefFragmentCall implements Udf {
    private FragmentProcess fragmentProcess;

    public RefFragmentCall(FragmentProcess fragmentProcess) {
        this.fragmentProcess = fragmentProcess;
    }

    @Override
    public Object call(Hints readOnly, Object... params) throws Throwable {
        String fragmentString = params[params.length - 1].toString();
        Object[] fragmentParams = new Object[params.length - 1];
        System.arraycopy(params, 0, fragmentParams, 0, fragmentParams.length);
        return this.fragmentProcess.runFragment(readOnly, fragmentParams, fragmentString);
    }
}