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
package net.hasor.dataway.dal.providers.db;
import net.hasor.core.*;
import net.hasor.dataway.config.DatawayUtils;
import net.hasor.dataway.dal.ApiDataAccessLayer;
import net.hasor.dataway.dal.EntityDef;
import net.hasor.dataway.dal.FieldDef;
import net.hasor.dataway.dal.QueryCondition;
import net.hasor.db.JdbcUtils;
import net.hasor.db.jdbc.ConnectionCallback;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 数据库存储层访问 ApiDataAccessLayer 接口实现。
 *   -- 利用数据库事务来实现集群情况下的数据一致性。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-09-11
 */
@Singleton
public class DataBaseApiDataAccessLayer implements ApiDataAccessLayer {
    protected static Logger              logger = LoggerFactory.getLogger(DataBaseApiDataAccessLayer.class);
    @Inject
    private          AppContext          appContext;
    private          InterfaceInfoDal    infoDal;
    private          InterfaceReleaseDal releaseDal;

    @Init
    public void init() throws SQLException {
        //
        InformationStorage informationStorage = this.appContext.getInstance(InformationStorage.class);
        JdbcTemplate jdbcTemplate = null;
        if (informationStorage != null) {
            jdbcTemplate = new JdbcTemplate(Objects.requireNonNull(informationStorage.getDataSource(), "informationStorage dataSource is null."));
        } else {
            jdbcTemplate = this.appContext.getInstance(JdbcTemplate.class);
        }
        //
        if (jdbcTemplate == null) {
            throw new IllegalStateException("jdbcTemplate is not init.");
        }
        //
        Settings settings = this.appContext.getInstance(Settings.class);
        String dbType = settings.getString("hasor.dataway.settings.dal_db_type");
        if (StringUtils.isBlank(dbType)) {
            dbType = jdbcTemplate.execute((ConnectionCallback<String>) con -> {
                String jdbcUrl = con.getMetaData().getURL();
                String jdbcDriverName = con.getMetaData().getDriverName();
                return JdbcUtils.getDbType(jdbcUrl, jdbcDriverName);
            });
        }
        if (dbType == null) {
            logger.warn("dataway dbType unknown.");
        }
        //
        String tablePrefix = settings.getString("hasor.dataway.settings.dal_db_table_prefix");
        tablePrefix = StringUtils.isBlank(tablePrefix) ? "" : tablePrefix;
        this.infoDal = new InterfaceInfoDal(jdbcTemplate, dbType, tablePrefix + "interface_info");
        this.releaseDal = new InterfaceReleaseDal(jdbcTemplate, dbType, tablePrefix + "interface_release");
        //
        logger.info("dataway dbType is {} ,table names = {}, {}", dbType, this.infoDal.getTableName(), this.releaseDal.getTableName());
    }

    @Override
    public Map<FieldDef, String> getObjectBy(EntityDef objectType, FieldDef indexKey, String indexValue) {
        try {
            if (EntityDef.INFO == objectType) {
                return this.infoDal.getObjectBy(indexKey, indexValue);
            } else {
                return this.releaseDal.getObjectBy(indexKey, indexValue);
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
    public String generateId(EntityDef objectType, String apiPath) {
        String newId = DatawayUtils.generateID();
        return ((EntityDef.INFO == objectType) ? "i_" : "r_") + newId;
    }

    @Override
    public boolean deleteObject(EntityDef objectType, String id) {
        try {
            if (EntityDef.INFO == objectType) {
                return this.infoDal.deleteObject(id);
            } else {
                return this.releaseDal.deleteObject(id);
            }
        } catch (SQLException e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }

    @Override
    public boolean updateObject(EntityDef objectType, String id, Map<FieldDef, String> newData) {
        try {
            if (EntityDef.INFO == objectType) {
                return this.infoDal.updateObject(id, newData);
            } else {
                return this.releaseDal.updateObject(id, newData);
            }
        } catch (SQLException e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }

    @Override
    public boolean createObject(EntityDef objectType, Map<FieldDef, String> newData) {
        try {
            if (EntityDef.INFO == objectType) {
                return this.infoDal.createObject(newData);
            } else {
                return this.releaseDal.createObject(newData);
            }
        } catch (SQLException e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }
}