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
package test.net.hasor.rsf.functions;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.address.AddressBucket;
import net.hasor.rsf.address.AddressTypeEnum;
import net.hasor.rsf.rpc.context.DefaultRsfEnvironment;

import java.net.URISyntaxException;
import java.util.ArrayList;
/**
 *
 * @version : 2016年09月09日
 * @author 赵永春(zyc@hasor.net)
 */
public class BuildBucketBuild {
    private DefaultRsfEnvironment rsfEnv;
    private String                serviceID;
    private AddressBucket         bucket;
    public BuildBucketBuild(String serviceID, DefaultRsfEnvironment rsfEnv) {
        this.serviceID = serviceID;
        this.rsfEnv = rsfEnv;
    }
    public AddressBucket getBucket() {
        return bucket;
    }
    public BuildBucketBuild invoke() throws URISyntaxException {
        bucket = new AddressBucket(serviceID, rsfEnv);
        //
        ArrayList<InterAddress> dynamicList = new ArrayList<InterAddress>();
        dynamicList.add(new InterAddress("127.0.0.1", 8000, "etc2"));
        dynamicList.add(new InterAddress("127.0.0.2", 8000, "etc2"));
        dynamicList.add(new InterAddress("127.0.0.3", 8000, "etc2"));
        dynamicList.add(new InterAddress("127.0.0.4", 8000, "etc2"));
        bucket.newAddress(dynamicList, AddressTypeEnum.Dynamic);
        //
        ArrayList<InterAddress> staticList = new ArrayList<InterAddress>();
        staticList.add(new InterAddress("127.0.1.1", 8000, "etc2"));
        staticList.add(new InterAddress("127.0.2.2", 8000, "etc2"));
        staticList.add(new InterAddress("127.0.3.3", 8000, "etc2"));
        staticList.add(new InterAddress("127.0.4.4", 8000, "etc2"));
        bucket.newAddress(staticList, AddressTypeEnum.Static);
        return this;
    }
}