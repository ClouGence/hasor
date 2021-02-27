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
import net.hasor.dataql.*;
import net.hasor.dataql.domain.DataModel;
import net.hasor.dataql.domain.DomainHelper;
import net.hasor.dataql.parser.location.RuntimeLocation;
import net.hasor.dataql.runtime.QueryRuntimeException;
import net.hasor.utils.ExceptionUtils;

/**
 * 栈数据
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2019-11-22
 */
public class RefCall {
    private final RuntimeLocation location;
    private final boolean         autoUnwrap;
    private final Udf             refCall;

    public RefCall(RuntimeLocation location, boolean autoUnwrap, Udf refCall) {
        this.location = location;
        this.autoUnwrap = autoUnwrap;
        this.refCall = refCall;
    }

    public Object invokeMethod(Object[] paramArrays, Hints optionSet, Finder finder) throws DataQueryException {
        try {
            Object[] objects = paramArrays.clone();
            if (this.autoUnwrap) {
                for (int i = 0; i < objects.length; i++) {
                    if (objects[i] instanceof DataModel) {
                        objects[i] = ((DataModel) objects[i]).unwrap();
                    }
                }
            }
            Object result = this.refCall.call(optionSet, objects);
            if (result instanceof UdfSource) {
                result = ((UdfSource) result).getUdfResource(finder).get();
            }
            return DomainHelper.convertTo(result);
        } catch (Throwable e) {
            if (e instanceof DataQueryException) {
                throw (DataQueryException) e;
            }
            throw ExceptionUtils.toRuntimeException(e, throwable -> {
                String message = e.getClass().getName() + ": " + throwable.getLocalizedMessage();
                return new QueryRuntimeException(location, message, throwable);
            });
        }
    }
}
