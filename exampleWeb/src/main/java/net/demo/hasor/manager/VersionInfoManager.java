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
package net.demo.hasor.manager;
import net.demo.hasor.core.Service;
import net.demo.hasor.domain.AppConstant;
import net.demo.hasor.domain.VersionInfoDO;
import net.demo.hasor.utils.LogUtils;
import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.hasor.db.jdbc.core.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
/**
 *
 * @version : 2016年1月10日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
@Service("versionMap")
public class VersionInfoManager {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Inject(AppConstant.DB_HSQL)
    private JdbcTemplate jdbcTemplate;
    //
    /**根据版本号，查询发布信息。*/
    public VersionInfoDO queryByVersion(String version) throws Exception {
        try {
            VersionInfoDO infoDO = null;
            String query = "select * from VersionInfo where version = ?";
            infoDO = jdbcTemplate.queryForObject(query, VersionInfoDO.class, version);
            return infoDO;
        } catch (Exception e) {
            logger.error(LogUtils.create("ERROR_999_0003").logException(e) //
                    .addString("version : queryByVersion error -> " + e.getMessage()).toJson());
            throw e;
        }
    }
    /**查询所有版本(根据版本号排序)*/
    public List<VersionInfoDO> queryListOrerByVersion() throws Exception {
        try {
            List<VersionInfoDO> infoListDO = null;
            String query = "select * from VersionInfo order by id asc";
            infoListDO = jdbcTemplate.queryForList(query, VersionInfoDO.class);
            return infoListDO;
        } catch (Exception e) {
            logger.error(LogUtils.create("ERROR_999_0003").logException(e) //
                    .addString("version : queryByVersion error -> " + e.getMessage()).toJson());
            throw e;
        }
    }
}