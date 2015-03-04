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
package net.test.aliyun.oss;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.StartModule;
import net.hasor.db.jdbc.BatchPreparedStatementSetter;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.test.aliyun.OSSModule;
import net.test.hasor.db._07_datasource.warp.OneDataSourceWarp;
import org.more.util.StringUtils;
import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.model.ListObjectsRequest;
import com.aliyun.openservices.oss.model.OSSObjectSummary;
import com.aliyun.openservices.oss.model.ObjectListing;
import com.aliyun.openservices.oss.model.ObjectMetadata;
/**
 * 射手 数据遍历
 * @version : 2014年8月1日
 * @author 赵永春(zyc@hasor.net)
 */
public class ListOSSObject implements StartModule {
    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {}
    @Override
    public void onStart(AppContext appContext) throws Throwable {
        String bucketName = "files-subtitle";
        OSSClient client = appContext.getInstance(OSSClient.class);
        JdbcTemplate jdbc = appContext.getInstance(JdbcTemplate.class);
        ListObjectsRequest listQuery = new ListObjectsRequest(bucketName);
        listQuery.setMaxKeys(1000);
        jdbc.update("delete from `oss-subtitle-copy`");
        //
        long index = 0;
        while (true) {
            ObjectListing listData = client.listObjects(listQuery);
            List<OSSObjectSummary> objSummary = listData.getObjectSummaries();
            final ArrayList<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
            for (OSSObjectSummary summary : objSummary) {
                index++;
                System.out.println(index + "\t from :" + summary.getKey());
                //
                ObjectMetadata omd = client.getObjectMetadata(bucketName, summary.getKey());
                String contentDisposition = omd.getContentDisposition();
                String newKey = summary.getKey();
                newKey = newKey.substring(0, newKey.length() - ".rar".length()) + ".zip";
                contentDisposition = contentDisposition.substring(0, contentDisposition.length() - ".rar".length()) + ".zip";
                //
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("newKey", newKey);
                hashMap.put("contentDisposition", contentDisposition);
                dataList.add(hashMap);
                //
            }
            jdbc.batchUpdate("insert into `oss-subtitle-copy` (oss_key,files,ori_name,size,lastTime,doWork) values (?,null,?,-1,now(),0)", new BatchPreparedStatementSetter() {
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    Map<String,String> map =dataList.get(i);
                    ps.setString(1, map.get("newKey"));
                    ps.setString(2, map.get("contentDisposition"));
                }
                public int getBatchSize() {
                    return dataList.size();
                }
            });
            //
            //
            listQuery.setMarker(listData.getNextMarker());
            if (StringUtils.isBlank(listData.getNextMarker())) {
                break;
            }
        }
    }
    public static void main(String[] args) {
        AppContext app = Hasor.createAppContext("net/test/simple/db/jdbc-config.xml",//
                new OneDataSourceWarp(), new OSSModule(), new ListOSSObject());
        System.out.println("end");
    }
}