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
import net.hasor.core.Hasor;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.address.AddressBucket;
import net.hasor.rsf.address.RouteTypeEnum;
import net.hasor.rsf.rpc.context.DefaultRsfEnvironment;
import net.hasor.rsf.utils.IOUtils;
import net.hasor.rsf.utils.ResourcesUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
/**
 *
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class BucketTest {
    @Test
    public void saveToZipTest() throws URISyntaxException, IOException {
        DefaultRsfEnvironment rsfEnv = new DefaultRsfEnvironment(Hasor.createAppContext().getEnvironment());
        String serviceID = "tttt";
        BuildBucketBuild buildBucket = new BuildBucketBuild(serviceID, rsfEnv).invoke();
        AddressBucket bucket = buildBucket.getBucket();
        //
        String flowBody = IOUtils.readToString(ResourcesUtils.getResourceAsStream("/flow-control/full-flow.xml"));
        bucket.updateFlowControl(flowBody);
        //
        String scriptBody1 = IOUtils.readToString(ResourcesUtils.getResourceAsStream("/rule-script/service-level.groovy"));
        bucket.updateRoute(RouteTypeEnum.ServiceLevel, scriptBody1);
        //
        String scriptBody2 = IOUtils.readToString(ResourcesUtils.getResourceAsStream("/rule-script/method-level.groovy"));
        bucket.updateRoute(RouteTypeEnum.MethodLevel, scriptBody2);
        //
        String scriptBody3 = IOUtils.readToString(ResourcesUtils.getResourceAsStream("/rule-script/args-level.groovy"));
        bucket.updateRoute(RouteTypeEnum.ArgsLevel, scriptBody3);
        //
        File outFile = new File(rsfEnv.getPluginDir(BucketTest.class), serviceID + ".zip");
        outFile.getParentFile().mkdirs();
        FileOutputStream out = new FileOutputStream(outFile, false);
        bucket.saveToZip(out);
        out.flush();
        out.close();
    }
    @Test
    public void readFormZipTest() throws IOException, URISyntaxException {
        this.saveToZipTest();
        //
        DefaultRsfEnvironment rsfEnv = new DefaultRsfEnvironment(Hasor.createAppContext().getEnvironment());
        String serviceID = "tttt";
        BuildBucketBuild buildBucket = new BuildBucketBuild(serviceID, rsfEnv).invoke();
        AddressBucket bucket = buildBucket.getBucket();
        //
        File inFile = new File(rsfEnv.getPluginDir(BucketTest.class), serviceID + ".zip");
        FileInputStream in = new FileInputStream(inFile);
        bucket.readFromZip(in);
        in.close();
    }
    //
    @Test
    public void invalidAddressTest() throws IOException, InterruptedException, URISyntaxException {
        DefaultRsfEnvironment rsfEnv = new DefaultRsfEnvironment(Hasor.createAppContext().getEnvironment());
        String serviceID = "tttt";
        BuildBucketBuild buildBucket = new BuildBucketBuild(serviceID, rsfEnv).invoke();
        final AddressBucket bucket = buildBucket.getBucket();
        //
        Thread watcher = new Thread() {
            @Override
            public void run() {
                while (true) {
                    bucket.refreshAddress();
                    System.out.println(bucket.getAvailableAddresses().size());
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                    }
                }
            }
        };
        watcher.setDaemon(true);
        watcher.start();
        //
        Thread.sleep(2000);
        bucket.invalidAddress(new InterAddress("127.0.0.2", 8000, "etc2"), 500);
        //
        Thread.sleep(2000);
        watcher.stop();
    }
}