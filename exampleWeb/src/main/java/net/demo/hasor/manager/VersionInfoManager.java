/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.demo.hasor.core.constant.DBConstant;
import net.demo.hasor.domain.VersionInfoDO;
import net.hasor.core.Inject;
import net.hasor.db.jdbc.core.JdbcTemplate;
/**
 * 
 * @version : 2016年1月10日
 * @author 赵永春(zyc@hasor.net)
 */
public class VersionInfoManager {
    protected Logger     logger = LoggerFactory.getLogger(getClass());
    @Inject(DBConstant.DB_HSQL)
    private JdbcTemplate jdbcTemplate;
    //
    /**根据版本号，查询发布信息。*/
    public VersionInfoDO queryByVersion(String version) {
        try {
            VersionInfoDO infoDO = null;
            String query = "select * from VersionInfo where version = ?";
            infoDO = jdbcTemplate.queryForObject(query, VersionInfoDO.class, version);
            return infoDO;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
}