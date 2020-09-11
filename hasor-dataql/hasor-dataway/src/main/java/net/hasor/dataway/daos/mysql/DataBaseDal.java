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
package net.hasor.dataway.daos.mysql;
import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.hasor.dataway.daos.ApiDataAccessLayer;
import net.hasor.dataway.daos.EntityDef;
import net.hasor.dataway.daos.FieldDef;
import net.hasor.dataway.daos.QueryCondition;
import net.hasor.utils.ExceptionUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * DAO 层接口
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-06-03
 */
@Singleton
public class DataBaseDal implements ApiDataAccessLayer {
    @Inject
    private InterfaceInfoDal    infoDal;
    @Inject
    private InterfaceReleaseDal releaseDal;

    @Override
    public Map<FieldDef, String> getObjectBy(EntityDef objectType, FieldDef indexKey, String index) {
        try {
            if (EntityDef.INFO == objectType) {
                return this.infoDal.getObjectBy(indexKey, index);
            } else {
                return this.releaseDal.getObjectBy(indexKey, index);
            }
        } catch (SQLException e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }

    @Override
    public List<Map<FieldDef, String>> listObjectBy(EntityDef objectType, Map<QueryCondition, Object> conditions) {
        try {
            if (EntityDef.INFO == objectType) {
                return this.infoDal.listObjectBy(conditions);
            } else {
                return this.releaseDal.listObjectBy(conditions);
            }
        } catch (SQLException e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }

    @Override
    public String generateId(EntityDef objectType) {
        if (EntityDef.INFO == objectType) {
            return "i_" + Long.toString(System.currentTimeMillis(), 24);
        } else {
            return "r_" + Long.toString(System.currentTimeMillis(), 24);
        }
    }

    @Override
    public boolean deleteObjectBy(EntityDef objectType, FieldDef indexKey, String index) {
        try {
            if (EntityDef.INFO == objectType) {
                return this.infoDal.deleteObjectBy(indexKey, index);
            } else {
                return this.releaseDal.deleteObjectBy(indexKey, index);
            }
        } catch (SQLException e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }

    @Override
    public boolean updateObjectBy(EntityDef objectType, FieldDef indexKey, String index, Map<FieldDef, String> newData) {
        try {
            if (EntityDef.INFO == objectType) {
                return this.infoDal.updateObjectBy(indexKey, index, newData);
            } else {
                return this.releaseDal.updateObjectBy(indexKey, index, newData);
            }
        } catch (SQLException e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }

    @Override
    public boolean createObjectBy(EntityDef objectType, Map<FieldDef, String> newData) {
        try {
            if (EntityDef.INFO == objectType) {
                return this.infoDal.createObjectBy(newData);
            } else {
                return this.releaseDal.createObjectBy(newData);
            }
        } catch (SQLException e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }
}