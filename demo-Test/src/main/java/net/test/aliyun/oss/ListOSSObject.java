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
import java.util.List;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.StartModule;
import net.test.aliyun.OSSModule;
import org.more.util.StringUtils;
import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.model.ListObjectsRequest;
import com.aliyun.openservices.oss.model.OSSObjectSummary;
import com.aliyun.openservices.oss.model.ObjectListing;
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
        OSSClient client = appContext.getInstance(OSSClient.class);
        ListObjectsRequest listQuery = new ListObjectsRequest("files-subtitle");
        //
        long index = 0;
        while (true) {
            ObjectListing listData = client.listObjects(listQuery);
            List<OSSObjectSummary> objSummary = listData.getObjectSummaries();
            for (OSSObjectSummary summary : objSummary) {
                index++;
                System.out.println(index + "\t from :" + summary.getKey());
                //
            }
            //
            listQuery.setMarker(listData.getNextMarker());
            if (StringUtils.isBlank(listData.getNextMarker())) {
                break;
            }
        }
    }
    public static void main(String[] args) {
        AppContext app = Hasor.createAppContext(new OSSModule(), new ListOSSObject());
        System.out.println("end");
    }
}