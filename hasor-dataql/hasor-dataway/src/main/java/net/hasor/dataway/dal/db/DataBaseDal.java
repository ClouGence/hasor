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
package net.hasor.dataway.dal.db;
import net.hasor.core.AppContext;
import net.hasor.core.Init;
import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.hasor.dataway.dal.ApiDataAccessLayer;
import net.hasor.dataway.dal.EntityDef;
import net.hasor.dataway.dal.FieldDef;
import net.hasor.dataway.dal.QueryCondition;
import net.hasor.db.jdbc.ConnectionCallback;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * DAO 层接口
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-06-03
 */
@Singleton
public class DataBaseDal implements ApiDataAccessLayer {
    protected static Logger              logger = LoggerFactory.getLogger(DataBaseDal.class);
    @Inject
    private          InterfaceInfoDal    infoDal;
    @Inject
    private          InterfaceReleaseDal releaseDal;
    @Inject
    private          AppContext          appContext;

    @Init
    public void init() throws SQLException {
        JdbcTemplate jdbcTemplate = this.appContext.getInstance(JdbcTemplate.class);
        if (jdbcTemplate == null) {
            throw new IllegalStateException("jdbcTemplate is not init.");
        }
        //
        String dbType = null;//appContext.getEnvironment().getVariable("HASOR_DATAQL_DATAWAY_FORCE_DBTYPE");
        if (StringUtils.isBlank(dbType)) {
            dbType = jdbcTemplate.execute((ConnectionCallback<String>) con -> {
                String jdbcUrl = con.getMetaData().getURL();
                String jdbcDriverName = con.getMetaData().getDriverName();
                return JdbcUtils.getDbType(jdbcUrl, jdbcDriverName);
            });
        }
        if (dbType == null) {
            throw new IllegalStateException("unknown dbType.");
        }
        //
        this.infoDal.dbType = dbType;
        this.releaseDal.dbType = dbType;
        logger.info("dataway dbType is {}", dbType);
    }

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
        long timeMillis = System.currentTimeMillis();
        int nextInt = new Random(timeMillis).nextInt();
        String s = Integer.toString(nextInt, 24);
        if (s.length() > 4) {
            s = s.substring(0, 4);
        } else {
            s = StringUtils.rightPad(s, 4, "0");
        }
        //
        String newId = Long.toString(timeMillis, 24) + s;
        return ((EntityDef.INFO == objectType) ? "i_" : "r_") + newId;
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