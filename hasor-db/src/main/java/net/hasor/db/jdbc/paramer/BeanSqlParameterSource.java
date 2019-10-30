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
package net.hasor.db.jdbc.paramer;
import net.hasor.db.jdbc.SqlParameterSource;
import net.hasor.db.jdbc.core.ParameterDisposer;
import net.hasor.utils.BeanUtils;

import java.util.List;
import java.util.Objects;

/**
 *
 * @version : 2014-3-31
 * @author 赵永春 (zyc@hasor.net)
 */
public class BeanSqlParameterSource implements SqlParameterSource, ParameterDisposer {
    private Object       dataBean;
    private List<String> dataNames;

    public BeanSqlParameterSource(Object dataBean) {
        this.dataBean = Objects.requireNonNull(dataBean);
        this.dataNames = BeanUtils.getPropertysAndFields(dataBean.getClass());
    }

    @Override
    public boolean hasValue(final String paramName) {
        return this.dataNames.contains(paramName);
    }

    @Override
    public Object getValue(final String paramName) throws IllegalArgumentException {
        return BeanUtils.readPropertyOrField(this.dataBean, paramName);
    }

    @Override
    public void cleanupParameters() {
        if (this.dataBean instanceof ParameterDisposer) {
            ((ParameterDisposer) this.dataBean).cleanupParameters();
        }
    }
}